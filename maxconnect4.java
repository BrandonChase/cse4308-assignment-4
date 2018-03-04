import java.util.Scanner;

/**
 * @author Brandon Chase, based on code by James Spargo
 * 
 * This class controls the game play for the Max Connect-Four game. 
 * To compile the program, use the following command from the maxConnectFour directory:
 * javac *.java
 *
 * the usage to run the program is as follows:
 * ( again, from the maxConnectFour directory )
 *
 *  -- for interactive mode:
 * java MaxConnectFour interactive [ input_file ] [ computer-next / human-next ] [ search depth]
 *
 * -- for one move mode
 * java maxConnectFour.MaxConnectFour one-move [ input_file ] [ output_file ] [ search depth]
 * 
 * description of arguments: 
 *  [ input_file ]
 *  -- the path and filename of the input file for the game
 *  
 *  [ computer-next / human-next ]
 *  -- the entity to make the next move. either computer or human. can be abbreviated to either C or H. This is only used in interactive mode
 *  
 *  [ output_file ]
 *  -- the path and filename of the output file for the game.  this is only used in one-move mode
 *  
 *  [ search depth ]
 *  -- the depth of the minimax search algorithm
 * 
 *   
 */

public class maxconnect4 {
	public static void main(String[] args) {
		try {
			//check for correct number of arguments
			if(args.length != 4) {
				System.out.println("Four command-line arguments are needed:\n"
									+"Usage: java [program name] interactive [input_file] [computer-next / human-next] [depth]\n"
									+ " or:  java [program name] one-move [input_file] [output_file] [depth]\n");
				System.exit(0);
			}
			
			//Process arguments
			String gameMode = args[0].toString(); //specifies interactive or one-move mode
			String inputFileName = args[1].toString(); //name of input game file
			int depth = Integer.parseInt(args[3]); //depth level of AI depth-limited search
			
			//Create game board and AI Player
			GameBoard currentGame = new GameBoard(inputFileName);
			AIPlayer ai = new AIPlayer();
			
			//Play specified mode 
			switch(gameMode.toLowerCase()) {
			case "interactive":
				String startingPlayer = args[2].toString();
				playInteractiveMode(currentGame, ai, startingPlayer, depth);
				break;
			case "one-move":
				String outputFileName = args[2].toString();
				playOneMoveMode(currentGame, ai, outputFileName, depth);
				break;
			default:
				System.out.println("ERROR: '" + gameMode + "' is an unsupported game mode! Try again.");
				break;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void playInteractiveMode(GameBoard currentGame, AIPlayer ai, String startingPlayer, int depth) throws Exception {
		int humanNumber; //values on board are either 1 or 2. based on input file and program arguments, player's value will either be 1 or 2
		if(startingPlayer.equalsIgnoreCase("human-next")) {
			humanNumber = currentGame.getCurrentTurn();
		} else {
			humanNumber = 3 - currentGame.getCurrentTurn(); //only options are 1 or 2; if turn is 1, 3 - 1 = 2 and if turn is 2, 3 - 2 = 1
		}
		
		Scanner reader = new Scanner(System.in);
		while(!currentGame.isGameOver()) {
			if(currentGame.getCurrentTurn() == humanNumber) {
				currentGame.print();
				System.out.print("Enter column to play: ");
				int column = reader.nextInt();
				if(currentGame.isPlayValid(column)) {
					currentGame.playPiece(column);
					currentGame.save("human.txt");
				} else {
					System.out.println("Invalid move!");
				}
			} else {
				currentGame.print();
				currentGame.playPiece(ai.calculateBestPlay(currentGame, depth));
				currentGame.save("computer.txt");
			}
		}
		
		System.out.println("The game is over.");
		reader.close();
	}
	
	private static void playOneMoveMode(GameBoard currentGame, AIPlayer ai, String outputFileName, int depth) throws Exception {
		currentGame.print();
		if(!currentGame.isGameOver()) {
			ai.calculateBestPlay(currentGame, depth);
			currentGame.print();
		}
		
		currentGame.save(outputFileName);
	}
}
