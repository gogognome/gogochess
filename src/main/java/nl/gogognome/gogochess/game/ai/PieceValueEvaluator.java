package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import java.util.concurrent.atomic.*;
import nl.gogognome.gogochess.game.*;

public class PieceValueEvaluator implements BoardEvaluator {

	private int factor;

	public PieceValueEvaluator(int factor) {
		this.factor = factor;
	}

	@Override
	public int value(Board board) {
		AtomicInteger value = new AtomicInteger();
		board.forEachPlayerPiece(WHITE, (playerPiece, square) -> value.getAndAdd(playerPiece.getPiece().value()));
		board.forEachPlayerPiece(BLACK, (playerPiece, square) -> value.getAndAdd(-playerPiece.getPiece().value()));
		return value.get() * factor;
	}
}
