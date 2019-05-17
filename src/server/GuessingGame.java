package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GuessingGame implements Runnable {

	private Player player;
	private int target;
	private int maxGuesses;

	public GuessingGame(Player player, int target, int maxGuesses) {
		this.player = player;
		this.target = target;
		this.maxGuesses = maxGuesses;
	}

	@Override
	public void run() {
		String guess;
		int guessInt = -1;
		String feedback;

		try {
			PrintWriter out = player.getWriter();
			BufferedReader in = new BufferedReader(new InputStreamReader(player.getConnection().getInputStream()));

			while (player.getGuesses() < maxGuesses && player.getGuesses() != -1 && !player.isWinner()) {
				// tell the client it can keep guessing.
				out.print(Server.IS_READY);
				out.flush();

				player.incGuesses();
				guess = in.readLine();
				try {
					guessInt = Integer.parseInt(guess);

					if (guessInt == target) {
						feedback = "You guessed the number! It took you " + player.getGuesses() + " guesses.";
						player.won();
					} else if (guessInt > target) {
						feedback = "Too high!";
					} else {
						feedback = "Too low!";
					}
				} catch (NumberFormatException e) {
					if (guess.toUpperCase().equals("E")) {
						feedback = "You forfeited like a coward.";
						player.forfeitGuess();
					} else {
						feedback = "That wasn't a number. Don't waste your guesses!";
					}
				}

				out.println(feedback);
				out.flush();
			}

			// tell client to stop guessing.
			out.print(Server.HALT_ACTION);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
