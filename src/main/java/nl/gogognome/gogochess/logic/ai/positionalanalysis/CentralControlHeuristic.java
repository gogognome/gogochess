package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

class CentralControlHeuristic {

	private final static Map<Piece, Integer> OPENING_PIECE_VALUES = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 1)
			.put(KNIGHT, 4)
			.put(BISHOP, 3)
			.put(ROOK, 2)
			.put(QUEEN, 1)
			.put(KING, -1)
			.build();

	private final static Map<Piece, Integer> MIDDLE_GAME_PIECE_VALUES = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 3)
			.put(KNIGHT, 4)
			.put(BISHOP, 3)
			.put(ROOK, 2)
			.put(QUEEN, 1)
			.put(KING, 1)
			.build();

	private final static int[][] CENTER_CONTORL_ARRAY = new int[][]{
			{0, 1, 2, 3, 3, 2, 1, 0},
			{1, 3, 4, 5, 5, 4, 3, 1},
			{2, 4, 6, 7, 7, 6, 4, 2},
			{3, 5, 7, 8, 8, 7, 5, 3},
			{3, 5, 7, 8, 8, 7, 5, 3},
			{2, 4, 6, 7, 7, 6, 4, 2},
			{1, 3, 4, 5, 5, 4, 3, 1},
			{0, 1, 2, 3, 3, 2, 1, 0}
	};

	int getCenterControlDeltaForOpening(BoardMutation from, BoardMutation to) {
		return getCenterControlDelta(from, to, OPENING_PIECE_VALUES);
	}

	int getCenterControlDeltaForMiddleGame(BoardMutation from, BoardMutation to) {
		return getCenterControlDelta(from, to, MIDDLE_GAME_PIECE_VALUES);
	}

	private int getCenterControlDelta(BoardMutation from, BoardMutation to, Map<Piece, Integer> pieceToValue) {
		int centerControlDelta = 0;
		centerControlDelta -= valueOf(from.getPlayerPiece().getPiece(), from.getSquare(), pieceToValue);
		centerControlDelta += valueOf(to.getPlayerPiece().getPiece(), to.getSquare(), pieceToValue);
		return centerControlDelta;
	}

	private int valueOf(Piece piece, Square square, Map<Piece, Integer> pieceToValue) {
		int pieceFactor = pieceToValue.get(piece);
		int centerControlValue = CentralControlHeuristic.CENTER_CONTORL_ARRAY[square.row()][square.column()];
		return pieceFactor * centerControlValue;
	}

}
