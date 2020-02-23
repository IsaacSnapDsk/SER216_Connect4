package application;

/**
 * Responsible for running our Connect4 program and displaying the board in the console
 * @author isaaclandis
 * @asuID ilandis
 */
public class Connect4TextConsole {
//	A reference to our Connect4 game class
	private Connect4 mConnect4;
	
	//	Constants to represent our grid
	public static final int COLUMNS = 7;
	public static final int ROWS = 6;
	
	public Connect4TextConsole(Connect4 pConnect4) {
		mConnect4 = pConnect4;
	}
	/**
	 * Displays our game board in the console so the players can see the status.
	 * Only parameter it takes in is the game grid so that it can be applied to
	 * any game.
	 * @param m_gameGrid	char[][] - The game grid that we will be displaying
	 */
	public void display(char[][] m_gameGrid) {
		//	Display our grid to the user
		for(int i=1; i<8; i++) {
			System.out.print(' ');
			System.out.print(i);
		}
		System.out.print('\n');
		for(int i=0; i<ROWS; i++) {
			for(int j=0; j<COLUMNS; j++) {
				System.out.print('|');
				System.out.print(m_gameGrid[i][j]);
				if(j==COLUMNS-1) System.out.println('|');
			}
		}
		System.out.print('\n');
	}
}