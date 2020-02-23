package application;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Our Connect4Client that will connect to our server for playing the game
 * @author isaaclandis
 *
 */
public class Connect4Client {

	private Socket socket = null;
	private DataInputStream din = null;
	private DataOutputStream dout = null;
	private int move;
	
	/**
	 * Default constructor for our Connect4Client.
	 * <p>
	 * Creates a new Socket for our client at a port matching the server port
	 * and then creates a unique datainputstream and dataoutputstream for the socket.
	 * @throws IOException
	 */
	Connect4Client() throws IOException {
		socket = new Socket("localhost", 5010);
			
		din = new DataInputStream(socket.getInputStream());
		dout = new DataOutputStream(socket.getOutputStream());
			
		System.out.println("Connected to server.");
	}
	
	/**
	 * Gets the initial shape of the player, with it being determined from our
	 * Connect4Server. Can be 'X' or 'O'.
	 * @return String - Represents our player's shape
	 * @throws IOException
	 */
	String getInitial() throws IOException {
		return din.readUTF();
	}
	
	/**
	 * Sends our move into the server so that the client can reflect it on both sides
	 * @param s - The move we are pushing to the server
	 * @return Int - The move from the server
	 * @throws IOException
	 */
	int sendMove(String s) throws IOException {
		dout.writeUTF(s);
		s = din.readUTF();
		move = Integer.parseInt(s);
		return Integer.parseInt(s);
	}
	
	/**
	 * Returns our last stored move in the client 
	 * @return Int - Last move conducted by client
	 */
	int getLastMove() {
		return this.move;
	}
	
	/**
	 * The last move from the server
	 * @return Int - Last move from the server
	 * @throws IOException
	 */
	int getPlayerMove() throws IOException {
		String s = din.readUTF();
		return Integer.parseInt(s);
	}
}