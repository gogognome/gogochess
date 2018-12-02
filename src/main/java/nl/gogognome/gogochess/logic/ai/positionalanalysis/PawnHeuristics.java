package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValues.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PawnHeuristics {

	private final int wingPawnAdvancementValue;

	PawnHeuristics(int wingPawnAdvancementValue) {
		this.wingPawnAdvancementValue = wingPawnAdvancementValue;
	}

	int getPawnHeuristicsForOpening(Board board, Move move, BoardMutation from, BoardMutation to) {
		return board.temporarilyMove(move, () -> {
			int value = getValueForWhitePawnMovingToD3_D4_E3_E4(from, to);
			value += getValueForPieceBlocksWhiteCenterPawn(board, to);
			value += getValueForPieceBlocksBlackCenterPawn(board, to);
			value += negateForBlack(getValueForPawnCapturingOtherPiece(board, move, from, to), move);
			value += negateForBlack(getValueForPawnOnSideOfBoard(from, to), move);
			return value;
		});
	}

	private int getValueForWhitePawnMovingToD3_D4_E3_E4(BoardMutation from, BoardMutation to) {
		int value = 0;
		if (from.getPlayerPiece().equals(WHITE_PAWN)) {
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
		return value;
	}

	private int getValueForPieceBlocksWhiteCenterPawn(Board board, BoardMutation to) {
		int pieceBlocksWhiteCenterPawn = 0;
		if (E3.equals(to.getSquare()) || D3.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRow(-1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksWhiteCenterPawn = -50;
			}
		}
		return pieceBlocksWhiteCenterPawn;
	}

	private int getValueForPieceBlocksBlackCenterPawn(Board board, BoardMutation to) {
		int pieceBlocksBlackCenterPawn = 0;
		if (E6.equals(to.getSquare()) || D6.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRow(1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksBlackCenterPawn = 50;
			}
		}
		return pieceBlocksBlackCenterPawn;
	}

	private int getValueForPawnCapturingOtherPiece(Board board, Move move, BoardMutation from, BoardMutation to) {
		if (!move.isCapture() || from.getPlayerPiece().getPiece() != PAWN) {
			return 0;
		}

		int pawnCaptureValue = 0;
		int toColumn = to.getSquare().column();
		if (isNearerToCenter(from.getSquare(), to.getSquare())) {
			pawnCaptureValue += 5;
		} else {
			pawnCaptureValue -= 5;
		}

		if (board.countNrOccurrencesInColumn(to.getPlayerPiece(), toColumn) > 1 && board.isIsolatedPawnInColumn(move.getPlayer(), toColumn)) {
			pawnCaptureValue -= 10;
		}

		if (move.capturedPlayerPiece().getPiece() == PAWN && (toColumn == 3 || toColumn == 4)) {
			if (board.isIsolatedPawnInColumn(move.getPlayer().opponent(), toColumn)) {
				pawnCaptureValue += 50;
			}
			int rowDelta = negateForBlack(1, move);
			Square leftForward = to.getSquare().addColumnAndRow(-1, rowDelta);
			Square rightForward = to.getSquare().addColumnAndRow(1, rowDelta);
			PlayerPiece pawnOfOpponent = new Pawn(move.getPlayer().opponent());
			if ((leftForward != null && pawnOfOpponent.equals(board.pieceAt(leftForward)))
					|| (rightForward != null && pawnOfOpponent.equals(board.pieceAt(rightForward)))) {
				pawnCaptureValue -= 15;
			}
		}
		return pawnCaptureValue;
	}

	private boolean isNearerToCenter(Square from, Square to) {
		return (from.column() < 4 && to.column() > from.column())
				|| (from.column() >= 4 && to.column() < from.column());
	}

	private int getValueForPawnOnSideOfBoard(BoardMutation from, BoardMutation to) {
		if (from.getPlayerPiece().getPiece() == PAWN && (to.getSquare().column() == 0 || to.getSquare().column() == 7)) {
			return wingPawnAdvancementValue;
		}
		return 0;
	}
}
