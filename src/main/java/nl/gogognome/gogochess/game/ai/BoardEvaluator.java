package nl.gogognome.gogochess.game.ai;

import nl.gogognome.gogochess.game.*;

public interface BoardEvaluator {

	int value(Board board, Status status, Player playerThatMadeLastMove);
}
