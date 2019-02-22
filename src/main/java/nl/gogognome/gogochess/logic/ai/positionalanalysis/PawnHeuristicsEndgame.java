package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Square.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PawnHeuristicsEndgame {

    private final static int[] OPPOSED_PAWN_SCORE_PER_RANK = new int[] { 0, 0, 2, 1, 3, 4, 0, 0 };
    private final static int[] UNOPPOSED_PAWN_SCORE_PER_RANK = new int[] { 0, 0, 3, 5, 10, 13, 23, 80 };

    int getPawnHeuristicsForEndgame(Board board, BoardMutation from, BoardMutation to) {
        if (!from.getPlayerPiece().getPiece().equals(PAWN)) {
            return 0;
        }

        if (isBlockedByPawn(board, from, (Pawn) from.getPlayerPiece())) {
            return -10;
        } else {
            int[] scores = isBlockedByPawn(board, from, new Pawn(from.getPlayerPiece().getPlayer().opponent())) ? OPPOSED_PAWN_SCORE_PER_RANK : UNOPPOSED_PAWN_SCORE_PER_RANK;
            int rank = to.getSquare().rank();
            int index = from.getPlayerPiece().getPlayer() == Player.WHITE ? rank : RANK_8 - rank;
            return scores[index];
        }
    }

    private boolean isBlockedByPawn(Board board, BoardMutation from, Pawn blockinPawn) {
        Player player = from.getPlayerPiece().getPlayer();
        int delta = player == Player.WHITE ? 1 : -1;
        for (Square square = from.getSquare().addRanks(delta); square != null; square = square.addRanks(delta)) {
            PlayerPiece opposingPiece = board.pieceAt(square);
            if (opposingPiece != null && opposingPiece.equals(blockinPawn)) {
                return true;
            }
        }
        return false;
    }

}
