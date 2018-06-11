package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Move.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class BoardHashTest {

	@Disabled("because this test is slow. Use this test to check for uniqueness of the hashes.")
	@Test
	void hashesAreUnique() {
		Map<Long, String> uniqueHashes = new HashMap<>(500_000);
		LinkedList<Move> remainingMoves = new LinkedList<>();
		remainingMoves.add(INITIAL_BOARD);

		Board board = new Board();
		for (int i=0; i<500_000; i++) {
			Move move = remainingMoves.remove(0);
			board.process(move);
			long hash = board.getBoardHash();
			String newBoard = board.toString();
			String previousBoard = uniqueHashes.put(hash, newBoard);
			if (previousBoard != null) {
				assertThat(previousBoard).isEqualTo(newBoard);
			}
			remainingMoves.addAll(board.validMoves());
		}
	}

	@Test
	void movesWithDifferentPlayerResultingInSameBoardMustHaveDifferentHashes() {
		Board board = new Board();
		board.process(INITIAL_BOARD);
		Move move1_1 = new Move(INITIAL_BOARD, WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		Move move1_2 = new Move(move1_1, BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		board.process(move1_2);
		long hash1 = board.getBoardHash();

		Move move2_1 = new Move(INITIAL_BOARD, WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E3));
		Move move2_2 =new Move(move2_1, BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		Move move2_3 = new Move(move2_2, WHITE_PAWN.removeFrom(E3), WHITE_PAWN.addTo(E4));
		board.process(move2_3);
		long hash2 = board.getBoardHash();

		assertThat(hash1).isNotEqualTo(hash2);
	}
}