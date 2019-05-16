package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import server.Server.GameType;

/**
 * takes a connecting Player's request: either a singleplayer or multiplayer game,
 * and passes their corresponding Socket connection to the correct lobby.
 * singleplayer: a new lobby with a player limit of 1. so, a new lobby is created for every singleplayer game.
 * multiplayer: either a new lobby with a player limit of 3, or an existing waiting lobby with less than 3 players.
 * @author Speck
 *
 */
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
				System.out.println("Found " + nextPlayer.getName() + " in playerQueue.");
				
				if (nextPlayer.getGameType() == GameType.SINGLEPLAYER) {
					LobbyThread spLobby = findLobby(GameType.SINGLEPLAYER);
					spLobby.addPlayer(nextPlayer);
					// it's a SP game, so start it now!
					new Thread(spLobby).start();
				} else {
					LobbyThread mpLobby = findLobby(GameType.MULTIPLAYER);
					mpLobby.addPlayer(nextPlayer);
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
	
	/**
	 * register the given connection as a player by prompting for their name and desired game type.
	 * 
	 * @param connection
	 */
	protected void registerPlayer(Socket connection) {
		new Thread(new RegisterThread(connection, this)).start();
	}
	
	protected void addToQueue(Player player) {
		playerQueue.add(player);
		System.out.println("Added " + player.getName() + " to playerQueue.");
		
	}
	
	private LobbyThread findLobby(GameType gameType) {
		LobbyThread lobby = null;
		
		if (gameType == GameType.SINGLEPLAYER) {
			// this player wants a singleplayer lobby.
			// we create a new lobby for every singleplayer game.
			lobby = new LobbyThread(GameType.SINGLEPLAYER);
			lobbies.add(lobby);
			System.out.println("Made new SP lobby.");
			return lobby;
		} else {
			// this player wants a multiplayer lobby.
			for (LobbyThread mpLobby : lobbies) {
				if (mpLobby.getLobbyType() == GameType.MULTIPLAYER &&
					mpLobby.getLobbySize() < Server.MAX_MP_PLAYERS) {
					// ensure the lobby is a multiplayer one AND there are free slots
					System.out.println("Found MP lobby: " + mpLobby.getLobbySize() + " players waiting.");
					return mpLobby;
				}
			}
			// couldn't find a multiplayer lobby with slots available. let's make one!
			lobby = new LobbyThread(GameType.MULTIPLAYER);
			lobbies.add(lobby);
			System.out.println("Made new MP lobby.");
			return lobby;
		}
	}

}
