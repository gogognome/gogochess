package nl.gogognome.gogochess.game.piece;

import java.util.*;
import nl.gogognome.gogochess.game.*;

public abstract class MultiStepPlayerPiece extends PlayerPiece {

	private final int[] deltaX;
	private final int[] deltaY;

	MultiStepPlayerPiece(Player player, Piece piece, int[] delta_x, int[] delta_y) {
		super(player, piece);
		deltaX = delta_x;
		deltaY = delta_y;
	}

	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		for (int i = 0; i< deltaX.length; i++) {
			Square to = square;
			while (true) {
				to = to.addColumnAndRow(deltaX[i], deltaY[i]);
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
