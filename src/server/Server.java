package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	enum GameType {
		SINGLEPLAYER, // singleplayer game
		MULTIPLAYER // multiplayer game
	}

	public static void main(String[] args) {
		/*
		 * open the server
		 * 
		 * open a "lobby", a thread that waits for connections.
		 * if the first player to connect to a lobby has the singleplayer flag, immediately begin the game with only that player.
		 * if a player connects to a lobby with the multiplayer flag, either continue waiting for more players or, if 3 players have connected, begin the game.
		 * 
		 * after a lobby accepts the first connection, generate a new empty lobby for any singleplayer requests.
		 * thread pool maybe?
		 * 
		 * open server
		 * lobby1: 0
		 * player with SP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 0
		 * player with MP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 1
		 * lobby3: 0
		 * player with MP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 2
		 * lobby3: 0
		 * player with SP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 2
		 * lobby3: 1 (closed)
		 * lobby4: 0
		 * etc.
		 */
		
		new Server();

	}
	
	public Server() {
		System.out.println("[*] Server started.");
		ServerSocket serverSocket = null;
		Socket connection = null;
		PlayerMatchmakeThread matchmakingThread = null;

		try {
			serverSocket = new ServerSocket(61802);
			
			// create the matchmaking thread. we'll pass connections to it, where the matchmaker can figure out what lobby to put them in.
			matchmakingThread = new PlayerMatchmakeThread();
			new Thread(matchmakingThread).start();

			while (true) {
				// keep accepting connections and sending them to the matchmaking thread's player queue.
				System.out.println("Waiting for connections...");
				connection = serverSocket.accept();
				matchmakingThread.registerPlayer(connection);
				System.out.println("Sent new connection to register");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
