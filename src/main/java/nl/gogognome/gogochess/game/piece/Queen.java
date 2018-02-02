package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.*;
import nl.gogognome.gogochess.game.*;

public class Queen extends MultiStepPlayerPiece {

	public Queen(Player player) {
		super(player, QUEEN,
				new int[] { 1, 1, -1, -1, 1, -1, 0, 0 },
				new int[] { 1, -1, 1, -1, 0, 0, 1, -1 });
	}
}
