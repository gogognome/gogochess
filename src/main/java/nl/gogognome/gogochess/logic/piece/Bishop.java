package nl.gogognome.gogochess.logic.piece;

import static java.lang.Math.abs;
import static nl.gogognome.gogochess.logic.Piece.*;
import nl.gogognome.gogochess.logic.*;

public class Bishop extends MultiStepPlayerPiece {

	public Bishop(Player player) {
		super(player, BISHOP,
				new int[] { 1, 1, -1, -1 },
				new int[] { 1, -1, 1, -1 });
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board, int deltaX, int deltaY) {
		if (abs(deltaX) != abs(deltaY)) {
			return false;
		}
		return allSquaresEmptyBetweenPieceSquareAndAttackedSquare(pieceSquare, attackedSquare, board, signum(deltaX), signum(deltaY));
	}

}
