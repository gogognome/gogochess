package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.Move;

import java.util.List;

public interface MovesEvaluator {

    void evaluate(Board board, List<Move> moves);
}
