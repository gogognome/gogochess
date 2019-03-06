package nl.gogognome.gogochess.logic.movenotation;

import static java.util.stream.Collectors.*;
import java.util.*;
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

	/**
	 * Formats a collection of moves.
	 * @param moves the moves
	 * @return a list of string representations of the moves.
	 */
	default List<String> format(Collection<Move> moves) {
		return moves.stream()
				.map(this::format)
				.collect(toList());
	}
}
