package nl.gogognome.gogochess.logic.piece;

import static java.lang.Math.abs;
import static nl.gogognome.gogochess.logic.Piece.*;
import nl.gogognome.gogochess.logic.*;

public class Queen extends MultiStepPlayerPiece {

	public Queen(Player player) {
		super(player, QUEEN,
				new int[] { 1, 1, -1, -1, 1, -1, 0, 0 },
				new int[] { 1, -1, 1, -1, 0, 0, 1, -1 });
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		int deltaX = attackedSquare.column() - pieceSquare.column();
		int deltaY = attackedSquare.row() - pieceSquare.row();

		if (abs(deltaX) != abs(deltaY) && (deltaX != 0 && deltaY != 0)) {
			return false;
		}
		return super.attacks(pieceSquare, attackedSquare, board, signum(deltaX), signum(deltaY));
	}

}
