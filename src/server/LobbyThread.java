package server;

import java.util.ArrayList;

import server.Server.GameType;

/**
 * holds a lobby of players and begins a game for them.
 * @author Speck
 *
 */
public class LobbyThread implements Runnable {

	private GameType lobbyType;
	private ArrayList<Player> playerList;
	
	protected LobbyThread() {
		lobbyType = null;
		playerList = new ArrayList<Player>();
	}

	@Override
	public void run() {
		if (lobbyType == GameType.SINGLEPLAYER) {
			if (this.getLobbySize() == 1) {
				// if the lobby is SINGLEPLAYER, there should always be one player before run() is invoked.
				// the PlayerMatchmakeThread should ensure this.
				// TODO: start a SINGLEPLAYER game
				System.out.println("SP lobby has started.");
			}
		} else if (lobbyType == GameType.MULTIPLAYER) {
			while (this.getLobbySize() < 3) {
				// wait for 3 players to join
			}
			// TODO: start a MULTIPLAYER game
		}

	}
	
	protected void addPlayer(Player player) {
		playerList.add(player);
	}
	
	protected void setLobbyType(GameType gameType) {
		lobbyType = gameType;
	}
	
	protected GameType getLobbyType() {
		return lobbyType;
	}
	
	protected int getLobbySize() {
		return playerList.size();
	}
	
	

}
