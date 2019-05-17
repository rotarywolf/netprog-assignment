package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	enum GameType {
		SINGLEPLAYER, // singleplayer game
		MULTIPLAYER // multiplayer game
	}

	public static final char IS_READY = 'r';
	public static final char HALT_ACTION = 's';
	public static final int MAX_GUESSES = 4;
	public static final int MAX_MP_PLAYERS = 3;

	public static void main(String[] args) {
		new Server();

	}

	public Server() {
		System.out.println("[*] Server started.");
		ServerSocket serverSocket = null;
		Socket connection = null;
		PlayerMatchmakeThread matchmakingThread = null;

		try {
			serverSocket = new ServerSocket(61802);

			// create the matchmaking thread. we'll pass connections to it, where the
			// matchmaker can figure out what lobby to put them in.
			matchmakingThread = new PlayerMatchmakeThread();
			new Thread(matchmakingThread).start();

			while (true) {
				// keep accepting connections and sending them to the matchmaking thread's
				// player queue.
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
