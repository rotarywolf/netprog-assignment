package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import server.Server.GameType;

public class Player {

	private String name;
	private GameType gameType;
	private Socket connection;
	private PrintWriter out;
	// the below two parameters are reset before the player enters the game again.
	private int guesses;
	private boolean winner;

	public Player(String name, GameType gameType, Socket connection) {
		this.name = name;
		this.gameType = gameType;
		this.connection = connection;
		guesses = 0;
		winner = false;
		try {
			out = new PrintWriter(this.connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public GameType getGameType() {
		return gameType;
	}

	public Socket getConnection() {
		return connection;
	}

	public int incGuesses() {
		guesses++;
		return guesses;
	}

	public void forfeitGuess() {
		guesses = -1;
	}

	public int getGuesses() {
		return guesses;
	}

	public void resetGuesses() {
		guesses = 0;
	}

	public void won() {
		winner = true;
	}

	public boolean isWinner() {
		return winner;
	}

	public void resetWinner() {
		winner = false;
	}

	public PrintWriter getWriter() {
		return out;
	}

}
