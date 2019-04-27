package server;

public class Server {

	public static void main(String[] args) {
		/*
		 * open the server
		 * 
		 * open a "lobby", a thread that waits for connections.
		 * if the first player to connect to a lobby has the singleplayer flag, immediately begin the game with only that player.
		 * if a player connects to a lobby with the multiplayer flag, either continue waiting for more players or, if 3 players have connected, begin the game.
		 * 
		 * after a lobby accepts the first connection, generate a new empty lobby for any singleplayer requests.
		 * thread pool maybe?
		 * 
		 * open server
		 * lobby1: 0
		 * player with SP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 0
		 * player with MP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 1
		 * lobby3: 0
		 * player with MP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 2
		 * lobby3: 0
		 * player with SP flag joins
		 * lobby1: 1 (closed)
		 * lobby2: 2
		 * lobby3: 1 (closed)
		 * lobby4: 0
		 * etc.
		 */

	}

}
