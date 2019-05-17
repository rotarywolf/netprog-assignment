package server;

import java.util.ArrayList;

import server.Server.GameType;

/**
 * holds a lobby of players and begins a game for them.
 * 
 * @author Speck
 *
 */
public class LobbyThread implements Runnable {

	private GameType lobbyType;
	private ArrayList<Player> playerList;
	private ArrayList<Thread> gameThreads;
	private PlayerMatchmakeThread matchmakingThread;

	protected LobbyThread(GameType lobbyType, PlayerMatchmakeThread matchmakingThread) {
		this.lobbyType = lobbyType;
		this.matchmakingThread = matchmakingThread;
		playerList = new ArrayList<Player>();
		gameThreads = new ArrayList<Thread>();
	}

	@Override
	public void run() {
		int target;
		String playerListString;
		String resultsString = "";

		playerListString = "You're playing with: ";
		for (Player player : playerList) {
			playerListString += player.getName() + " ";
		}
		broadcast(playerListString + System.lineSeparator());

		target = (int) (Math.random() * 10) % 10;

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

		for (Player player : playerList) {
			if (player.getGuesses() == -1) {
				resultsString += player.getName() + " forfeited like a coward." + System.lineSeparator();
			} else if (player.isWinner()) {
				resultsString += player.getName() + " guessed the number in " + player.getGuesses() + " tries!"
						+ System.lineSeparator();
			} else {
				resultsString += player.getName() + " ran out of guesses." + System.lineSeparator();
			}
		}
		resetPlayers();

		broadcast(resultsString + Server.HALT_ACTION + System.lineSeparator());
		System.out.println("Game has ended.");
		endOfGame();

	}

	/**
	 * sends a String message to all Players concurrently.
	 * 
	 * @param message message to send
	 */
	private void broadcast(String message) {
		ArrayList<Thread> broadcastThreads = new ArrayList<Thread>();

		for (Player player : playerList) {
			Thread broadcastThread = new Thread(new BroadcastThread(message, player));
			broadcastThreads.add(broadcastThread);
			broadcastThread.start();
		}

		for (Thread broadcastThread : broadcastThreads) {
			try {
				broadcastThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void broadcast(char character) {
		ArrayList<Thread> broadcastThreads = new ArrayList<Thread>();

		for (Player player : playerList) {
			Thread broadcastThread = new Thread(new BroadcastThread(character, player));
			broadcastThreads.add(broadcastThread);
			broadcastThread.start();
		}

		for (Thread broadcastThread : broadcastThreads) {
			try {
				broadcastThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void endOfGame() {
		ArrayList<Thread> endGameThreads = new ArrayList<Thread>();

		for (Player player : playerList) {
			Thread endGameThread = new Thread(new EndGameThread(player, matchmakingThread));
			endGameThreads.add(endGameThread);
			endGameThread.start();
		}

		// no need to join these threads, as the clients will be leaving the lobby
		// anyway.
	}

	protected void addPlayer(Player player) {
		playerList.add(player);
	}

	/**
	 * sets a Player's parameters back to the default, the same as before they
	 * played.
	 */
	protected void resetPlayers() {
		for (Player player : playerList) {
			player.resetGuesses();
			player.resetWinner();
		}
	}

	protected GameType getLobbyType() {
		return lobbyType;
	}

	protected int getLobbySize() {
		return playerList.size();
	}

}
