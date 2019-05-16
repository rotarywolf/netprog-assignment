package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		testMain();
	}
	
	public static void testMain() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 61802);
		System.out.println("Connected to server");
	}

}
