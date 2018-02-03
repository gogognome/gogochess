package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Status.*;
import nl.gogognome.gogochess.game.*;

public class CheckMateBoardEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board, Status status, Player playerThatMadeLastMove) {
		if (status == CHECK_MATE) {
			return MoveValues.maxValue(playerThatMadeLastMove);
		}
		if (status == CHECK) {
			return MoveValues.add(0, 100, playerThatMadeLastMove);
		}
		return 0;
	}
}
