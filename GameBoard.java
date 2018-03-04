import java.io.*;
import java.util.ArrayList;

/**
 * @author Brandon Chase, based on code by James Spargo
 * 
 * This is the Gameboard class.  It implements a two dimension array that
 * represents a connect four gameboard. It keeps track of the player making
 * the next play based on the number of pieces on the game board. It provides
 * all of the methods needed to implement the playing of a max connect four
 * game.
 */

public class GameBoard {
	//Board dimensions
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLS = 7;
	
	//Class fields
	private int[][] board;
	private int turnNum;
	private int numPieces;
	
	//Constructor
	public GameBoard(String inputFileName) throws Exception {
		board = new int[NUM_ROWS][NUM_COLS];
		numPieces = 0;
		
		//Open input file
		BufferedReader input = new BufferedReader(new FileReader(inputFileName));
		
		//Read game data
		ArrayList<String> data = new ArrayList<>();
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
	
	private String turnToString(int turn) {
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
