package nl.gogognome.gogochess.logic.ai;

import nl.gogognome.gogochess.logic.*;

public class MiniMax {

	/**
	 * Update value of the move preceding <code>move</code> up to and including <code>stopAtMove</code>.
	 * @param move the preceding move of this move will be updated first
	 * @param stopAtMove the last move that needs to be updated
	 */
	public void updateValuesInPrecedingMoves(Move move, Move stopAtMove) {
		move = move.getPrecedingMove();
		while (move != null && move.depthInTree() >= stopAtMove.depthInTree()) {
			Player player = move.getPlayer().other();
			int bestValue = MoveValues.minValue(player);
			for (Move followingMove : move.getFollowingMoves()) {
				if (MoveValues.compareTo(followingMove.getValue(), bestValue, player) > 0) {
					bestValue = followingMove.getValue();
				}
			}
//			if (move.getValue() != bestValue) {
//				System.out.println("changing value of " + System.identityHashCode(move) + " " + move.getDescription() + " from " + move.getValue() + " to " + bestValue +  " (level " + move.depthInTree() + ")");
//			}
			move.setValue(bestValue);
			move = move.getPrecedingMove();
		}
	}

}
