package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Status.*;
import nl.gogognome.gogochess.game.*;

public class CheckMateBoardEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board, Status status) {
		if (status == CHECK_MATE) {
			return MoveValues.maxValue(board.lastMove().getPlayer());
		}
		if (status == CHECK) {
			return MoveValues.add(0, 100, board.lastMove().getPlayer());
		}
		return 0;
	}
}
