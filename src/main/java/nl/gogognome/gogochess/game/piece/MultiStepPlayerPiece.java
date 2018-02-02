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
			boolean toIsEmptyField;
			do {
				to = to.addColumnAndRow(deltaX[i], deltaY[i]);
				toIsEmptyField = addMoveToEmptyFieldOCapture(moves, board, square, to);
			} while (toIsEmptyField);
		}
	}

}
