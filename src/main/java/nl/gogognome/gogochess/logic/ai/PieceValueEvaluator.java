package nl.gogognome.gogochess.logic.ai;

import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

public class PieceValueEvaluator implements BoardEvaluator {

	private Map<Piece, Integer> whitePieceToValue = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 100)
			.put(KNIGHT, 330)
			.put(BISHOP, 330)
			.put(ROOK, 500)
			.put(QUEEN, 900)
			.put(KING, 15000)
			.build();

	private Map<Piece, Integer> blackPieceToValue = whitePieceToValue;

	@Override
	public MoveValue value(Board board) {
		return new MoveValue(getValueForPieces(board, WHITE), getValueForPieces(board, BLACK));
	}

	public int getValueForPieces(Board board, Player player) {
		AtomicInteger whiteValue = new AtomicInteger();
		Map<Piece, Integer> playerPieceToValue = player == WHITE ? whitePieceToValue : blackPieceToValue;
		board.forEachPlayerPiece(player, (playerPiece, square) -> whiteValue.getAndAdd(playerPieceToValue.get(playerPiece.getPiece())));
		return whiteValue.get();
	}

	public void readjustWhitePieceValues(float factor) {
		whitePieceToValue = readjustBlackPieceValues(whitePieceToValue, factor);
	}

	public void readjustBlackPieceValues(float factor) {
		blackPieceToValue = readjustBlackPieceValues(blackPieceToValue, factor);
	}

	public void setWhitePawnValue(int newValue) {
		whitePieceToValue = new HashMap<>(blackPieceToValue);
		whitePieceToValue.put(PAWN, newValue);
	}

	public void setBlackPawnValue(int newValue) {
		blackPieceToValue = new HashMap<>(blackPieceToValue);
		blackPieceToValue.put(PAWN, newValue);
	}

	private Map<Piece, Integer> readjustBlackPieceValues(Map<Piece, Integer> pieceToValue, float factor) {
		return pieceToValue.keySet().stream()
				.collect(toMap(piece -> piece, piece -> applyFactor(piece, pieceToValue.get(piece), factor)));
	}

	private int applyFactor(Piece piece, int oldValue, float factor) {
		if (piece == PAWN) {
			return oldValue;
		}
		return (int)(oldValue * factor + 0.5f);
	}

	@Override
	public String toString() {
		return "White piece values: " + whitePieceToValue + "; black piece values: " + blackPieceToValue;
	}
}
