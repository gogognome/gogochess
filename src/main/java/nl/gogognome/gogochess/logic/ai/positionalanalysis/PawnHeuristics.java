package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Piece.PAWN;
import static nl.gogognome.gogochess.logic.Squares.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

public class PawnHeuristics {


	public int getPawnHeuristicsForOpening(Board board, BoardMutation from, BoardMutation to) {
		int value = 0;
		PlayerPiece playerPiece = from.getPlayerPiece();
		if (playerPiece.equals(WHITE_PAWN)) {
			if (from.getSquare().equals(E2) && to.getSquare().equals(E4)) {
				value += 30;
			}
			if (from.getSquare().equals(E3) && to.getSquare().equals(E4)) {
				value += 2;
			}
			if (from.getSquare().equals(D2) && to.getSquare().equals(D4)) {
				value += 20;
			}
			if (from.getSquare().equals(D3) && to.getSquare().equals(D4)) {
				value += 2;
			}
		}

		value += getPieceBlocksWhiteCenterPawn(board, to);
		value += getPieceBlocksBlackCenterPawn(board, to);

		return value;
	}

	private int getPieceBlocksWhiteCenterPawn(Board board, BoardMutation to) {
		int pieceBlocksWhiteCenterPawn = 0;
		if (E3.equals(to.getSquare()) || D3.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRow(-1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksWhiteCenterPawn = -50;
			}
		}
		return pieceBlocksWhiteCenterPawn;
	}

	private int getPieceBlocksBlackCenterPawn(Board board, BoardMutation to) {
		int pieceBlocksBlackCenterPawn = 0;
		if (E6.equals(to.getSquare()) || D6.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRow(1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksBlackCenterPawn = 50;
			}
		}
		return pieceBlocksBlackCenterPawn;
	}

}
