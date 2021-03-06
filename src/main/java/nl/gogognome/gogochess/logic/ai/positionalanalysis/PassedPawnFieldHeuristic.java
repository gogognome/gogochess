package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.PlayerPiece;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

import static nl.gogognome.gogochess.logic.Piece.PAWN;
import static nl.gogognome.gogochess.logic.Player.BLACK;

class PassedPawnFieldHeuristic {

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

    int getDeltaForPassedPawns(Board board, Move move) {
        BoardMutation mutationRemovingPieceFromStart = move.getMutationRemovingPieceFromStart();
        Square from = mutationRemovingPieceFromStart.getSquare();
        Square to = move.getMutationAddingPieceAtDestination().getSquare();
        Player player = move.getPlayer();
        PlayerPiece movedPlayerPiece = mutationRemovingPieceFromStart.getPlayerPiece();

        AtomicInteger delta = new AtomicInteger(0);
        if (movedPlayerPiece.getPiece() == PAWN && board.isPassedPawn(movedPlayerPiece, from)) {
            BiPredicate<PlayerPiece, Square> isNotMovedPiece =
                    (playerPiece, square) -> !playerPiece.equals(movedPlayerPiece);
            board.forEachPlayerPieceWhere(player, isNotMovedPiece,
                    (playerPiece, square) -> delta.addAndGet(deltaForPieceAt(playerPiece, square, from, to, player)));
            board.forEachPlayerPieceWhere(player.opponent(), isNotMovedPiece,
                    (playerPiece, square) -> delta.addAndGet(deltaForPieceAt(playerPiece, square, from, to, player)));
        }

        BiPredicate<PlayerPiece, Square> isNotMovedPieceAndIsPassedPawn =
                (playerPiece, square) -> !playerPiece.equals(movedPlayerPiece) && playerPiece.getPiece() == PAWN && board.isPassedPawn(playerPiece, square);
        board.forEachPlayerPieceWhere(player, isNotMovedPieceAndIsPassedPawn,
                (playerPiece, square) -> delta.addAndGet(deltaForPawnAt(playerPiece, square, from, to, player)));
        board.forEachPlayerPieceWhere(player.opponent(), isNotMovedPieceAndIsPassedPawn,
                (playerPiece, square) -> delta.addAndGet(deltaForPawnAt(playerPiece, square, from, to, player)));

        return delta.get();
    }

    private int deltaForPieceAt(PlayerPiece playerPiece, Square square, Square from, Square to, Player currentPlayer) {
        boolean ownPawn = playerPiece.getPlayer() == currentPlayer;
        return fieldValue(playerPiece.getPlayer(), ownPawn, from, square) - fieldValue(currentPlayer, ownPawn, to, square);
    }

    private int deltaForPawnAt(PlayerPiece playerPiece, Square playerPieceSquare, Square from, Square to, Player currentPlayer) {
        boolean ownPawn = playerPiece.getPlayer() == currentPlayer;
        return fieldValue(playerPiece.getPlayer(), ownPawn, playerPieceSquare, to) - fieldValue(playerPiece.getPlayer(), ownPawn, playerPieceSquare, from);
    }

    private int fieldValue(Player pawnPlayer, boolean ownPawn, Square pawnSquare, Square square) {
        int[][] field = ownPawn ? OWN_WHITE_PASSED_PAWN_FIELD : OPPONENTS_WHITE_PASSED_PAWN_FIELD;
        int fieldColumn = square.file() - pawnSquare.file() + 2;
        int fieldRow = (pawnPlayer == BLACK ? -1 : 1) * (square.rank() - pawnSquare.rank()) + 4;

        if (0 <= fieldColumn && fieldColumn < 5 && 0 <= fieldRow && fieldRow < 9) {
            return field[fieldRow][fieldColumn];
        } else {
            return 0;
        }
    }
}
