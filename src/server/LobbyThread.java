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
	private ArrayList<Thread> gameThreads;
	
	
	protected LobbyThread(GameType lobbyType) {
		this.lobbyType = lobbyType;
		playerList = new ArrayList<Player>();
		gameThreads = new ArrayList<Thread>();
	}

	@Override
	public void run() {
		int target;
		String playerListString;
		
		playerListString = "You're playing with: ";
		for (Player player : playerList) {
			playerListString = playerListString.concat(player.getName() + " ");
		}
		for (Player player : playerList) {
			// TODO: make separate thread for this?
			player.getWriter().println(playerListString);
		}
		
		
		target = (int)(Math.random() * 10) % 10;
		
		for (Player player : playerList) {
			gameThreads.add(new Thread(new GuessingGame(player, target, Server.MAX_GUESSES)));
		}
		for (Thread gameThread : gameThreads) {
			gameThread.start();
		}
		
		System.out.println("Waiting for players to make their guesses...");
		
		for (Thread gameThread : gameThreads) {
			try {
				gameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// TODO: broadcast this to the clients...
		for (Player player : playerList) {
			if (player.getGuesses() == -1) {
				System.out.println(player.getName() + " forfeited like a coward.");
			} else if (player.isWinner()) {
				System.out.println(player.getName() + " guessed the number in " + player.getGuesses() + " tries!");
			} else {
				System.out.println(player.getName() + " ran out of guesses.");
			}
		}
		
		// wait for clients to either play again or exit and update playerList accordingly
		// if playerList is empty, let the thread die
		
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
