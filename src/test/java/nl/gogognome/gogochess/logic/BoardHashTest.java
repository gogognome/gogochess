package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

class BoardHashTest {

	@Disabled("because this test is slow. Use this test to check for uniqueness of the hashes.")
	@Test
	void hashesAreUnique() {
		Map<Long, String> uniqueHashes = new HashMap<>(500_000);
		LinkedList<Move> remainingMoves = new LinkedList<>();
		remainingMoves.add(Board.INITIAL_BOARD);

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
			remainingMoves.addAll(board.currentPlayer().validMoves(board));
		}
	}

	@Test
	void movesWithDifferentPlayerResultingInSameBoardMustHaveDifferentHashes() {
		Board board = new Board();
		board.initBoard();
		board.process(WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		board.process(BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		long hash1 = board.getBoardHash();

		board.initBoard();
		board.process(WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E3));
		board.process(BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		board.process(WHITE_PAWN.removeFrom(E3), WHITE_PAWN.addTo(E4));
		long hash2 = board.getBoardHash();

		assertThat(hash1).isNotEqualTo(hash2);
	}
}