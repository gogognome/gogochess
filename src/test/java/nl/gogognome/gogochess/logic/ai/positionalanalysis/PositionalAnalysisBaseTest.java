package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.BoardMutation;
import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.Player;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.REMOVE;

abstract class PositionalAnalysisBaseTest {

    private final MovesEvaluator movesEvaluator;

    protected PositionalAnalysisBaseTest(MovesEvaluator movesEvaluator) {
        this.movesEvaluator = movesEvaluator;
    }

    protected int valueOfMove(BoardMutation... mutations) {
        Move setup = buildSetupMove(mutations);
        return valueOfMove(setup, mutations);
    }

    int valueOfMove(Move setup, BoardMutation... mutations) {
        Move move = new Move(setup, mutations);
        Board board = new Board();
        board.process(setup);
        movesEvaluator.evaluate(board, asList(move));
        return move.getValue();
    }

    private Move buildSetupMove(BoardMutation[] mutations) {
        Player player = mutations[0].getPlayerPiece().getPlayer().opponent();
        return new Move(player,
                Arrays.stream(mutations)
                        .filter(m -> m.getMutation() == REMOVE)
                        .map(m -> new BoardMutation(m.getPlayerPiece(), m.getSquare(), ADD))
                        .toArray(BoardMutation[]::new));
    }
}
