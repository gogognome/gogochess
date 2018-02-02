package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.KING;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class King extends PlayerPiece {

	private final int[] deltaX = new int[] { 1, 1, -1, -1, 1, -1, 0, 0 };
	private final int[] deltaY = new int[] { 1, -1, 1, -1, 0, 0, 1, -1 };

	public King(Player player) {
		super(player, KING);
	}

	@Override
	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		for (int i=0; i<deltaX.length; i++) {
			Square to = square.addColumnAndRow(deltaX[i], deltaY[i]);
			addMoveToEmptyFieldOCapture(moves, board, square, to);
		}
	}
}
