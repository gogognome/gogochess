package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Piece.KING;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

public class KingFieldHeuristic {

	private final static int[][] MIDDLE_GAME_KING_FIELD = new int[][]{
			{2, 8, 8, 8, 8, 8, 2},
			{2, 8, 8, 8, 8, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 8, 8, 8, 8, 2},
			{2, 8, 8, 8, 8, 8, 2}
	};

	private final static Map<Piece, Integer> MIDDLE_GAME_PIECE_VALUES = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 3)
			.put(KNIGHT, 4)
			.put(BISHOP, 3)
			.put(ROOK, 2)
			.put(QUEEN, 1)
			.put(KING, 1)
			.build();

	int getKingFieldDeltaForMiddleGame(BoardMutation from, BoardMutation to, Square opponentKingSquare) {
		int centerControlDelta = 0;
		int pieceValue = KingFieldHeuristic.MIDDLE_GAME_PIECE_VALUES.get(from.getPlayerPiece().getPiece());
		centerControlDelta -= valueOf(pieceValue, from.getSquare(), opponentKingSquare);
		centerControlDelta += valueOf(pieceValue, to.getSquare(), opponentKingSquare);
		return centerControlDelta;
	}

	private int valueOf(int pieceFactor, Square square, Square opponentKingSquare) {
		int row = square.row() - opponentKingSquare.row() + 3;
		int column = square.column() - opponentKingSquare.column() + 3;
		int centerControlValue;
		if (0 <= row && row < 7 && 0 <= column && column < 7) {
			centerControlValue = MIDDLE_GAME_KING_FIELD[row][column];
		} else {
			centerControlValue = 0;
		}
		return pieceFactor * centerControlValue;
	}

}