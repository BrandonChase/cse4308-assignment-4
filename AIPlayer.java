public class AIPlayer {
	//class fields
	public int playerNumber;
	public int depthLimit;
	
	//constructor
	public AIPlayer(int playerNumber, int depth) {
		this.playerNumber = playerNumber;
		this.depthLimit = depth;
	}
	public int calculateBestPlay(GameBoard currentGame) {
		if(depthLimit == 0) { //if depth limit is 0 (stupid AI), just choose lowest valid column
			for(int column = 0; column < GameBoard.NUM_COLS; column++)
			{
				if(currentGame.isPlayValid(column)) {
					return column;
				}
			}
		}
		
		Node root = new Node(currentGame, 0, -1);
		int bestValue = maxValue(root, -999_999, 999_9995);
		for(Node successor : root.successors ) {
			if(successor.value == bestValue)
			{
				return successor.column;
			}
		}
		
		//should never reach here
		return -1;
	}
	
	private int maxValue(Node node, int alpha, int beta) {
		if(node.game.isGameOver() || node.depth == depthLimit) {
			node.value = node.game.calculateUtility(playerNumber);
			return node.value;
		}
		
		int value = -999_999;
		node.successors = node.getSuccessors();
		for(Node successor : node.successors) {
			value = Math.max(value, minValue(successor, alpha, beta));
			if(value >= beta) {
				node.value = value;
				return value;
			}
			
			alpha = Math.max(alpha, value);
		}
		node.value = value;
		return value;
	}
	
	private int minValue(Node node, int alpha, int beta) {
		if(node.game.isGameOver() || node.depth == depthLimit) {
			node.value = node.game.calculateUtility(playerNumber);
			return node.value;
		}
		
		int value = 999_999;
		node.successors = node.getSuccessors();
		for(Node successor : node.successors) {
			value = Math.min(value, maxValue(successor, alpha, beta));
			if(value <= alpha) {
				node.value = value;
				return value;
			}
			
			beta = Math.min(beta, value);
		}
		
		node.value = value;
		return value;
	}
}
