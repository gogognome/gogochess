package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;

public class PieceValueEvaluator implements BoardEvaluator {

	private final static Map<Piece, Integer> PIECE_TO_VALUE = ImmutableMap.<Piece, Integer>builder()
			.put(PAWN, 100)
			.put(KNIGHT, 330)
			.put(BISHOP, 330)
			.put(ROOK, 500)
			.put(QUEEN, 900)
			.put(KING, 15000)
			.build();

	@Override
	public int value(Board board) {
		return getValueForPieces(board, WHITE) - getValueForPieces(board, BLACK);
	}

	public int getValueForPieces(Board board, Player white) {
		AtomicInteger whiteValue = new AtomicInteger();
		board.forEachPlayerPiece(white, (playerPiece, square) -> {
			whiteValue.getAndAdd(PIECE_TO_VALUE.get(playerPiece.getPiece()));
		});
		return whiteValue.get();
	}

}
