import java.util.ArrayList;

public class Node {
	//class fields
	public GameBoard game;
	public int depth;
	public int column; //the column that was played to get to this node
	public int value;
	public ArrayList<Node> successors;
	
	//constructor
	public Node(GameBoard game, int depth, int column) {
		this.game = game;
		this.depth = depth;
		this.column = column;
	}
	
	public ArrayList<Node> getSuccessors() {
		ArrayList<Node> successors = new ArrayList<Node>();
		for(int column = 0; column < GameBoard.NUM_COLS; column++) {
			if(game.isPlayValid(column)) {
				GameBoard tempGame = new GameBoard(game);
				tempGame.playPiece(column);
				successors.add(new Node(tempGame, depth+1, column));
			}
		}
		
		return successors;
	}
}
