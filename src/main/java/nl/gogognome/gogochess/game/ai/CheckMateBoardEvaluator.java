package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Status.*;
import nl.gogognome.gogochess.game.*;

public class CheckMateBoardEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board) {
		Status status = board.lastMove().getStatus();
		if (status == CHECK_MATE) {
			return MoveValues.maxValue(board.lastMove().getPlayer());
		}
		if (status == CHECK) {
			return MoveValues.add(0, 10000, board.lastMove().getPlayer());
		}
		return 0;
	}
}
