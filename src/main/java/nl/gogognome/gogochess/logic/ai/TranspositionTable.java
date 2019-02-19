package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

/**
 * This class stores values for board positions, which are represented by a hash value.
 */
class TranspositionTable {

	public static class BoardPosition {

		private final int alpha;
		private final int beta;
		private final Move bestDeepestMove;
		private final MoveValue value;
		private final int depthInTree;

		BoardPosition(int alpha, int beta, Move bestDeepestMove, MoveValue value, int depthInTree) {
			this.alpha = alpha;
			this.beta = beta;
			this.bestDeepestMove = bestDeepestMove;
			this.value = value;
			this.depthInTree = depthInTree;
		}

		int getAlpha() {
			return alpha;
		}

		int getBeta() {
			return beta;
		}

		Move getBestDeepestMove() {
			return bestDeepestMove;
		}

		public MoveValue getValue() {
			return value;
		}

		int getDepthInTree() {
			return depthInTree;
		}

	}

	private final Map<Long, BoardPosition> boardHashToBestDeepestMove = new HashMap<>(500_000);

	void clear() {
		boardHashToBestDeepestMove.clear();
	}

	BoardPosition getCachedBoardPosition(long hash, int alpha, int beta, int depthInTree) {
		BoardPosition boardPosition = boardHashToBestDeepestMove.get(hash);
		if (boardPosition == null) {
			return null;
		}
		if (alpha != boardPosition.getAlpha() || beta != boardPosition.getBeta() || depthInTree != boardPosition.getDepthInTree()) {
			return null;
		}

		return boardPosition;
	}

	void store(long hash, int alpha, int beta, MoveValue value, int depthInTree, Move bestDeepestMove) {
		boardHashToBestDeepestMove.put(hash, new BoardPosition(alpha, beta, bestDeepestMove, value, depthInTree));
	}

}
