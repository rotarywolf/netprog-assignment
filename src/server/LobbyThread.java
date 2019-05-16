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
	private static final int MAX_GUESSES = 4;
	
	protected LobbyThread(GameType lobbyType) {
		this.lobbyType = lobbyType;
		playerList = new ArrayList<Player>();
	}

	@Override
	public void run() {
		
		// TODO: run the game!
		
	}
	
	protected void addPlayer(Player player) {
		playerList.add(player);
	}
	
	protected GameType getLobbyType() {
		return lobbyType;
	}
	
	protected int getLobbySize() {
		return playerList.size();
	}
	
	

}
