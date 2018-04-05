package nl.gogognome.gogochess.logic.piece;

import static java.lang.Math.abs;
import static nl.gogognome.gogochess.logic.Piece.KNIGHT;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class Knight extends PlayerPiece {

	private final static int[] DELTA_X = new int[] { 1, 1, -1, -1, 2, 2, -2, -2 };
	private final static int[] DELTA_Y = new int[] { 2, -2, 2, -2, 1, -1, 1, -1 };

	public Knight(Player player) {
		super(player, KNIGHT);
	}

	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		for (int i=0; i<DELTA_X.length; i++) {
			Square to = square.addColumnAndRow(DELTA_X[i], DELTA_Y[i]);
			addMoveToEmptyFieldOrCapture(moves, board, square, to);
		}
	}

	@Override
	protected boolean attacks(Square pieceSquare, Square attackedSquare, Board board, int deltaX, int deltaY) {
		deltaX = abs(deltaX);
		deltaY = abs(deltaY);
		return (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
	}
}