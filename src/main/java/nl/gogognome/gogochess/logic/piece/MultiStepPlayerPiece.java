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
				to = to.addFilesAndRanks(deltaX[i], deltaY[i]);
				toIsEmptySquare = addMoveToEmptyFieldOrCapture(moves, board, square, to);
			} while (toIsEmptySquare);
		}
	}

	boolean allSquaresEmptyBetweenPieceSquareAndAttackedSquare(Square pieceSquare, Square attackedSquare, Board board, int deltaX, int deltaY) {
		int toX = pieceSquare.file();
		int toY = pieceSquare.rank();
		int attackedSquareX = attackedSquare.file();
		int attackedSquareY = attackedSquare.rank();
		boolean toIsEmptySquare;
		do {
			toX += deltaX;
			toY += deltaY;
			if (toX == attackedSquareX && toY == attackedSquareY) {
				return true;
			}
			toIsEmptySquare = board.empty(toX, toY);
		} while (toIsEmptySquare);

		return false;
	}

	/**
	 * Returns the signum of n.
	 * @param n a number
	 * @return 0 if n = 0, -1 if n is negative, 1 if n is positive
	 */
	int signum(int n) {
		return n == 0 ? 0 : n < 0 ? -1 : 1;
	}

}
