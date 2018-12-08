package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.Move;

import java.util.List;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;

public class PositionalAnalysisForEndGame implements MovesEvaluator {

    private final PassedPawnFieldHeuristic passedPawnFieldHeuristic;

    PositionalAnalysisForEndGame(PassedPawnFieldHeuristic passedPawnFieldHeuristic) {
        this.passedPawnFieldHeuristic = passedPawnFieldHeuristic;
    }

    @Override
    public void evaluate(Board board, List<Move> moves) {
        for (Move move : moves) {
            int value = negateForBlack(passedPawnFieldHeuristic.getDeltaForPassedPawns(board, move), move);

            move.setValue(value);
        }
    }
}
