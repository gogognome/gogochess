package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.*;
import nl.gogognome.gogochess.game.*;

public class Bishop extends MultiStepPlayerPiece {

	public Bishop(Player player) {
		super(player, BISHOP,
				new int[] { 1, 1, -1, -1 },
				new int[] { 1, -1, 1, -1 });
	}
}
