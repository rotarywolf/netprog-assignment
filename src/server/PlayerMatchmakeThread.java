package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import server.Server.GameType;

public class PlayerMatchmakeThread implements Runnable {

	private LinkedList<Player> playerQueue;
	private ArrayList<LobbyThread> lobbies;

	protected PlayerMatchmakeThread() {
		playerQueue = new LinkedList<Player>();
		lobbies = new ArrayList<LobbyThread>();
	}

	@Override
	public void run() {
		while (true) {
			try {
				// grab the next player in line
				Player nextPlayer = playerQueue.removeFirst();

				if (nextPlayer.getGameType() == GameType.SINGLEPLAYER) {
					LobbyThread spLobby = findLobby(GameType.SINGLEPLAYER);
					spLobby.addPlayer(nextPlayer);
					Server.LOGGER
							.info("Sent " + nextPlayer.getName() + " to a " + nextPlayer.getGameType() + " lobby.");
					// it's a SP game, so start it now!
					new Thread(spLobby).start();
				} else {
					LobbyThread mpLobby = findLobby(GameType.MULTIPLAYER);
					mpLobby.addPlayer(nextPlayer);
					Server.LOGGER
							.info("Sent " + nextPlayer.getName() + " to a " + nextPlayer.getGameType() + " lobby.");
					if (mpLobby.getLobbySize() == Server.MAX_MP_PLAYERS) {
						// we've filled the lobby. start it!
						new Thread(mpLobby).start();
					}
				}
			} catch (NoSuchElementException e) {
				// that's fine, keep going until a player is added to the queue.
			}
		}
	}

	protected void registerPlayer(Socket connection) {
		new Thread(new RegisterThread(connection, this)).start();
	}

	protected void addToQueue(Player player) {
		playerQueue.add(player);
		Server.LOGGER.info("Added " + player.getName() + " to playerQueue.");

	}

	private LobbyThread findLobby(GameType gameType) {
		LobbyThread lobby = null;

		if (gameType == GameType.SINGLEPLAYER) {
			// this player wants a singleplayer lobby.
			// we create a new lobby for every singleplayer game.
			lobby = new LobbyThread(GameType.SINGLEPLAYER, this);
			lobbies.add(lobby);
			Server.LOGGER.info("Created new " + GameType.SINGLEPLAYER + " lobby.");
			return lobby;
		} else {
			// this player wants a multiplayer lobby.
			for (LobbyThread mpLobby : lobbies) {
				if (mpLobby.getLobbyType() == GameType.MULTIPLAYER && mpLobby.getLobbySize() < Server.MAX_MP_PLAYERS) {
					// ensure the lobby is a multiplayer one AND there are free slots
					System.out.println("Found MP lobby: " + mpLobby.getLobbySize() + " players waiting.");
					Server.LOGGER.info("Found " + GameType.MULTIPLAYER + " lobby: " + mpLobby.getLobbySize()
							+ " players waiting.");
					return mpLobby;
				}
			}
			// couldn't find a multiplayer lobby with slots available. let's make one!
			lobby = new LobbyThread(GameType.MULTIPLAYER, this);
			lobbies.add(lobby);
			Server.LOGGER.info("Created new " + GameType.MULTIPLAYER + " lobby.");
			return lobby;
		}
	}

}
