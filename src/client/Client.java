package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private Socket socket;
	private Scanner clientIn;
	private PrintWriter out;
	private BufferedReader serverIn;

	private static final char IS_READY = 'r';
	private static final char HALT_ACTION = 's';

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client();
	}

	public Client() throws UnknownHostException, IOException {
		socket = new Socket("localhost", 61802);
		clientIn = new Scanner(System.in);
		serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream());

		System.out.println("Connected to server.");

		// the server is now creating a RegisterThread for this client.
		waitForServer();
		// now it will want the client's input!
		register();
		// after registering, the player is placed in a queue.
		// server put us in a lobby and started the game.
		do {
			guess();
			waitForServer();
		} while (playAgain());

	}

	private void register() {
		String playerName;
		String gameType;

		System.out.println("Please input your name!");
		playerName = clientIn.nextLine();
		if (playerName.equals("")) {
			// player left his name is empty. what an idiot.
			System.out.println("Can't say it's what I'd have picked for you.");
			System.out.println("But if that's your name, that's your name.");
			playerName = "The Courier";
		}
		out.println(playerName);

		System.out.println("Please input your desired game type!");
		System.out.println("Type 'SP' for single player, or 'MP' for multi-player.");
		gameType = clientIn.nextLine();
		out.println(gameType);
		out.flush();
	}

	private void guess() throws IOException {
		printGameInfo();
		String playerList;
		String guess;
		String feedback;
		String results;

		System.out.println("Waiting for other players, hold tight...");
		playerList = serverIn.readLine();
		System.out.println(playerList);

		while (serverIn.read() != HALT_ACTION) {
			System.out.print("Make your guess: ");
			guess = clientIn.nextLine();
			out.println(guess);
			out.flush();

			feedback = serverIn.readLine();
			System.out.println(feedback);
		}

		System.out.println("Your game has ended. Waiting for other players to finish...");
		System.out.println("~ Results ~");

		while (!(results = serverIn.readLine()).equals("" + HALT_ACTION)) {
			System.out.println(results);
		}

	}

	private boolean playAgain() throws IOException {
		boolean playAgain;
		String option;

		System.out.println("Thanks for playing!");
		System.out.println("To play again, type 'P'.");
		System.out.println("Type 'Q' to quit. Any other character will also quit.");

		option = clientIn.nextLine();
		if (option.toUpperCase().trim().equals("P")) {
			out.println(option);
			out.flush();
			playAgain = true;
		} else {
			out.println("Q");
			out.flush();
			playAgain = false;
		}

		return playAgain;

	}

	private void printGameInfo() {
		System.out.println("=== Online Guessing Game ===");
		System.out.println("~ How to Play ~");
		System.out.println("The server has picked a number between 0 and 9. You have to guess it!");
		System.out.println("You only get a certain number of guesses.");
		System.out.println("The server will give you a hint after each wrong guess.");
		System.out.println("Type 'e' to give up. But you're better than that.");
		System.out.println();
	}

	/**
	 * sometimes, the client must wait for the server to do something. we'd better
	 * wait, or we might send data when the server isn't ready to use it. this
	 * method will wait for the server to send a single 'r' character, indicating
	 * that it is ready for the client's next action.
	 * 
	 * @throws IOException
	 */
	private void waitForServer() throws IOException {
		while (serverIn.read() != IS_READY) {
			// just wait.
			// TODO: server crashes and we wait forever?
		}

	}

}
