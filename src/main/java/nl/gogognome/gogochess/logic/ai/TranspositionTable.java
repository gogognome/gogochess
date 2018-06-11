package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

/**
 * This class stores values for board positions, which are represented by a hash value.
 */
public class TranspositionTable {

	public static class BoardPosition {

		private final int alpha;
		private final int beta;
		private final Move bestDeepestMove;
		private final int value;
		private final int depthInTree;

		public BoardPosition(int alpha, int beta, Move bestDeepestMove, int value, int depthInTree) {
			this.alpha = alpha;
			this.beta = beta;
			this.bestDeepestMove = bestDeepestMove;
			this.value = value;
			this.depthInTree = depthInTree;
		}

		public int getAlpha() {
			return alpha;
		}

		public int getBeta() {
			return beta;
		}

		public Move getBestDeepestMove() {
			return bestDeepestMove;
		}

		public int getValue() {
			return value;
		}

		public int getDepthInTree() {
			return depthInTree;
		}

	}

	private final Map<Long, BoardPosition> boardHashToBestDeepestMove = new HashMap<>(500_000);

	public void clear() {
		boardHashToBestDeepestMove.clear();
	}

	public BoardPosition getCachedBoardPosition(long hash, int alpha, int beta, int depthInTree) {
		BoardPosition boardPosition = boardHashToBestDeepestMove.get(hash);
		if (boardPosition == null) {
			return null;
		}
		if (alpha != boardPosition.getAlpha() || beta != boardPosition.getBeta() || depthInTree != boardPosition.getDepthInTree()) {
			return null;
		}

		return boardPosition;
	}

	public void store(long hash, int alpha, int beta, int value, int depthInTree, Move bestDeepestMove) {
		boardHashToBestDeepestMove.put(hash, new BoardPosition(alpha, beta, bestDeepestMove, value, depthInTree));
	}

}
