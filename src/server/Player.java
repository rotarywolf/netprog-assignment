package server;

import java.net.Socket;
import server.Server.GameType;

/**
 * the Player class links a client to a player name and their preferred game type.
 * 
 * @author Speck
 *
 */
public class Player {

	private String name;
	private GameType gameType;
	private Socket connection;
	
	// TODO: store current guess? maximum score? etc.
	
	public Player(String name, GameType gameType, Socket connection) {
		this.name = name;
		this.gameType = gameType;
		this.connection = connection;
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

}
