package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.MoveValues.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
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
				.put(new SimpleMove(WHITE_KNIGHT.removeFrom(B8), WHITE_KNIGHT.addTo(A6)), -15)
				.put(new SimpleMove(WHITE_KNIGHT.removeFrom(G8), WHITE_KNIGHT.addTo(H6)), -15)
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

		int value = 0;
		BoardMutation from = move.getMutationRemovingPieceFromStart();
		BoardMutation to = move.getMutationAddingPieceAtDestination();
		value -= negateForBlack(valueOf(from.getPlayerPiece().getPiece(), from.getSquare()), move.getPlayer());
		value += negateForBlack(valueOf(to.getPlayerPiece().getPiece(), to.getSquare()), move.getPlayer());

		Integer delta = SIMPLE_MOVE_TO_VALUE.get(new SimpleMove(from, to));
		if (delta != null) {
			value += delta;
		}

		int fromColumn = from.getSquare().column();
		int toColumn = to.getSquare().column();
		if (from.getPlayerPiece().getPiece() == KING && toColumn - fromColumn == 2) {
			value += 30;
		}

		if (from.getPlayerPiece().getPiece() == KING && fromColumn - toColumn == 3) {
			value += 10;
		}
		if (E3.equals(to.getSquare()) || D3.equals(to.getSquare())) {
			PlayerPiece blockedPiece = board.pieceAt(to.getSquare().addRow(-1));
		    if (blockedPiece != null && blockedPiece.getPiece() == PAWN) {
				value -= 50;
			}
		}

		if (fromColumn >= 4 && toColumn < 4) {
			value += negateForBlack(2, move.getPlayer());
		}

		if (from.getPlayerPiece().getPiece() == PAWN) {
			if (move.isCapture()) {
				if (isNearerToCenter(from.getSquare(), to.getSquare())) {
					value += negateForBlack(5, move.getPlayer());
				} else {
					value -= negateForBlack(5, move.getPlayer());
				}

				if (board.countNrPawnsInColumn(to.getPlayerPiece(), toColumn) > 1 && board.isIsolatedPawnInColumn(to.getPlayerPiece(), toColumn)) {
					value -= negateForBlack(10, move.getPlayer());
				}

				if (move.capturedPlayerPiece().getPiece() == PAWN && (toColumn == 3 || toColumn == 4)) {
					if (board.isIsolatedPawnInColumn(move.capturedPlayerPiece(), toColumn)) {
						value += negateForBlack(50, move.getPlayer());
					}
					int rowDelta = move.getPlayer() == WHITE ? 1 : -1;
					Square leftForward = to.getSquare().addColumnAndRow(-1, rowDelta);
					Square rightForward = to.getSquare().addColumnAndRow(1, rowDelta);
					PlayerPiece pawnOfOpponent = new Pawn(move.getPlayer().other());
					if ((leftForward != null && pawnOfOpponent.equals(board.pieceAt(leftForward)))
							|| (rightForward != null && pawnOfOpponent.equals(board.pieceAt(rightForward)))) {
						value -= negateForBlack(15, move.getPlayer());
					}
				}
			} else {
				if (to.getSquare().column() == 0 || to.getSquare().column() == 7) {
					value -= negateForBlack(10, move.getPlayer());
				}
			}
		}

		move.setValue(value);
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
