package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	enum GameType {
		SINGLEPLAYER, // singleplayer game
		MULTIPLAYER // multiplayer game
	}

	public static final char IS_READY = 'r';
	public static final char HALT_ACTION = 's';
	public static final int MAX_GUESSES = 4;
	public static final int MAX_MP_PLAYERS = 3;

	public static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private static final String LOG_FILE = "server.log";

	public static void main(String[] args) {
		new Server();

	}

	public Server() {
		ServerSocket serverSocket = null;
		Socket connection = null;
		PlayerMatchmakeThread matchmakingThread = null;

		initLogger();
		LOGGER.info("Server started.");

		try {
			serverSocket = new ServerSocket(61802);

			// create the matchmaking thread. we'll pass connections to it, where the
			// matchmaker can figure out what lobby to put them in.
			matchmakingThread = new PlayerMatchmakeThread();
			new Thread(matchmakingThread).start();

			LOGGER.info("Accepting connections...");
			while (true) {
				// keep accepting connections and sending them to the matchmaking thread's
				// player queue.
				connection = serverSocket.accept();
				matchmakingThread.registerPlayer(connection);
				LOGGER.info("Accepted connection from " + connection.getInetAddress() + ".");
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

	private void initLogger() {
		Handler fileHandler;

		try {
			fileHandler = new FileHandler(LOG_FILE);

			LOGGER.addHandler(fileHandler);

			fileHandler.setLevel(Level.ALL);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
