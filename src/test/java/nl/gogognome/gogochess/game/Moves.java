package nl.gogognome.gogochess.game;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.*;

public class Moves {

	public static void assertMovesContain(List<Move> moves, BoardMutation... expectedMoves) {
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(expectedMoves)),
				actualMoves.toString());
	}


	public static Move find(List<Move> moves, String moveDescription) {
		return moves.stream()
				.filter(m -> m.getDescription().equals(moveDescription))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("could not find move " + moveDescription + " in moves " + moves));
	}
}
