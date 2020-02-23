package application;

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Connect4Gui extends Application {
	//	A reference to our Connect4 game class
	private Connect4 mConnect4;
	
	private Stage m_stage;
	private Label[][] m_slots;
	private Button[] m_buttons;
	
	private Alert m_warnAlert;
	private TextInputDialog m_inputDialog;
	
//	Constants to represent our grid
	public static final int COLUMNS = 7;
	public static final int ROWS = 6;
	
	private int m_currentPlayer = 1;
	
	private char m_piece = 'X';
	private boolean m_win = false;
	private boolean m_draw = false;
	private boolean m_quit = false;
	private boolean m_newGame = false;
	
	/**
	 * Our default constructor for Connect4Gui
	 */
	public Connect4Gui() { }
	
	/**
	 * Our paramaterized constructor for Connect4Gui that allows us to
	 * set the current player and our mConnect4
	 * @param pConnect4 - A reference to our Connect4 class so we can initialize our mConnect4
	 */
	public Connect4Gui(Connect4 pConnect4) {
		//	Initializing all of our member variables
		mConnect4 = pConnect4;
		m_currentPlayer = mConnect4.getTurn();
		
	}
	/**
	 * Our action handler so that when a player clicks on a spot on the board, it places
	 * a game piece in the respective column
	 * @author isaaclandis
	 *
	 */
	private class actionHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			String val = ((Button)e.getSource()).getText();
			
			int a = Integer.parseInt(val);
			System.out.println("Turn "+a);
			boolean valid = mConnect4.validateMove(a);
			if(valid) {
				mConnect4.executeTurn(a,m_piece);
				updateBoard();
				String title = m_currentPlayer == 1 ? "Player 1's Turn" : "Player 2's Turn";
				m_stage.setTitle("Connect 4: "+title);
			}
			else {
				
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Choose a different column. \n Column is full");
				alert.showAndWait();	
			}
		}
	}
	/**
	 * Updates our board to match our game grid from Connect4.java
	 * <p>
	 * We check for each spot on the game grid to find any existing placement
	 * of pieces. If we happen to find a piece, we first determine whether
	 * or not it is an 'X' or an 'O' so we know which player placed it. We then
	 * change the slot on our gui to match, with 'X' being red and 'O' being blue.
	 */
	public void updateBoard() {
		for(int i=0; i<ROWS; i++) {
			for(int j=0; j<COLUMNS; j++) {
				if(mConnect4.getGrid(i,j) == 'X') {
					m_slots[i][j].setOpacity(1);
					m_slots[i][j].setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY, Insets.EMPTY)));
				}
				if(mConnect4.getGrid(i,j) == 'O') {
					m_slots[i][j].setOpacity(1);
					m_slots[i][j].setBackground(new Background(new BackgroundFill(Color.BLUE,CornerRadii.EMPTY, Insets.EMPTY)));
				}
			}
		}
		m_currentPlayer = mConnect4.getTurn();
		m_piece = m_currentPlayer == 1 ? 'X' : 'O';  
	}
	/**
	 * Displays to the user the winner of the previous game, and asks if they
	 * would like to play another game.
	 * <p>
	 * If the user answers yes to our question of playing a new game, we dispose of
	 * the previous frame and set m_newGame to true
	 * If the user answers no to our question of playing a new game, we dispose of
	 * the previous frame and set m_quit to true
	 * @param winner - A string the tells us who the winner is, p1, p2, or a draw
	 */
	public void displayEnd(String winner) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText(winner);
		alert.setContentText("Would you like to play a new game?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			m_stage.close();
			m_stage.show();
			m_newGame = true;
		} else {
			m_stage.close();
			m_quit = true;
		}
	}
	
	/**
	 * Displays a message to the player asking them if they want to play against another player,
	 * or the CPU
	 * @return The player's choice of whether it is a cpu or player they wanna play against
	 */
	public char playstyle() {
		System.out.println("hey");
		char pChoice = ' ';
		
		//	Ask the user if they want to play against another player, or the CPU
		do {
			String input = m_inputDialog.getEditor().getText();
			pChoice = Character.toLowerCase(input.charAt(0));
			if(pChoice != 'p' && pChoice != 'c') m_warnAlert.showAndWait();
		} while(pChoice != 'p' && pChoice != 'c');
		return pChoice;
	}
	
	/**
	 * Creates our game board and GUI for us to play on.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		m_stage = primaryStage;
		m_stage.setTitle("Connect 4");
		
		m_warnAlert = new Alert(AlertType.WARNING);
		m_warnAlert.setHeaderText("Invalid input try again.");
		
		m_inputDialog = new TextInputDialog("Enter 'P' if you want to play against another player. Enter 'C' to play against a computer.");
		m_inputDialog.setHeaderText("Choose your opponent");
		
		GridPane gridPane = new GridPane();
		for(int i=0; i<COLUMNS; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(100.0/COLUMNS);
			gridPane.getColumnConstraints().add(colConst);
		}
		for(int i=0; i<ROWS; i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPercentHeight(100.0/ROWS);
			gridPane.getRowConstraints().add(rowConst);
		}

		//		panel.setLayout(new GridLayout(COLUMNS,ROWS + 1));
		
		m_slots = new Label[ROWS][COLUMNS];
		m_buttons = new Button[COLUMNS];
		
		//	Creating our buttons at each column so players can drop their pieces
		for (int i=0; i<COLUMNS; i++) {
			m_buttons[i] = new Button("" + (i + 1));
			m_buttons[i].setOnAction(new actionHandler());
				
			gridPane.getChildren().add(m_buttons[i]);
		}
		//	Creating our grid for our board full of open slots
		for(int i=0; i<ROWS; i++) {
			for(int j=0; j<COLUMNS; j++) {
				m_slots[i][j] = new Label();
//				m_slots[i][j].setHorizontalAlignment(SwingConstants.CENTER);
//				m_slots[i][j].setBorder(new LineBorder(Color.black));
				m_slots[i][j].setStyle("-fx-border-color: white;");
				gridPane.getChildren().add(m_slots[i][j]);
			}
		}
		//	Establishing our frame
		m_stage.setScene(new Scene(gridPane,700,600));
		m_stage.setResizable(false);
		m_stage.setWidth(700);
		m_stage.setHeight(600);
		m_stage.show();
	}
	/**
	 * Allows us to launch the JavaFX environment and then calls start() in its stack trace
	 * @param args - A list of arguments we can pass into the launcher
	 */
	public static void main(String args[]) {
		Application.launch(args);
	}
}