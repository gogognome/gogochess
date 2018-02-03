package nl.gogognome.gogochess.game.ai;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static nl.gogognome.gogochess.game.Status.CHECK_MATE;
import nl.gogognome.gogochess.game.*;

public class CheckMateBoardEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board, Status status, Player playerThatMadeLastMove) {
		if (status == CHECK_MATE) {
			return playerThatMadeLastMove == WHITE ? MAX_VALUE : MIN_VALUE;
		}
		return 0;
	}
}
