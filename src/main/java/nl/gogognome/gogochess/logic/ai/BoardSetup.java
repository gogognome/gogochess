package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

/**
 * Sets up a board in the initial position and replays moves.
 * The moves are passed in as strings in the configured notation.
 */
public class BoardSetup {

    private final MoveNotation moveNotation;

    public BoardSetup(MoveNotation moveNotation) {
        this.moveNotation = moveNotation;
    }

    public List<Move> parseMoves(String... moves) {
        Board board = new Board();
        board.initBoard();
        List<Move> parsedMoves = new ArrayList<>();
        for (String move : moves) {
            Move parsedMove = parseMove(board, move);
            parsedMoves.add(parsedMove);
            board.process(parsedMove);
        }
        return parsedMoves;
    }

    void setupBoard(Board board, String... moves) {
        board.initBoard();
        for (String move : moves) {
            processMove(board, move);
        }
    }

    private void processMove(Board board, String move) {
        Move parsedMove = parseMove(board, move);
        board.process(parsedMove);
    }

    private Move parseMove(Board board, String move) {
        Move parsedMove = null;
        List<Move> validMoves = board.currentPlayer().validMoves(board);
        for (Move validMove : validMoves) {
            if (move.equals(moveNotation.format(validMove))) {
                parsedMove = validMove;
                break;
            }
        }
        if (parsedMove == null) {
            throw new IllegalArgumentException("The move " + move + " is invalid");
        }
        return parsedMove;
    }
}
