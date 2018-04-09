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
		AtomicInteger whiteValue = new AtomicInteger();
		AtomicInteger whitePawnsValue = new AtomicInteger();
		AtomicInteger blackValue = new AtomicInteger();
		AtomicInteger blackPawnsValue = new AtomicInteger();

		board.forEachPlayerPiece(WHITE, (playerPiece, square) -> {
			whiteValue.getAndAdd(PIECE_TO_VALUE.get(playerPiece.getPiece()));
			if (playerPiece.getPiece() == PAWN) {
				whitePawnsValue.getAndAdd(PIECE_TO_VALUE.get(playerPiece.getPiece()));
			}
		});
		board.forEachPlayerPiece(BLACK, (playerPiece, square) -> {
			blackValue.getAndAdd(PIECE_TO_VALUE.get(playerPiece.getPiece()));
			if (playerPiece.getPiece() == PAWN) {
				blackPawnsValue.getAndAdd(PIECE_TO_VALUE.get(playerPiece.getPiece()));
			}
		});

		reduceValueOfPiecesIfAhead(whiteValue, whitePawnsValue, blackValue);
		reduceValueOfPiecesIfAhead(blackValue, blackPawnsValue, whiteValue);

		return whiteValue.get() - blackValue.get();
	}

	private void reduceValueOfPiecesIfAhead(
			AtomicInteger whiteValue, AtomicInteger whitePawnsValue, AtomicInteger blackValue) {
		if (whiteValue.get() - blackValue.get() >= 200) {
			double factor = Math.max(0.6, ((double) blackValue.get()) / whiteValue.get());
			whiteValue.set((int) ((whiteValue.get() - whitePawnsValue.get()) * factor + whitePawnsValue.get()));
		}
	}
}
