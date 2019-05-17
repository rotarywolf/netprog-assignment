package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;

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

			Server.LOGGER.info("Game has ended.");
			// tell the client it's ready for the client's next option.
			out.print(Server.IS_READY);
			out.flush();

			option = in.readLine();

			if (option.toUpperCase().trim().equals("P")) {
				// player wants to play again. put them back in the matchmaking queue!
				Server.LOGGER.info("Player " + player.getName() + "wants to play again.");
				matchmakingThread.addToQueue(player);
			} else if (option.toUpperCase().trim().equals("Q")) {
				Server.LOGGER.info("Player " + player.getName() + "has quit.");
			} else {
				Server.LOGGER.info("Player " + player.getName() + "has quit.");
			}
		} catch (IOException e) {
			Server.LOGGER.log(Level.SEVERE, "Fatal error.", e);
		}

	}

}
