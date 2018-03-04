package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Player.*;
import java.util.concurrent.atomic.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PiecePositionEvaluator implements BoardEvaluator {

	private final static int[] PREFER_CENTER_VALUES = new int[] { 1, 2, 3, 4, 4, 3, 2, 1};
	private final static int[] AVOID_CENTER_VALUES = new int[] { 4, 3, 2, 1, 1, 2, 3, 4 };

	private final int factor;

	PiecePositionEvaluator(int factor) {
		this.factor = factor;
	}

	@Override
	public int value(Board board) {
		AtomicInteger value = new AtomicInteger();
		board.forEachPlayerPiece(WHITE, (playerPiece, square) -> value.addAndGet(valueOf(playerPiece, square)));
		board.forEachPlayerPiece(BLACK, (playerPiece, square) -> value.addAndGet(valueOf(playerPiece, square)));
		return factor * value.get();
	}

	private int valueOf(PlayerPiece playerPiece, Square square) {
		switch (playerPiece.getPiece()) {
			case PAWN:
				if (playerPiece.getPlayer() == WHITE) {
					return square.row() * PREFER_CENTER_VALUES[square.column()];
				} else {
					return (square.row() - 7) * PREFER_CENTER_VALUES[square.column()];
				}
			case KNIGHT:
				return 5 * MoveValues.negateForBlack(PREFER_CENTER_VALUES[square.column()] * PREFER_CENTER_VALUES[square.row()], playerPiece.getPlayer());
			case BISHOP:
				return 5 * MoveValues.negateForBlack(PREFER_CENTER_VALUES[square.column()] * PREFER_CENTER_VALUES[square.row()], playerPiece.getPlayer());
			case ROOK:
				return 5 * (MoveValues.negateForBlack(PREFER_CENTER_VALUES[square.column()] * PREFER_CENTER_VALUES[square.row()], playerPiece.getPlayer()));
			case QUEEN:
				return 5 * (MoveValues.negateForBlack(PREFER_CENTER_VALUES[square.column()] * PREFER_CENTER_VALUES[square.row()], playerPiece.getPlayer()));
			case KING:
				return 5 * (MoveValues.negateForBlack(AVOID_CENTER_VALUES[square.column()] * AVOID_CENTER_VALUES[square.row()], playerPiece.getPlayer()));
			default:
				return 0;
		}
	}
}
