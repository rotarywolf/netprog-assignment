package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class EndGameThread implements Runnable {

	private Player player;
	private PlayerMatchmakeThread matchmakingThread;

	public EndGameThread(Player player, PlayerMatchmakeThread matchmakingThread) {
		this.player = player;
		this.matchmakingThread = matchmakingThread;
	}

	@Override
	public void run() {
		String option;

		try {
			PrintWriter out = player.getWriter();
			BufferedReader in = new BufferedReader(new InputStreamReader(player.getConnection().getInputStream()));

			// tell the client it's ready for the client's next option.
			out.print(Server.IS_READY);
			out.flush();
			System.out.println("Waiting for client's next option.");
			option = in.readLine();

			if (option.toUpperCase().trim().equals("P")) {
				// player wants to play again. put them back in the matchmaking queue!
				matchmakingThread.addToQueue(player);
			} else if (option.toUpperCase().trim().equals("Q")) {
				// player wants to exit. just let the LobbyThread die.
			} else {
				// TODO: just exit anyway?
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
