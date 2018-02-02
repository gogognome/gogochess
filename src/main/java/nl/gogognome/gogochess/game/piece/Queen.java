package nl.gogognome.gogochess.game.piece;

import static java.lang.Math.abs;
import static nl.gogognome.gogochess.game.Piece.*;
import nl.gogognome.gogochess.game.*;

public class Queen extends MultiStepPlayerPiece {

	public Queen(Player player) {
		super(player, QUEEN,
				new int[] { 1, 1, -1, -1, 1, -1, 0, 0 },
				new int[] { 1, -1, 1, -1, 0, 0, 1, -1 });
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		if (abs(pieceSquare.column() - attackedSquare.column()) != abs(pieceSquare.row() - attackedSquare.row())
				&& pieceSquare.column() != attackedSquare.column() && pieceSquare.row() != attackedSquare.row()) {
			return false;
		}
		return super.attacks(pieceSquare, attackedSquare, board);
	}

}
