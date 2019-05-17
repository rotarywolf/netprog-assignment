package server;

import java.io.PrintWriter;

public class BroadcastThread implements Runnable {

	private String message = null; // default value of a string
	private char character = '\u0000'; // default value of a character
	private Player player;

	public BroadcastThread(String message, Player player) {
		this.message = message;
		this.player = player;
	}

	public BroadcastThread(char character, Player player) {
		this.character = character;
		this.player = player;
	}

	@Override
	public void run() {
		PrintWriter out = player.getWriter();

		if (message != null) {
			out.print(message);
		} else if (character != '\u0000') {
			out.print(character);
		}
		out.flush();
	}

}
