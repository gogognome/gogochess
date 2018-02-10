package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Piece.PAWN;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import java.util.concurrent.atomic.*;
import nl.gogognome.gogochess.game.*;
import nl.gogognome.gogochess.game.piece.*;

class PiecePositionEvaluator implements BoardEvaluator {

	@Override
	public int value(Board board) {
		AtomicInteger value = new AtomicInteger();
		board.forEachPlayerPiece(WHITE, (playerPiece, square) -> value.addAndGet(valueOf(playerPiece, square)));
		board.forEachPlayerPiece(BLACK, (playerPiece, square) -> value.addAndGet(valueOf(playerPiece, square)));
		return value.get();
	}

	private int valueOf(PlayerPiece playerPiece, Square square) {
		if (playerPiece.getPiece() == PAWN) {
			if (playerPiece.getPlayer() == WHITE) {
				return square.row();
			} else {
				return square.row();
			}
		}
		return 0;
	}
}
