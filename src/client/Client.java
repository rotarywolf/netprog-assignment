package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private static Socket socket;
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		testMain();
	}
	
	public static void testMain() throws UnknownHostException, IOException {
		socket = new Socket("localhost", 61802);
		System.out.println("Connected to server.");
		
		// the server is now creating a RegisterThread for this client.
		waitForServer();
		// now it will want the client's input!
		register();
		// after registering, the player is placed in a queue.
		// we'll wait for the server to place us in a lobby and start the game!
		waitForServer();
		
		// take input for guess
		// wait for server to get other guesses and give feedback on guess
		// either take another guess or win?
		
	}
	
	private static void register() throws IOException {
		Scanner clientIn = new Scanner(System.in);
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		String playerName;
		String gameType;
		
		try {
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
		} finally {
			clientIn.close();
			out.close();
		}
	}
	
	/**
	 * sometimes, the client must wait for the server to do something.
	 * we'd better wait, or we might send data when the server isn't ready to use it.
	 * this method will wait for the server to send a single 'r' character,
	 * indicating that it is ready for the client's next action.
	 * 
	 * @throws IOException
	 */
	private static void waitForServer() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		System.out.println("Waiting for server...");
		while (in.read() != 'r') {
			// just wait.
			// TODO: server crashes and we wait forever?
		}
		
	}

}
