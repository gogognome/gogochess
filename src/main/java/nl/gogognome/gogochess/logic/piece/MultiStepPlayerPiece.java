package nl.gogognome.gogochess.logic.piece;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

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
			boolean toIsEmptySquare;
			do {
				to = to.addColumnAndRow(deltaX[i], deltaY[i]);
				toIsEmptySquare = addMoveToEmptyFieldOCapture(moves, board, square, to);
			} while (toIsEmptySquare);
		}
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		for (int i = 0; i< deltaX.length; i++) {
			Square to = pieceSquare;
			boolean toIsEmptySquare;
			do {
				to = to.addColumnAndRow(deltaX[i], deltaY[i]);
				if (attackedSquare.equals(to)) {
					return true;
				}
				toIsEmptySquare = to != null && board.empty(to);
			} while (toIsEmptySquare);
		}

		return false;
	}
}
