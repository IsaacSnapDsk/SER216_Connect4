package application;

import java.util.Scanner;

//import javafx.application.Application;

import java.util.InputMismatchException;

/**
 * Handles all of the logic related to our Connect4 board game
 * This contains our game board, taking turns, checking win conditions
 * and calling on display from our Connect4TextConsole so that it can
 * display our board to the players
 * @author isaaclandis
 * @asuid ilandis
 */

public class Connect4 {
//	Connect4Gui mConnect4Gui;
	Connect4TextConsole mConnect4TextConsole;
	
	private static Connect4Client client;
	//	Constants to represent our grid
	public static final int COLUMNS = 7;
	public static final int ROWS = 6;
	
	//	Our game grid of 6 rows and 7 columns
	private char[][] m_gameGrid = new char[6][7];
	//	Variable to represent whose turn it is
	private boolean m_p1Turn = true;
	private boolean m_cpuTurn = false;
	//	Variables to check whose win it is, or if it ends in a draw
	private int m_winner = 0;
	private boolean m_draw = false;
	//	Variable to let us know if a cpu is playing or not for error msgs
	private boolean m_cpuGame = false;
	//	Variables to contain the last move the player made for checking later
	private int m_lastRow;
	private int m_lastColumn;
	
	public Connect4() {}
	
	public static void main(String args[]) {
		new Connect4().run();
	}
	
	/**
	 * Our main function that calls all of the others that is responsible for
	 * running through the logic of our Connect4 game
	 * <p>
	 * We continue running through our Connect4 game having each player take
	 * turns until someone wins, or the game ends in a draw which we then will
	 * display a console output depending on what happens.
	 * @return
	 */
	public char[][] run() {
		char pChoice = ' ';
		char gChoice = ' ';
		//	Initialize our game grid
		for(int i=0; i<ROWS; i++) {
			for(int j=0; j<COLUMNS; j++) {
				this.m_gameGrid[i][j] = ' ';
			}
		}
		Scanner in = new Scanner(System.in);
		//	Ask the user if they want to play the GUI version or the console UI version
		do {
			System.out.print("Enter 'G' if you want to play with GUI version, or enter 'U' if you want to player the console UI version: ");
			gChoice = in.next().charAt(0);
			gChoice = Character.toLowerCase(gChoice);
			if(gChoice != 'g' && gChoice != 'u') System.out.println("Invalid input try again.");
			in.nextLine();
		} while(gChoice != 'g' && gChoice != 'u');
		
		//	Establish the GUI if picked, or the Console-UI board if pickedß
		switch(gChoice) {
		case 'u':
			System.out.println("You have picked the console UI version.");
			mConnect4TextConsole = new Connect4TextConsole(this);
			mConnect4TextConsole.display(this.m_gameGrid);
			//	Ask the user if they want to play against another player, or the CPU
			do {
				System.out.print("Enter 'P' if you want to play against another player; enter 'C' to play against computer. ");
				pChoice = in.next().charAt(0);
				pChoice = Character.toLowerCase(pChoice);
				if(pChoice != 'p' && pChoice != 'c') System.out.println("Invalid input try again.");
				in.nextLine();
			} while(pChoice != 'p' && pChoice != 'c');
			break;
		case 'g':
			System.out.println("You have picked the GUI version.");
//			try {
//				Application.launch(Connect4Gui.class);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			pChoice = mConnect4Gui.playstyle();
			break;
		}
		
		//	Lets us know whether or not the CPU is playing or not
		if(pChoice == 'c' || pChoice == 'C') this.m_cpuGame = true;	
		
		//	Begin our connection to the server
		this.gameStart(in);
		
		in.close();
		//	If the match is a draw, tell the user
		if(this.m_draw) {
			System.out.println("No winner, match ends in a draw.");
		}
		//	Otherwise, tell the respective winner that they won
		else {
			System.out.println("Congratulations! Player "+getTurn()+" has won.");
		}
		return this.m_gameGrid;
	}
	
	/**
	 * Triggers our Client connection to connect to the game server
	 * <p>
	 * Establishes a socket that connects to the game server by initializing Connect4Client.
	 * Once connected, we wait until the second player connects to the server and once they have,
	 * we start playing the game.
	 * @param in - A reference to our scanner so we can read the command line inputs
	 */
	private void gameStart(Scanner in) {
		try {
			client = new Connect4Client();
			System.out.println("Successfully Connected.");
			if(this.m_p1Turn) {
				System.out.println("Waiting on other player...");
				int pMove = client.getPlayerMove();
				//	If the move is valid, execute their turn
				if(pMove <= 7 && pMove >= 1) this.executeTurn(pMove, client.getInitial().charAt(0));
				
			}
			
		} catch (Exception e) {
			System.out.println("Failed to connect to server... Terminating");
			System.exit(1);
		}
		
		while(true) {
			this.pickMove(in);
		}
	}
	
	/**
	 * Our method responsible for executing each players turn where we check whose
	 * turn it is, then ask that player for where they'd like to place their piece.
	 * <p>
	 * We handle errors here by forcing the user to enter in a number 1-7 and repeating
	 * until this condition is met.
	 * Then we call executeTurn() to do the rest of the player's turn
	 * @param in - Reference to our Scanner object so we can read user input
	 */
	private void pickMove(Scanner in) {
		int c_pick = 0;
		boolean validMove = false;
		
		//			If it is p1's turn, we use X, p2 we use O
		char p_shape = this.m_p1Turn ? 'X' : 'O';

		//	We ask our player for a number 1-7 then validate that move
		//	We only proceed if the column is between 1-7 AND valid
		do {
			try {
				System.out.print("Choose a column from 1-7 to place your piece. ");
				c_pick = in.nextInt();
				validMove = this.validateMove(c_pick);
			} catch (InputMismatchException e) {
				System.out.println("Invalid column input." );
			}
			in.nextLine();
		} while((c_pick > 1 || c_pick < 7) && !validMove);
		
		try {
			client.sendMove(Integer.toString(c_pick));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.executeTurn(c_pick,p_shape);
	}
	/**
	 * Our method responsible for executing the player's turn and checking if anyone has won.
	 * Permission set to protected so it can be called in Connect4ComputerPlayer
	 * <p>
	 * We then place their piece on the board respectively, and display the board to 
	 * the console.
	 * After all of this, we then check if anyone won or if it is a draw and change
	 * the member variables to react to this.
	 * @param c_pick - The player's selected column they'd like to place their piece
	 * @param p_shape - The shape of the piece being placed so we know if it is p1 or p2
	 */
	protected void executeTurn(int c_pick, char p_shape) {
		//		If move is valid, we place it on the board and display it
		this.placeMove(c_pick,p_shape);
		
		mConnect4TextConsole.display(this.m_gameGrid);

		if(this.checkWin()) {
			this.m_winner = getTurn();
//			this.mConnect4Gui.displayEnd("Player "+getTurn()+" has won!");
		}
		else {
			if(this.checkFull()) {
				this.m_draw = true;
			}
		}
	}
	/**
	 * We use this to validate the move that the user picks and make sure
	 * that it fits within the bounds of 1-7 and that it is not a full column.
	 * The only way that a column can be full is if the very top (index 0) is
	 * occupied.
	 * @param c_pick	int - The index of the column our player wishes to play.
	 * @return boolean
	 */
	public boolean validateMove(int c_pick) {
		//	Check if the spot we are trying to place is occupied or not
		//	We want to make sure our spot is NOT occupied
		if((c_pick > 7 || c_pick < 1) || (this.m_gameGrid[0][c_pick-1] != ' ')) {
			if(!this.m_cpuTurn) {
				System.out.println("Invalid input. Input must be a number between 1-7.");
			}
			return false;
		}
		return true;
	}
	/**
	 * Places the players piece where they picked on the board and then 
	 * switches turns to the other player.
	 * @param c_pick	int - The index of the column the player wants to play.
	 * @param p_shape	char - The piece the current player is playing, either
	 * 'O' or 'X'
	 */
	private void placeMove(int c_pick, char p_shape) {
		boolean placed = false;
		//	Start from bottom up to find our first available space
		for(int i=ROWS-1; i>=0; i--) {
			if(placed) break;
			for(int j=COLUMNS-1; j>=0; j--) {
				if(placed) break;
				//	If we are at the selected column, check if available
				if(j+1 == c_pick && this.m_gameGrid[i][j] == ' ') {
					this.m_gameGrid[i][j] = p_shape;
					//	Store our last move for future reference in checkWin()
					this.m_lastRow = i;
					this.m_lastColumn = j;
					placed = true;
					
				}
			}
		}
		if(this.m_cpuGame) this.m_cpuTurn = this.m_p1Turn;
		
		this.m_p1Turn = !this.m_p1Turn;
	}
	/**
	 * Checking our grid to make sure that it isn't full.
	 * <p>
	 * The only way that our grid can be full is if the top layer is occupied
	 * so all we have to do is check if there are any spots on this top layer
	 * that are empty, and if there are, we return false because it isn't full.
	 * Likewise, if we are unable to find any blank spots on the top row, our grid
	 * is full so we return true.
	 * @return boolean
	 */
	private boolean checkFull() {
		for(int i=0; i<COLUMNS; i++) {
			if(this.m_gameGrid[0][i] == ' ') {
				return false;
			}
		}
		return true;
	}
	/**
	 * Checks if any player has currently won by matching 4 pieces in a row
	 * either horizontally, vertically, or diagonally.
	 * @return boolean
	 */
	private boolean checkWin() {
		char p_token = this.m_gameGrid[this.m_lastRow][this.m_lastColumn];

		System.out.println("IT IS CURRENTLY "+this.m_p1Turn+" TURN");
		int count = 0;
		//	Horizontal Check
		for(int i=0; i<COLUMNS; i++) {
			//	If consecutive matching horizontally, increase count
			if(this.m_gameGrid[this.m_lastRow][i] == p_token) {
				count++;
			}
			//	Reset count if not consecutive
			else {
				count = 0;
			}
			//	If 4 or more, then the player wins
			if(count >= 4) {
				return true;
			}
		}
		//	Vertical Check
		for(int i=0; i<ROWS; i++) {
			if(this.m_gameGrid[i][this.m_lastColumn] == p_token) {
				count++;
			}
			else {
				count = 0;
			}
			if(count >= 4) {
				return true;
			}
		}
		//	Diagonal Check ascending
		for(int i=3; i<ROWS; i++) {
			for(int j=0; j<COLUMNS - 3; j++) {

				//	If we have 4 matching tokens diagonally, return true
				if(this.m_gameGrid[i][j] == p_token && 
						this.m_gameGrid[i-1][j+1] == p_token &&
						this.m_gameGrid[i-2][j+2] == p_token &&
						this.m_gameGrid[i-3][j+3] == p_token) {
					return true;
				}
			}
		}
		//		Diagonal Check descending
		for(int i=3; i<ROWS; i++) {
			for(int j=3; j<COLUMNS; j++) {
				//	If we have 4 matching tokens diagonally, return true
				if(this.m_gameGrid[i][j] == p_token && 
						this.m_gameGrid[i-1][j-1] == p_token &&
						this.m_gameGrid[i-2][j-2] == p_token &&
						this.m_gameGrid[i-3][j-3] == p_token) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * A getter method to return whether it is player 1 or player 2's turn
	 * @return An integer value representing whose turn it is
	 */
	public int getTurn() {
		return this.m_p1Turn ? 1 : 2;
	}
	/**
	 * A getter method that will return the current shape of the game piece
	 * signaling whose turn it is, with 'X' being p1 and 'O' being p2.
	 * @return
	 */
	private char getShape() {
		return this.m_p1Turn ? 'X' : 'O';
	}
	/**
	 * A getter method that will return the current char at the spot specified
	 * @param x - The x-coordinate of our spot
	 * @param y - The y-coordinate of our spot
	 * @return The char that is at our spot, being either 'X', 'O' or ' '
	 */
	public char getGrid(int x, int y) {
		return this.m_gameGrid[x][y];
	}
}