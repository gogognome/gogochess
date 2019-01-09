package nl.gogognome.gogochess.logic.ai;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.movenotation.MoveNotation;

import java.util.List;

/**
 * Sets up a board in the initial position and replays moves.
 * The moves are passed in as strings in the configured notation.
 */
class BoardSetup {

    private final MoveNotation moveNotation;

    BoardSetup(MoveNotation moveNotation) {
        this.moveNotation = moveNotation;
    }

    void setupBoard(Board board, String... moves) {
        board.initBoard();
        for (String move : moves) {
            processMove(board, move);
        }
    }

    private void processMove(Board board, String move) {
        List<Move> validMoves = board.currentPlayer().validMoves(board);
        for (Move validMove : validMoves) {
            if (move.equals(moveNotation.format(validMove))) {
                board.process(validMove);
                return;
            }
        }
        throw new IllegalArgumentException("The move " + move + " is invalid");
    }
}
