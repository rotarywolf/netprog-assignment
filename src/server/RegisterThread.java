package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.Server.GameType;

public class RegisterThread implements Runnable {

	private Socket connection;
	private PlayerMatchmakeThread matchmakingThread;

	protected RegisterThread(Socket connection, PlayerMatchmakeThread matchmakingThread) {
		this.connection = connection;
		this.matchmakingThread = matchmakingThread;
	}

	@Override
	public void run() {
		String playerName;
		GameType gameType;
		System.out.println("Registering a new player.");

		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			// send a single 'r' character to the client.
			// this indicates the server is ready for the client to input their name and
			// desired gametype.
			out.print(Server.IS_READY);
			out.flush();

			playerName = in.readLine();
			gameType = parseGameTypeInput(in.readLine());

			matchmakingThread.addToQueue(new Player(playerName, gameType, connection));

		} catch (IOException e) {
			System.err.println("An error occurred. Aborting.");
			e.printStackTrace();
		}
	}

	private GameType parseGameTypeInput(String input) throws IOException {
		GameType gameType = null;

		if (input.toUpperCase().trim().equals("SP")) {
			gameType = GameType.SINGLEPLAYER;
		} else if (input.toUpperCase().trim().equals("MP")) {
			gameType = GameType.MULTIPLAYER;
		} else {
			throw new IOException();
		}

		return gameType;
	}

}
