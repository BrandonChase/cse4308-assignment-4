import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Brandon Chase, based on code by James Spargo
 * 
 * This is the Gameboard class.  It implements a two dimension array that
 * represents a connect four gameboard. It keeps track of the player making
 * the next play based on the number of pieces on the game board. It provides
 * all of the methods needed to implement the playing of a max connect four
 * game.
 */

public class GameBoard implements Cloneable {
	//Board dimensions
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLS = 7;
	public static final int SUM_TURNS = 3; //if player 1 represented by turn number 1 and player 2 represented by turn number 2, sum can be used to get one players number from knowing other
	public static final int UTILITY1 = 1;
	public static final int UTILITY2 = 10;
	public static final int UTILITY3 = 25;
	public static final int UTILITY4 = 100;
	
	//Class fields
	private int[][] board;
	private int turnNum;
	private int numPieces;
	
	//Constructor
	public GameBoard(String inputFileName) throws Exception {
		board = new int[NUM_ROWS][NUM_COLS];
		numPieces = 0;
		
		try {
			//Open input file
			BufferedReader input = new BufferedReader(new FileReader(inputFileName));
			
			//Read game data
			ArrayList<String> data = new ArrayList<String>();
			String line = null;
			while((line = input.readLine()) != null) { 
				data.add(line); 
			}
			input.close();
			
			//Set board values
			for(int row = 0; row < NUM_ROWS; row++) {
				String currentLine = data.get(row);
				
				for(int col = 0; col < NUM_COLS; col++) {
					int value = Character.getNumericValue(currentLine.charAt(col));
					
					if(value == 0 || value == 1 || value ==2) {
						board[row][col] = value;
					} else {
						throw new Exception("Invalid value at block [" + row + ", " + col + "].\n");
					}
					
					if(value != 0) {
						numPieces++;
					}
				}
			}
			
			//Get whose turn it is (last line in data)
			turnNum = Integer.parseInt(data.get(data.size() - 1));
			if(turnNum != 1 && turnNum != 2) { 
				throw new Exception("Invalid turn value at block.\n"); 
			}
		} catch(Exception e) { //Error opening file. board is already initialized and numPieces = 0, so just need to initialize turnNum = 1 to start game from clean slate.
			turnNum = 1;
		}
	}
	
	public GameBoard(GameBoard otherGame) {
		this.turnNum = otherGame.turnNum;
		this.numPieces = otherGame.numPieces;
		this.board = new int[NUM_ROWS][NUM_COLS];
		
		//copy board
		for(int row = 0; row < NUM_ROWS; row++) {
			for(int col = 0; col < NUM_COLS; col++)
			{
				this.board[row][col] = otherGame.board[row][col];
			}
		}
	}
	
	public int calculateScore(int player) {
		int score = 0;
		
		for(int row = 0; row < NUM_ROWS; row++) {
			for(int col = 0; col < NUM_COLS; col++) {
				//check horizontal (3 spaces to right of current space)
				if(col + 3 < NUM_COLS) {
					if(board[row][col] == player && board[row][col+1] == player && board[row][col+2] == player && board[row][col+3] == player) {
						score++;
					}
				}
				
				//check vertical (3 spaces down of current space)
				if(row + 3 < NUM_ROWS) {
					if(board[row][col] == player && board[row+1][col] == player && board[row+2][col] == player && board[row+3][col] == player) {
						score++;
					}
				}
				//check diagonal / (3 spaces down and to left of current space)
				if(row + 3 < NUM_ROWS && col - 3 >= 0) {
					if(board[row][col] == player && board[row + 1][col - 1] == player && board[row + 2][col - 2] == player && board[row + 3][col - 3] == player) {
						score++;
					}
				}
				
				//check diagonal \ (3 spaces down and to right of current space)
				if(row + 3 < NUM_ROWS && col + 3 < NUM_COLS) {
					if(board[row][col] == player && board[row + 1][col + 1] == player && board[row + 2][col + 2] == player && board[row + 3][col + 3] == player) {
						score++;
					}
				}
			}
		}
		
		return score;
	}
	
	//accounts for completed connect 4s as well as "connect" 1s, 2s, and 3s that have potential to be a connect 4 
	public int calculateUtility(int playerNumber) {
		int utility = 0;
		int enemyNumber = SUM_TURNS - playerNumber; //only options are 1 or 2; if turn is 1, 3 - 1 = 2 and if turn is 2, 3 - 2 = 1
		
		for(int row = 0; row < NUM_ROWS; row++) {
			for(int col = 0; col < NUM_COLS; col++) {
				ArrayList<Integer> values = new ArrayList<Integer>();
				
				//-----check horizontal (3 spaces to right of current space)------
				if(col + 3 < NUM_COLS) {
					values.add(board[row][col]);
					values.add(board[row][col+1]);
					values.add(board[row][col+2]);
					values.add(board[row][col+3]);
					
					utility = updateUtility(utility, values, playerNumber, enemyNumber);
				}
				
				//check vertical (3 spaces down of current space)
				if(row + 3 < NUM_ROWS) {
					values.clear();
					values.add(board[row][col]);
					values.add(board[row+1][col]);
					values.add(board[row+2][col]);
					values.add(board[row+3][col]);
					
					utility = updateUtility(utility, values, playerNumber, enemyNumber);
				}
				//check diagonal / (3 spaces down and to left of current space)
				if(row + 3 < NUM_ROWS && col - 3 >= 0) {
					values.clear();
					values.add(board[row][col]);
					values.add(board[row+1][col-1]);
					values.add(board[row+2][col-2]);
					values.add(board[row+3][col-3]);
					
					utility = updateUtility(utility, values, playerNumber, enemyNumber);
				}
				
				//check diagonal \ (3 spaces down and to right of current space)
				if(row + 3 < NUM_ROWS && col + 3 < NUM_COLS) {
					values.clear();
					values.add(board[row][col]);
					values.add(board[row+1][col+1]);
					values.add(board[row+2][col+2]);
					values.add(board[row+3][col+3]);
				
					utility = updateUtility(utility, values, playerNumber, enemyNumber);
				}
			}	
		}
		return utility;
	}
	
	private int updateUtility(int utility, ArrayList<Integer> values, int playerNumber, int enemyNumber) {
		if(!values.contains(enemyNumber)) //if only contains player chips or empty space, contains connect 4 or possible connect 4
		{
			int count = Collections.frequency(values, playerNumber);
			switch(count) {
			case 1:
				utility += UTILITY1;
				break;
			case 2:
				utility += UTILITY2;
				break;
			case 3:
				utility += UTILITY3;
				break;
			case 4:
				utility += UTILITY4;
				break;
			}
		} else if(!values.contains(playerNumber)) { //if only contains enemy chips or empty space, contains connect 4 or possible connect 4
			int count = Collections.frequency(values, enemyNumber);
			switch(count) {
			case 1:
				utility -= UTILITY1;
				break;
			case 2:
				utility -= UTILITY2;
				break;
			case 3:
				utility -= UTILITY3;
				break;
			case 4:
				utility -= UTILITY4;
				break;
			}
		}
		
		return utility;
	}
	
	public int getCurrentTurn() {
		return turnNum;
	}
	
	public int getNumPieces() {
		return numPieces;
	}
	
	public int[][] getBoard() {
		return board;
	}
	
	public boolean isPlayValid(int column) {
		//return true if column if within bounds and column is not full (top space of column is empty)
		return ((0 <= column && column < NUM_COLS) && board[0][column] == 0);
	}
	
	public void playPiece(int column) {
		//Starting from bottom of column, go up until empty space is found and put piece there
		for(int row = NUM_ROWS - 1; row >= 0; row--) {
			if(board[row][column] == 0) {
				board[row][column] = turnNum;
				numPieces++;
				changeTurn();
				break;
			}
		}
	}
	
	public void print() {
		//print divider line
		System.out.println("_________________________");
		//print first line with column indicator numbers
		String indexsLine = "";
		for(int col = 0; col < NUM_COLS; col++) {
			indexsLine += " " + String.valueOf(col); 
		} 
		System.out.println(indexsLine);
		
		//print board
		for(int[] row : board) {
			String line = "|";
			for(int num : row) {
				line += turnToString(num);
				line += "|";
			}
			
			System.out.println(line);
		}
		
		//print scores
		System.out.println(turnToString(1) + " Score: " + String.valueOf(calculateScore(1)));
		System.out.println(turnToString(2) + " Score: " + String.valueOf(calculateScore(2)));
	}
	
	public void save(String outputFileName) throws Exception {
		//open output file
		BufferedWriter output = new BufferedWriter(new FileWriter(outputFileName));
		
		//save board
		for(int[] row : board) {
			for(int num : row) {
				output.write(String.valueOf(num));
			}
			output.newLine();
		}
		
		//save next turn
		output.write(String.valueOf(turnNum));
		
		//close output file
		output.close();
	}
	
	public boolean isGameOver() {
		return (getNumPieces() == NUM_ROWS * NUM_COLS);
	}
	
	public String turnToString(int turn) {
		String result;
		switch(turn) {
		case 0: //blank space for empty space
			result = "-";
			break;
		case 1:
			result = "X";
			break;
		case 2:
			result = "O";
			break;
		default:
			result = "e"; //e for error
		}
		
		return result;
	}
	
	private void changeTurn() {
		if(turnNum == 1) {
			turnNum = 2;
		} else {
			turnNum = 1;
		}
	}
}
