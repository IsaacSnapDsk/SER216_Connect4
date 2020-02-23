package application;

//import Connect4;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Our Connect4Server that will get connection from the clients, and then
 * create unique game instance threads using these clients together.
 * @author isaaclandis
 *
 */
public class Connect4Server {
	
	/**
	 * Our main function that will connect 2 clients into the server, and then create
	 * a unique thread for these two clients to play Connect4 on.
	 * @param args
	 * @throws IOException
	 */
	public static void main (String args[]) {
		ServerSocket server;
		try {
			while(true) {
				server = new ServerSocket(5010);
				
				System.out.println("Server starting...");
				
				System.out.println("Waiting for client 1...");
				
				Socket socket1 = server.accept();
				
				System.out.println("Client 1 connected.");
				
				DataInputStream din1 = new DataInputStream(socket1.getInputStream());
				DataOutputStream dout1 = new DataOutputStream(socket1.getOutputStream());
				
				System.out.println("Waiting for client 2...");
				
				Socket socket2 = server.accept();
				
				System.out.println("Client 2 connected.");
				
				DataInputStream din2 = new DataInputStream(socket2.getInputStream());
				DataOutputStream dout2 = new DataOutputStream(socket2.getOutputStream());
				
				
				System.out.println("Establishing server thread...");
				
				Thread t = new Connect4ClientHandler(server, socket1, socket2, din1, din2, dout1, dout2);
				
				t.start();
				
			} 
		} catch (IOException i) {
			System.out.println("Server failed to start, terminating...");
			System.exit(1);
		}
		
	}
}

/**
 * Our thread that branches off from our server, containing two clients that are playing Connect4 together.
 * @author isaaclandis
 *
 */
class Connect4ClientHandler extends Thread {
	private DataInputStream din1 = null;
	private DataOutputStream dout1 = null;
	private DataInputStream din2 = null;
	private DataOutputStream dout2 = null;
	
	private Socket socket1 = null;
	private Socket socket2 = null;
	private ServerSocket server = null;
	private Connect4 mConnect4;
	
	/**
	 * Our unique game instance constructor that sets all of the clients info to the respective parameters, as well as creates a new game.
	 * @param socket1 - Client 1's instance
	 * @param socket2 - Client 2's instance
	 * @param din1 - Client 1's DataInputStream
	 * @param din2 - Client 2's DataInputStream
	 * @param dout1 - Client 1's DataOutputStream
	 * @param dout2 - Client 2's DataOutputStream
	 */
	public Connect4ClientHandler(ServerSocket server, Socket socket1, Socket socket2, DataInputStream din1, DataInputStream din2, DataOutputStream dout1, DataOutputStream dout2) {
		mConnect4 = new Connect4();
		this.server = server;
		this.socket1 = socket1;
		this.socket2 = socket2;
		this.din1 = din1;
		this.din2 = din2;
		this.dout1 = dout1;
		this.dout2 = dout2;
		try {
			String player = mConnect4.getTurn() == 1 ? "X" : "O";
			String nonPlayer = mConnect4.getTurn() == 1 ? "O" : "X";
			dout1.writeUTF(player);
			dout2.writeUTF(nonPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * An override of our run() method in Thread that will allow us to exchange information between the clients
	 * and the server
	 */
	@Override
	public void run() {
		String fromClient;
		int pMove;
		boolean valid = false;
		while(true) {
			try {
				//	Get user input until it is valid
				do {
					fromClient = din1.readUTF();
					System.out.println("Received input "+fromClient);
					pMove = Integer.parseInt(fromClient);
					
					valid = mConnect4.validateMove(pMove);
				}while(!valid);
				
				mConnect4.executeTurn(pMove, 'X');
				
				valid = false;
				fromClient = "";
				pMove = 0;
				
				do {
					fromClient = din2.readUTF();
					System.out.println("Received input "+fromClient);
					pMove = Integer.parseInt(fromClient);
				}while(!valid);
				
			} catch (Exception e) {
				System.out.println("Client terminated connection. Closing...");
				break;
			}
		}
		try {
			this.din1.close();
			this.dout1.close();
			this.din2.close();
			this.dout2.close();
			this.socket1.close();
			this.socket2.close();
			this.server.close();
		} catch (Exception e) {
			System.out.println("Could not close server, please end process.");
		}
	}
}