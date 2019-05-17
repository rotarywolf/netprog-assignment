package server;

import java.io.PrintWriter;

public class BroadcastThread implements Runnable {

	private String message;
	private Player player;

	public BroadcastThread(String message, Player player) {
		this.message = message;
		this.player = player;
	}

	@Override
	public void run() {
		PrintWriter out = player.getWriter();
		out.print(message);
		out.flush();
	}

}
