package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValues.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

/**
 * This class evaluates the opening positions. Based on James J.Gillogly, "The Technology Chess Program" (1972).
 */
class PositionalAnalysisForOpening {

	private final CentralControlHeuristic centralControlHeuristic;
	private final PawnHeuristics pawnHeuristics;

	private final static Map<SimpleMove, Integer> SIMPLE_MOVE_TO_VALUE = ImmutableMap.<SimpleMove, Integer>builder()
			.put(new SimpleMove(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(A3)), -15)
			.put(new SimpleMove(WHITE_KNIGHT.removeFrom(G1), WHITE_KNIGHT.addTo(H3)), -15)
			.put(new SimpleMove(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(A6)), -15)
			.put(new SimpleMove(BLACK_KNIGHT.removeFrom(G8), BLACK_KNIGHT.addTo(H6)), -15)
			.build();


	public PositionalAnalysisForOpening(
			CentralControlHeuristic centralControlHeuristic,
			PawnHeuristics pawnHeuristics) {
		this.centralControlHeuristic = centralControlHeuristic;
		this.pawnHeuristics = pawnHeuristics;
	}

	public void evaluate(Board board, List<Move> moves) {
		for (Move move : moves) {
			determineOpeningValue(board, move);
		}
	}

	private void determineOpeningValue(Board board, Move move) {
		BoardMutation from = move.getMutationRemovingPieceFromStart();
		BoardMutation to = move.getMutationAddingPieceAtDestination();
		int fromColumn = from.getSquare().column();
		int toColumn = to.getSquare().column();

		int value = negateForBlack(centralControlHeuristic.getCenterControlDeltaForOpening(from, to), move);
		value += pawnHeuristics.getPawnHeuristicsForOpening(board, move, from, to);
		value += negateForBlack(getKnightMoveValue(from, to), move);
		value += negateForBlack(getCastlingValue(from.getPlayerPiece().getPiece(), fromColumn, toColumn), move);
		value += negateForBlack(getPieceMovingFromKingSideValue(fromColumn), move);

		move.setValue(value);
	}

	private int getKnightMoveValue(BoardMutation from, BoardMutation to) {
		int pawnOrKnightMoveValue = 0;
		Integer delta = SIMPLE_MOVE_TO_VALUE.get(new SimpleMove(from, to));
		if (delta != null) {
			pawnOrKnightMoveValue = delta;
		}
		return pawnOrKnightMoveValue;
	}

	private int getPieceMovingFromKingSideValue(int fromColumn) {
		int pieceMovingFromKingSideValue = 0;
		if (fromColumn >= 4) {
			pieceMovingFromKingSideValue = 2;
		}
		return pieceMovingFromKingSideValue;
	}

	private int getCastlingValue(Piece movedPiece, int fromColumn, int toColumn) {
		int castlingValue = 0;
		if (movedPiece == KING && toColumn - fromColumn == 2) {
			castlingValue = 30;
		}

		if (movedPiece == KING && fromColumn - toColumn == 2) {
			castlingValue = 10;
		}
		return castlingValue;
	}

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

}