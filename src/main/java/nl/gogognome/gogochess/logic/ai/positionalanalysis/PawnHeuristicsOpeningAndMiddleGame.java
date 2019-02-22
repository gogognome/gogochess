package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PawnHeuristicsOpeningAndMiddleGame {

	private final int wingPawnAdvancementValue;

	PawnHeuristicsOpeningAndMiddleGame(int wingPawnAdvancementValue) {
		this.wingPawnAdvancementValue = wingPawnAdvancementValue;
	}

	MoveValue getPawnHeuristicsForOpeningAndMiddleGame(Board board, Move move, BoardMutation from, BoardMutation to) {
		return board.temporarilyMove(move, () -> getValueForWhitePawnMovingToD3_D4_E3_E4(from, to)
			.add(getValueForPieceBlocksWhiteCenterPawn(board, to), "piece blocks white center pawn")
			.add(getValueForPieceBlocksBlackCenterPawn(board, to), "piece blocks black center pawn")
			.add(getValueForPawnCapturingOtherPiece(board, move, from, to), "pawn capturing oher piece")
			.add(getValueForPawnOnSideOfBoard(from, to), move, "pawn on side of board"));
	}

	private MoveValue getValueForWhitePawnMovingToD3_D4_E3_E4(BoardMutation from, BoardMutation to) {
		MoveValue value = MoveValue.ZERO;
		if (from.getPlayerPiece().equals(WHITE_PAWN)) {
			if (from.getSquare().equals(E2) && to.getSquare().equals(E4)) {
				value = value.addForWhite(30, "white pawn moving from e2 to e4");
			}
			if (from.getSquare().equals(E3) && to.getSquare().equals(E4)) {
				value = value.addForWhite(2, "white pawn moving from e3 to e4");
			}
			if (from.getSquare().equals(D2) && to.getSquare().equals(D4)) {
				value = value.addForWhite(20, "white pawn moving from d2 to d4");
			}
			if (from.getSquare().equals(D3) && to.getSquare().equals(D4)) {
				value = value.addForWhite(2, "white pawn moving from d3 to d4");
			}
		}
		return value;
	}

	private MoveValue getValueForPieceBlocksWhiteCenterPawn(Board board, BoardMutation to) {
		MoveValue pieceBlocksWhiteCenterPawn = MoveValue.ZERO;
		if (E3.equals(to.getSquare()) || D3.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRanks(-1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksWhiteCenterPawn = pieceBlocksWhiteCenterPawn.addForBlack(50, "piece blocks white center pawn");
			}
		}
		return pieceBlocksWhiteCenterPawn;
	}

	private MoveValue getValueForPieceBlocksBlackCenterPawn(Board board, BoardMutation to) {
		MoveValue pieceBlocksBlackCenterPawn = MoveValue.ZERO;
		if (E6.equals(to.getSquare()) || D6.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRanks(1));
			if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				pieceBlocksBlackCenterPawn = pieceBlocksBlackCenterPawn.addForWhite(50, "piece blocks blackcenter pawn");
			}
		}
		return pieceBlocksBlackCenterPawn;
	}

	private MoveValue getValueForPawnCapturingOtherPiece(Board board, Move move, BoardMutation from, BoardMutation to) {
		if (!move.isCapture() || from.getPlayerPiece().getPiece() != PAWN) {
			return MoveValue.ZERO;
		}

		MoveValue pawnCaptureValue = MoveValue.ZERO;
		int toColumn = to.getSquare().file();
		if (isNearerToCenter(from.getSquare(), to.getSquare())) {
			pawnCaptureValue = pawnCaptureValue.add(5, move, "pawn capture brings pawn nearer to center");
		} else {
			pawnCaptureValue = pawnCaptureValue.add(-5, move, "pawn capture takes pawn further from center");
		}

		if (board.countNrOccurrencesInFile(to.getPlayerPiece(), toColumn) > 1 && board.isIsolatedPawnInFile(move.getPlayer(), toColumn)) {
			pawnCaptureValue = pawnCaptureValue.add(-10, move, "multiple pawns in file");
		}

		if (move.capturedPlayerPiece().getPiece() == PAWN && (toColumn == 3 || toColumn == 4)) {
			if (board.isIsolatedPawnInFile(move.getPlayer().opponent(), toColumn)) {
				pawnCaptureValue = pawnCaptureValue.add(50, move, "capture isolated center pawn");
			}
			int rowDelta = move.getPlayer() == WHITE ? 1 : -1;
			Square leftForward = to.getSquare().addFilesAndRanks(-1, rowDelta);
			Square rightForward = to.getSquare().addFilesAndRanks(1, rowDelta);
			PlayerPiece pawnOfOpponent = new Pawn(move.getPlayer().opponent());
			if ((leftForward != null && pawnOfOpponent.equals(board.pieceAt(leftForward)))
					|| (rightForward != null && pawnOfOpponent.equals(board.pieceAt(rightForward)))) {
				pawnCaptureValue = pawnCaptureValue.add(-15, move, "capture supported center pawn");
			}
		}
		return pawnCaptureValue;
	}

	private boolean isNearerToCenter(Square from, Square to) {
		return (from.file() < 4 && to.file() > from.file())
				|| (from.file() >= 4 && to.file() < from.file());
	}

	private int getValueForPawnOnSideOfBoard(BoardMutation from, BoardMutation to) {
		if (from.getPlayerPiece().getPiece() == PAWN && (to.getSquare().file() == 0 || to.getSquare().file() == 7)) {
			return wingPawnAdvancementValue;
		}
		return 0;
	}

}
