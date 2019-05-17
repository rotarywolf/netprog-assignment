package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import server.Server.GameType;

/**
 * the Player class links a client to a player name and their preferred game
 * type.
 * 
 * @author Speck
 *
 */
public class Player {

	private String name;
	private GameType gameType;
	private Socket connection;
	private PrintWriter out;

	private int guesses;
	private boolean winner;

	// TODO: store current guess? maximum score? etc.

	public Player(String name, GameType gameType, Socket connection) {
		this.name = name;
		this.gameType = gameType;
		this.connection = connection;
		guesses = 0;
		winner = false;
		try {
			out = new PrintWriter(this.connection.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
