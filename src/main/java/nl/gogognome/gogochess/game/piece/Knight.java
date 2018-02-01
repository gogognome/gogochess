package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.KNIGHT;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class Knight extends PlayerPiece {

	private final static int[] DELTA_X = new int[] { 1, 1, -1, -1, 2, 2, -2, -2 };
	private final static int[] DELTA_Y = new int[] { 2, -2, 2, -2, 1, -1, 1, -1 };

	public Knight(Player player) {
		super(player, KNIGHT);
	}

	public void addPossibleMoves(List<Move> moves, Square from, Board board) {
		for (int i=0; i<DELTA_X.length; i++) {
			Square to = from.addColumnAndRow(DELTA_X[i], DELTA_Y[i]);
			if (to == null) {
				continue;
			}

			PlayerPiece targetPiece = board.pieceAt(to);
			if (targetPiece != null && targetPiece.getPlayer() == getPlayer()) {
				continue;
			}

			if (targetPiece == null) {
				moves.add(new Move(moveNotation(from, to), board.lastMove(),
						removeFrom(from), addTo(to)));
			} else {
				moves.add(new Move(captureNotation(from, to, targetPiece), board.lastMove(),
						removeFrom(from), targetPiece.removeFrom(to), addTo(to)));
			}
		}
	}
}