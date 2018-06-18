package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Status.*;
import nl.gogognome.gogochess.logic.*;

public class EndOfGameBoardEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board) {
		Status status = board.lastMove().getStatus();
		if (status == CHECK_MATE) {
			return MoveValues.maxValue(board.lastMove().getPlayer(), board.lastMove().depthInTree());
		}
		return 0;
	}
}
