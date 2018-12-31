package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.BoardMutation;
import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.Player;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.REMOVE;

class SingleMoveEvaluator {

    private final BiFunction<Board, Move, Integer> moveEvaluator;

    static SingleMoveEvaluator forConsumer(BiConsumer<Board, Move> moveEvaluator) {
        return new SingleMoveEvaluator(moveEvaluator);
    }

    static SingleMoveEvaluator forFunction(BiFunction<Board, Move, Integer> moveEvaluator) {
        return new SingleMoveEvaluator(moveEvaluator);
    }

    private SingleMoveEvaluator(BiConsumer<Board, Move> moveEvaluator) {
        this((board, move) -> {
            moveEvaluator.accept(board, move);
            return move.getValue(); });
    }

    private SingleMoveEvaluator(BiFunction<Board, Move, Integer> moveEvaluator) {
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
