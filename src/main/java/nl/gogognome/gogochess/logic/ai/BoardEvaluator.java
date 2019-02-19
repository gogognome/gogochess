package nl.gogognome.gogochess.logic.ai;

import nl.gogognome.gogochess.logic.*;

public interface BoardEvaluator {

	MoveValue value(Board board);
}
