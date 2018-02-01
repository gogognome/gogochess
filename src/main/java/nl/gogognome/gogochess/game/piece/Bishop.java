package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.BISHOP;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class Bishop extends PlayerPiece {

	private final static int[] DELTA_X = new int[] { 1, 1, -1, -1 };
	private final static int[] DELTA_Y = new int[] { 1, -1, 1, -1 };

	public Bishop(Player player) {
		super(player, BISHOP);
	}

	@Override
	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		for (int i=0; i<DELTA_X.length; i++) {
			Square to = square;
			while (true) {
				to = to.addColumnAndRow(DELTA_X[i], DELTA_Y[i]);
				if (to == null) {
					break;
				}
				PlayerPiece capturedPiece = board.pieceAt(to);
				if (capturedPiece == null) {
					moves.add(new Move(moveNotation(square, to), board.lastMove(),
							removeFrom(square), addTo(to)));
				} else {
					if (capturedPiece.getPlayer() != getPlayer()) {
						moves.add(new Move(captureNotation(square, to, capturedPiece), board.lastMove(),
								removeFrom(square), capturedPiece.removeFrom(to), addTo(to)));
					}
					break;
				}
			}
		}
	}
}
