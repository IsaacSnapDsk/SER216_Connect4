package application;

import java.util.Random;

/**
 * Responsible for simulating a computer player in our Connect4 game
 * @author isaaclandis
 * @asuID ilandis
 */
public class Connect4ComputerPlayer {
	//	A reference to our Connect4 game class
	private Connect4 mConnect4;
	
	//	Constants to represent our grid
	public static final int COLUMNS = 7;
	public static final int ROWS = 6;
	
	/**
	 * In charge of calling our run() method which will link our 
	 * mConnect4 member variable to the Connect4 class we are using.
	 * @param pConnect4 - A reference to our main Connect4 class called in
	 * Connect4's run() method.
	 */
	public static void ComputerPlayer(Connect4 pConnect4) {
		new Connect4ComputerPlayer().run(pConnect4);
	}
	/**
	 * Links our mConnect4 to pConnect4 which is our reference to the 
	 * main Connect4 class and generates a randomized move for our computer while validating it,
	 * and once valid, we pass it into executeTurn() from Connect4
	 * @param pConnect4 - A reference to our main Connect4 class called in Connect4's run() method.
	 */
	private void run(Connect4 pConnect4) {
//		mConnect4 = pConnect4;
//		int randMove = 0;
//		boolean valid = false;
//		Random rand = new Random();
//		//	Continues to generate random moves until we get a valid one.
//		do {
//			randMove = rand.nextInt(6) + 1;
//			valid = mConnect4.validateMove(randMove);
//		} while(!valid);
//		//	Once a move is valid, we execute its turn
//		mConnect4.executeTurn(randMove, 'O');
	}
}