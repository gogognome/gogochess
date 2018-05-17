package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.MoveValues.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

/**
 * This class evaluates the opening positions. Based on James J.Gillogly, "The Technology Chess Program" (1972).
 */
public class PositionalAnalysis {

	private static class SimpleMove {
		private final BoardMutation from;
		private final BoardMutation to;

		private SimpleMove(BoardMutation from, BoardMutation to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SimpleMove) {
				SimpleMove that = (SimpleMove) obj;
				return this.from.equals(that.from) && this.to.equals(that.to);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return from.hashCode() + 23 * to.hashCode();
		}
	}

	private final static Map<SimpleMove, Integer> SIMPLE_MOVE_TO_VALUE = ImmutableMap.<SimpleMove, Integer>builder()
				.put(new SimpleMove(WHITE_PAWN.removeFrom(E2), WHITE_PAWN.removeFrom(E4)), 30)
				.put(new SimpleMove(WHITE_PAWN.removeFrom(E3), WHITE_PAWN.removeFrom(E4)), 2)
				.put(new SimpleMove(WHITE_PAWN.removeFrom(D2), WHITE_PAWN.removeFrom(D4)), 20)
				.put(new SimpleMove(WHITE_PAWN.removeFrom(D3), WHITE_PAWN.removeFrom(D4)), 2)
				.put(new SimpleMove(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(A3)), -15)
				.put(new SimpleMove(WHITE_KNIGHT.removeFrom(G1), WHITE_KNIGHT.addTo(H3)), -15)
				.put(new SimpleMove(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(A6)), -15)
				.put(new SimpleMove(BLACK_KNIGHT.removeFrom(G8), BLACK_KNIGHT.addTo(H6)), -15)
			.build();

	private final static int[][] CENTER_CONTORL_ARRAY = new int[][] {
			{ 0, 1, 2, 3, 3, 2, 1, 0 },
			{ 1, 3, 4, 5, 5, 4, 3, 1 },
			{ 2, 4, 6, 7, 7, 6, 4, 2 },
			{ 3, 5, 7, 8, 8, 7, 5, 3 },
			{ 3, 5, 7, 8, 8, 7, 5, 3 },
			{ 2, 4, 6, 7, 7, 6, 4, 2 },
			{ 1, 3, 4, 5, 5, 4, 3, 1 },
			{ 0, 1, 2, 3, 3, 2, 1, 0 }
	};

	public void evaluate(Board board, List<Move> moves) {
		if (moves.isEmpty()) {
			return;
		}

		for (Move move : moves) {
			determineOpeningValue(board, move);
		}
	}

	private void determineOpeningValue(Board board, Move move) {
		if (move.depthInTree() > 15) {
			return;
		}

		BoardMutation from = move.getMutationRemovingPieceFromStart();
		BoardMutation to = move.getMutationAddingPieceAtDestination();
		int fromColumn = from.getSquare().column();
		int toColumn = to.getSquare().column();

		int value = negateForBlack(getCenterControlDelta(from, to), move);
		value += negateForBlack(getPawnOrKnightMoveValue(from, to), move);
		value += negateForBlack(getCastlingValue(from.getPlayerPiece().getPiece(), fromColumn, toColumn), move);
		value += getPieceBlocksWhiteCenterPawn(board, to);
		value += getPieceBlocksBlackCenterPawn(board, to);
		value += negateForBlack(getPieceMovingFromKingSideValue(fromColumn), move);
		value += negateForBlack(getPawnCaptureValue(board, move, from, to, toColumn), move);

		move.setValue(value);
	}

	private int getPawnOrKnightMoveValue(BoardMutation from, BoardMutation to) {
		int pawnOrKnightMoveValue = 0;
		Integer delta = SIMPLE_MOVE_TO_VALUE.get(new SimpleMove(from, to));
		if (delta != null) {
			pawnOrKnightMoveValue = delta;
		}
		return pawnOrKnightMoveValue;
	}

	private int getPawnCaptureValue(Board board, Move move, BoardMutation from, BoardMutation to, int toColumn) {
		int pawnCaptureValue = 0;
		if (from.getPlayerPiece().getPiece() == PAWN) {
			if (move.isCapture()) {
				if (isNearerToCenter(from.getSquare(), to.getSquare())) {
					pawnCaptureValue += 5;
				} else {
					pawnCaptureValue -= 5;
				}

				if (board.countNrPawnsInColumn(to.getPlayerPiece(), toColumn) > 1 && board.isIsolatedPawnInColumn(to.getPlayerPiece(), toColumn)) {
					pawnCaptureValue -= 10;
				}

				if (move.capturedPlayerPiece().getPiece() == PAWN && (toColumn == 3 || toColumn == 4)) {
					if (board.isIsolatedPawnInColumn(move.capturedPlayerPiece(), toColumn)) {
						pawnCaptureValue += 50;
					}
					int rowDelta = negateForBlack(1, move);
					Square leftForward = to.getSquare().addColumnAndRow(-1, rowDelta);
					Square rightForward = to.getSquare().addColumnAndRow(1, rowDelta);
					PlayerPiece pawnOfOpponent = new Pawn(move.getPlayer().other());
					if ((leftForward != null && pawnOfOpponent.equals(board.pieceAt(leftForward)))
							|| (rightForward != null && pawnOfOpponent.equals(board.pieceAt(rightForward)))) {
						pawnCaptureValue -= 15;
					}
				}
			} else {
				if (to.getSquare().column() == 0 || to.getSquare().column() == 7) {
					pawnCaptureValue -= 10;
				}
			}
		}
		return pawnCaptureValue;
	}

	private int getPieceMovingFromKingSideValue(int fromColumn) {
		int pieceMovingFromKingSideValue = 0;
		if (fromColumn >= 4) {
			pieceMovingFromKingSideValue = 2;
		}
		return pieceMovingFromKingSideValue;
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

	private int getCenterControlDelta(BoardMutation from, BoardMutation to) {
		int centerControlDelta = 0;
		centerControlDelta -= valueOf(from.getPlayerPiece().getPiece(), from.getSquare());
		centerControlDelta += valueOf(to.getPlayerPiece().getPiece(), to.getSquare());
		return centerControlDelta;
	}

	private int getCastlingValue(Piece movedPiece, int fromColumn, int toColumn) {
		int castlingValue = 0;
		if (movedPiece == KING && toColumn - fromColumn == 2) {
			castlingValue = 30;
		}

		if (movedPiece == KING && fromColumn - toColumn == 3) {
			castlingValue = 10;
		}
		return castlingValue;
	}

	private boolean isNearerToCenter(Square from, Square to) {
		return (from.column() < 4 && to.column() > from.column())
				|| (from.column() >= 4 && to.column() < from.column());
	}

	private int valueOf(Piece piece, Square square) {
		int pieceFactor = getPieceFactor(piece);
		int centerControlValue = CENTER_CONTORL_ARRAY[square.row()][square.column()];
		return pieceFactor * centerControlValue;
	}

	private int getPieceFactor(Piece piece) {
		switch (piece) {
			case PAWN: return 1;
			case KNIGHT: return 4;
			case BISHOP: return 3;
			case ROOK: return 2;
			case QUEEN: return 1;
			case KING: return -1;
			default: throw new IllegalArgumentException("The piece " + piece + " is not supported");
		}
	}
}
