package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class Moves {

	public static void assertMovesContain(List<Move> moves, BoardMutation... expectedMoves) {
		List<List<BoardMutation>> actualMoves = moves.stream()
				.map(Move::getBoardMutations)
				.collect(toList());
		assertThat(actualMoves).contains(asList(expectedMoves));
	}

	public static void assertMovesContain(List<Move> moves, String description) {
		MoveNotation moveNotation = new ReverseAlgebraicNotation();
		List<String> descriptions = moves.stream().map(moveNotation::format).collect(toList());
		assertThat(descriptions).contains(description);
	}

	public static void assertMovesDoNotContain(List<Move> moves, String description) {
		MoveNotation moveNotation = new ReverseAlgebraicNotation();
		List<String> descriptions = moves.stream().map(moveNotation::format).collect(toList());
		assertThat(descriptions).doesNotContain(description);
	}

	public static List<String> formatMoves(List<Move> moves) {
		MoveNotation moveNotation = new ReverseAlgebraicNotation();
		return moves.stream()
				.map(moveNotation::format)
				.collect(toList());
	}

	public static void assertMovesContainsExactlyInAnyOrder(List<Move> moves, String... expectedValues) {
		assertThat(Moves.formatMoves(moves)).containsExactlyInAnyOrder(expectedValues);
	}

}
