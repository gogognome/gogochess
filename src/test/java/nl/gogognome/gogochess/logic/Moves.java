package nl.gogognome.gogochess.logic;

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

}
