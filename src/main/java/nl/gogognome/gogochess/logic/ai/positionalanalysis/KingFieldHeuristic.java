package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Piece.KING;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

class KingFieldHeuristic {

	private final static int[][] MIDDLE_GAME_KING_FIELD = new int[][]{
			{2, 2, 2, 2, 2, 2, 2},
			{2, 8, 8, 8, 8, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 10, 10, 10, 8, 2},
			{2, 8, 8, 8, 8, 8, 2},
			{2, 2, 2, 2, 2, 2, 2}
	};

	private final static Map<Piece, Integer> MIDDLE_GAME_PIECE_VALUES = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 3)
			.put(KNIGHT, 4)
			.put(BISHOP, 3)
			.put(ROOK, 2)
			.put(QUEEN, 1)
			.put(KING, 1)
			.build();

	private final static int[][] ENDGAME_WITH_PAWNS_KING_FIELD = new int[][]{
			{1, 1, 2, 3, 2, 1, 1},
			{1, 3, 4, 5, 4, 3, 1},
			{2, 4, 6, 6, 6, 4, 2},
			{3, 5, 6, 6, 6, 5, 3},
			{2, 4, 6, 6, 6, 4, 2},
			{1, 3, 4, 5, 4, 3, 1},
			{1, 1, 2, 3, 2, 1, 1}
	};

	private final static int[][] GENERAL_ENDGAME_KING_FIELD = new int[][]{
			{4, 4, 5, 6, 5, 4, 4},
			{4, 8, 10, 10, 10, 8, 4},
			{5, 10, 10, 10, 10, 10, 5},
			{6, 10, 10, 10, 10, 10, 6},
			{5, 10, 10, 10, 10, 10, 5},
			{4, 8, 10, 10, 10, 8, 4},
			{4, 4, 5, 6, 5, 4, 4}
	};

	private final static int[][] ENDGAME_WITH_PIECES_KING_FIELD = new int[][]{
			{4, 4, 5, 6, 5, 4, 4},
			{4, 8, 10, 9, 10, 8, 4},
			{5, 10, 10, 10, 10, 10, 5},
			{6, 9, 10, 10, 10, 9, 6},
			{5, 10, 10, 10, 10, 10, 5},
			{4, 8, 10, 9, 10, 8, 4},
			{4, 4, 5, 6, 5, 4, 4}
	};

	int getKingFieldDeltaForMiddleGame(BoardMutation from, BoardMutation to, Square opponentKingSquare) {
		int pieceValue = KingFieldHeuristic.MIDDLE_GAME_PIECE_VALUES.get(from.getPlayerPiece().getPiece());
		return valueOf(pieceValue, to.getSquare(), opponentKingSquare, MIDDLE_GAME_KING_FIELD) - valueOf(pieceValue, from.getSquare(), opponentKingSquare, MIDDLE_GAME_KING_FIELD);
	}

	int getKingFieldDeltaForEndgameWithPawns(BoardMutation from, BoardMutation to, Square opponentKingSquare) {
		return getKingFieldDeltaForEndgame(from, to, opponentKingSquare, ENDGAME_WITH_PAWNS_KING_FIELD);
	}

	int getKingFieldDeltaForGeneralEndgame(BoardMutation from, BoardMutation to, Square opponentKingSquare) {
		return getKingFieldDeltaForEndgame(from, to, opponentKingSquare, GENERAL_ENDGAME_KING_FIELD);
	}

	private int getKingFieldDeltaForEndgame(BoardMutation from, BoardMutation to, Square opponentKingSquare, int[][] kingField) {
		if (!from.getPlayerPiece().getPiece().equals(KING)) {
			return 0;
		}
		return valueOf(1, to.getSquare(), opponentKingSquare, kingField) - valueOf(1, from.getSquare(), opponentKingSquare, kingField);
	}

	int getOpponentKingFieldValueForEndgameWithPieces(Square ownKingSquare, Square opponentKingSquare) {
		return valueOf(2, ownKingSquare, opponentKingSquare, ENDGAME_WITH_PIECES_KING_FIELD);
	}

	int getCenterControlValueForPiecesAt(Square ownKingsSquare, List<Square> ownPiecesSquares) {
		if (ownPiecesSquares.isEmpty()) {
			return 0;
		}
		return ownPiecesSquares.stream()
				.mapToInt(square -> valueOf(1, square, ownKingsSquare, ENDGAME_WITH_PIECES_KING_FIELD))
				.sum() / ownPiecesSquares.size();
	}

	private int valueOf(int pieceFactor, Square square, Square kingsSquare, int[][] kingField) {
		int fieldColumn = square.rank() - kingsSquare.rank() + 3;
		int fieldRow = square.file() - kingsSquare.file() + 3;
		int kingFieldValue;
		if (0 <= fieldColumn && fieldColumn < 7 && 0 <= fieldRow && fieldRow < 7) {
			kingFieldValue = kingField[fieldRow][fieldColumn];
		} else {
			kingFieldValue = 0;
		}
		return pieceFactor * kingFieldValue;
	}
}