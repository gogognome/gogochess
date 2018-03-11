package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class NumberOfPossibleMovesEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board) {
		List<Move> followingMoves = board.validMoves();
		if (followingMoves != null) {
			return MoveValues.negateForBlack(followingMoves.size(), board.lastMove().getPlayer());
		}
		return 0;
	}
}
