package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

class SingleMoveEvaluator {

    private final BiFunction<Board, Move, MoveValue> moveEvaluator;

    static SingleMoveEvaluator forConsumer(BiConsumer<Board, Move> moveEvaluator) {
        return new SingleMoveEvaluator(moveEvaluator);
    }

    static SingleMoveEvaluator forFunction(BiFunction<Board, Move, MoveValue> moveEvaluator) {
        return new SingleMoveEvaluator(moveEvaluator);
    }

    private SingleMoveEvaluator(BiConsumer<Board, Move> moveEvaluator) {
        this((board, move) -> {
            moveEvaluator.accept(board, move);
            return move.getValue();
        });
    }

    private SingleMoveEvaluator(BiFunction<Board, Move, MoveValue> moveEvaluator) {
        this.moveEvaluator = moveEvaluator;
    }

    MoveValue valueOfMove(BoardMutation... mutations) {
        Move setup = buildSetupMove(mutations);
        return valueOfMove(setup, mutations);
    }

    MoveValue valueOfMove(Move setup, BoardMutation... mutations) {
        Move move = new Move(setup, mutations);
        Board board = new Board();
        board.process(setup);
        return moveEvaluator.apply(board, move);
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
