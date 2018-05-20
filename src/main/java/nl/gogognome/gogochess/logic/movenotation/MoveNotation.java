package nl.gogognome.gogochess.logic.movenotation;

import nl.gogognome.gogochess.logic.*;

public interface MoveNotation {
	/**
	 * Formats a single move.
	 * @param move the move
	 * @return the formatted move
	 */
	String format(Move move);

	/**
	 * Formats a sequence of moves.
	 * @param from the first move
	 * @param to the last move (including). This move must be a (grand)child of <code>from</code>
	 * @return a single string with the formatted moves
	 */
	String format(Move from, Move to);
}
