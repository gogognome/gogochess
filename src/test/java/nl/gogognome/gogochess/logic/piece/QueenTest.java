package nl.gogognome.gogochess.logic.piece;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class QueenTest {

	private final Board board = new Board();

	@Test
	void validMovesForQueenOnMiddleOfBoard() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_QUEEN, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertEquals("[Qe4-f5, Qe4-g6, Qe4-h7, Qe4-f3, Qe4-g2, Qe4-h1, Qe4-d5, Qe4-c6, Qe4-b7, Qe4-a8, Qe4-d3, Qe4-c2, Qe4-b1, " +
				"Qe4-f4, Qe4-g4, Qe4-h4, Qe4-d4, Qe4-c4, Qe4-b4, Qe4-a4, Qe4-e5, Qe4-e6, Qe4-e7, Qe4-e8, Qe4-e3, Qe4-e2, Qe4-e1]",
				moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertMovesContain(moves,
				WHITE_QUEEN.removeFrom(E4),
				WHITE_QUEEN.addTo(A4));
		assertMovesContain(moves,
				WHITE_QUEEN.removeFrom(E4),
				WHITE_QUEEN.addTo(H7));
	}

	@Test
	void queenCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_QUEEN, E4, ADD),
				new BoardMutation(WHITE_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertFalse(moves.toString().contains("Qe4xRe5"), moves.toString());
		assertFalse(moves.toString().contains("Qe4-e6"), moves.toString());
	}

	@Test
	void queenCanCapturePieceOfOtherPlayer() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_QUEEN, E4, ADD),
				new BoardMutation(BLACK_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertTrue(moves.toString().contains("Qe4xRe5"), moves.toString());
		assertMovesContain(moves,
				WHITE_QUEEN.removeFrom(E4),
				BLACK_ROOK.removeFrom(E5),
				WHITE_QUEEN.addTo(E5));
		assertFalse(moves.toString().contains("Qe4-e6"), moves.toString());
	}

}
