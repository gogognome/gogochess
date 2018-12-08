package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.BoardMutation;
import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.Player;

import java.util.Arrays;
import java.util.function.BiConsumer;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.REMOVE;

class SingleMoveEvaluator {

    private final BiConsumer<Board, Move> moveEvaluator;

    SingleMoveEvaluator(BiConsumer<Board, Move> moveEvaluator) {
        this.moveEvaluator = moveEvaluator;
    }

    int valueOfMove(BoardMutation... mutations) {
        Move setup = buildSetupMove(mutations);
        return valueOfMove(setup, mutations);
    }

    int valueOfMove(Move setup, BoardMutation... mutations) {
        Move move = new Move(setup, mutations);
        Board board = new Board();
        board.process(setup);
        moveEvaluator.accept(board, move);
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
