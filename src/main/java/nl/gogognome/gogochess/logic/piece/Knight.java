package nl.gogognome.gogochess.logic.piece;

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
			addMoveToEmptyFieldOCapture(moves, board, square, to);
		}
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		for (int i=0; i<DELTA_X.length; i++) {
			Square to = pieceSquare.addColumnAndRow(DELTA_X[i], DELTA_Y[i]);
			if (attackedSquare.equals(to)) {
				return true;
			}
		}
		return false;
	}
}