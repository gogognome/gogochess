package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.ROOK;
import nl.gogognome.gogochess.game.*;

public class Rook extends MultiStepPlayerPiece {

	public Rook(Player player) {
		super(player, ROOK,
				new int[] { 1, -1, 0, 0 },
				new int[] { 0, 0, 1, -1 });
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		if (pieceSquare.column() != attackedSquare.column() && pieceSquare.row() != attackedSquare.row()) {
			return false;
		}
		return super.attacks(pieceSquare, attackedSquare, board);
	}
}
