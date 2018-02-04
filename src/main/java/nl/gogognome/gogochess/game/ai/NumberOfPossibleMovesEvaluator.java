package nl.gogognome.gogochess.game.ai;

import java.util.*;
import nl.gogognome.gogochess.game.*;

public class NumberOfPossibleMovesEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board) {
		List<Move> followingMoves = board.lastMove().getFollowingMoves();
		if (followingMoves != null) {
			return MoveValues.add(0, followingMoves.size(), board.lastMove().getPlayer());
		}
		return 0;
	}
}
