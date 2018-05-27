package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Piece.KING;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

public class KingField {

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
		return getKingFieldDelta(from, to, MIDDLE_GAME_PIECE_VALUES, opponentKingSquare);
	}

	private int getKingFieldDelta(BoardMutation from, BoardMutation to, Map<Piece, Integer> pieceToValue, Square opponentKingSquare) {
		int centerControlDelta = 0;
		centerControlDelta -= valueOf(from.getPlayerPiece().getPiece(), from.getSquare(), pieceToValue, opponentKingSquare);
		centerControlDelta += valueOf(to.getPlayerPiece().getPiece(), to.getSquare(), pieceToValue, opponentKingSquare);
		return centerControlDelta;
	}

	private int valueOf(Piece piece, Square square, Map<Piece, Integer> pieceToValue, Square opponentKingSquare) {
		int pieceFactor = pieceToValue.get(piece);
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