package server;

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
		
		// TODO: prompt player for their name and desired game type.
		playerName = "TestPlayer";
		gameType = GameType.SINGLEPLAYER;
		
		matchmakingThread.addToQueue(new Player(playerName, gameType, connection));
	}

}
