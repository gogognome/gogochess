package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.Player;
import nl.gogognome.gogochess.logic.Square;
import nl.gogognome.gogochess.logic.piece.PlayerPiece;

import java.util.concurrent.atomic.AtomicInteger;

import static nl.gogognome.gogochess.logic.Piece.PAWN;
import static nl.gogognome.gogochess.logic.Player.WHITE;

public class PassedPawnFieldHeuristic {

    private final static int[][] OWN_WHITE_PASSED_PAWN_FIELD = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {8, 12, 0, 12, 8},
            {3, 5, 4, 5, 3},
            {4, 6, 5, 6, 4},
            {1, 2, 1, 2, 1},
            {0, 0, 0, 0, 0}
    };

    private final static int[][] OPPONENTS_WHITE_PASSED_PAWN_FIELD = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {8, 12, 0, 12, 8},
            {3, 5, 8, 5, 3},
            {4, 6, 7, 6, 4},
            {1, 2, 5, 2, 1},
            {0, 0, 1, 0, 0}
    };

    int getDeltaForPassedPawns(Board board, Square from, Square to, Player player) {
        AtomicInteger delta = new AtomicInteger(0);
        board.forEachPlayerPiece(player, (playerPiece, square) -> delta.addAndGet(deltaForPawnAt(board, playerPiece, square, from , to, player)));
        board.forEachPlayerPiece(player.opponent(), (playerPiece, square) -> delta.addAndGet(deltaForPawnAt(board, playerPiece, square, from , to, player)));
        return delta.get();
    }

    private int deltaForPawnAt(Board board, PlayerPiece playerPiece, Square playerPieceSquare, Square from, Square to, Player currentPlayer) {
        if (playerPiece.getPiece() != PAWN || !board.isPassedPawn(playerPiece, playerPieceSquare)) {
            return 0;
        }

        boolean ownPawn = playerPiece.getPlayer() == currentPlayer;
        return fieldValue(playerPiece.getPlayer(), ownPawn, playerPieceSquare, to) - fieldValue(playerPiece.getPlayer(), ownPawn, playerPieceSquare, from);
    }

    private int fieldValue(Player pawnPlayer, boolean ownPawn, Square pawnSquare, Square square) {
        int[][] field = ownPawn ? OWN_WHITE_PASSED_PAWN_FIELD : OPPONENTS_WHITE_PASSED_PAWN_FIELD;
        int fieldColumn = square.file() - pawnSquare.file() + 2;
        int fieldRow = (pawnPlayer == WHITE ? -1 : 1) * (square.file() - pawnSquare.file()) + 4;

        if (0 <= fieldColumn && fieldColumn < 5 && 0 <= fieldRow && fieldRow < 9) {
            return field[fieldRow][fieldColumn];
        } else {
            return 0;
        }
    }
}
