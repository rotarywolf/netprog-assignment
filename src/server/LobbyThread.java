package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
		Server.LOGGER.info("Started " + lobbyType + " lobby with ID " + Thread.currentThread().getId() + ".");

		// broadcast the player list to everyone, and the maximum guesses.
		playerListString = "You're playing with: ";
		for (Player player : playerList) {
			playerListString += player.getName() + " ";
		}
		playerListString += ". You get " + Server.MAX_GUESSES + " guesses!";
		broadcast(playerListString + System.lineSeparator());

		// generate the number for everyone to guess!
		target = (int) (Math.random() * 10) % 10;

		// begin the game for all players in this lobby.
		for (Player player : playerList) {
			gameThreads.add(new Thread(new GuessingGame(player, target, Server.MAX_GUESSES)));
		}
		for (Thread gameThread : gameThreads) {
			gameThread.start();
		}

		// wait for everyone's game to finish
		for (Thread gameThread : gameThreads) {
			try {
				gameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// sort + build the results
		sortPlayersByGuesses();
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

		// now that the results are done, reset everyone's guesses and win status.
		resetPlayers();

		// send the results to everyone!
		broadcast(resultsString + Server.HALT_ACTION + System.lineSeparator());

		// game has ended.
		endOfGame();
		Server.LOGGER.info(lobbyType + " lobby with ID " + Thread.currentThread().getId() + "has died.");
	}

	private void broadcast(String message) {
		ArrayList<Thread> broadcastThreads = new ArrayList<Thread>();

		// start threads to send the message concurrently...
		for (Player player : playerList) {
			Thread broadcastThread = new Thread(new BroadcastThread(message, player));
			broadcastThreads.add(broadcastThread);
			broadcastThread.start();
		}

		// now wait for them all to complete!
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
		// anyway. bye bye!
	}

	private void sortPlayersByGuesses() {
		// this is a generally accepted method of sorting an ArrayList's elements by a
		// specific parameter.
		// here, we override compare() to compare the amount of guesses a player made
		// when sorting.
		Collections.sort(playerList, new Comparator<Player>() {
			public int compare(Player player1, Player player2) {
				// Integer.compare ranks negative numbers above positive numbers, for some
				// reason.
				if (player1.getGuesses() == -1) {
					return 9999;
				} else if (player2.getGuesses() == -1) {
					return -9999;
				} else {
					return Integer.compare(player1.getGuesses(), player2.getGuesses());
				}
			}
		});
	}

	protected void addPlayer(Player player) {
		playerList.add(player);
	}

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
