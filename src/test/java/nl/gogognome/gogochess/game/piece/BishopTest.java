package nl.gogognome.gogochess.game.piece;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.Moves.assertMovesContain;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.Squares.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.*;

class BishopTest {

	private final Board board = new Board();

	@Test
	void validMovesForBishopOnMiddleOfBoard() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_BISHOP, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertEquals("[Be4-f5, Be4-g6, Be4-h7, Be4-f3, Be4-g2, Be4-h1, Be4-d5, Be4-c6, Be4-b7, Be4-a8, Be4-d3, Be4-c2, Be4-b1]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, F5, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, G6, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, H7, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, F3, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, G2, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, H1, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, D5, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, C6, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, B7, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, A8, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, D3, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, C2, ADD)),
						asList(new BoardMutation(WHITE_BISHOP, E4, REMOVE), new BoardMutation(WHITE_BISHOP, B1, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void bishopCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_BISHOP, E4, ADD),
				new BoardMutation(WHITE_QUEEN, F5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertFalse(moves.toString().contains("Be4xQf5"), moves.toString());
		assertFalse(moves.toString().contains("Be4-g6"), moves.toString());
	}

	@Test
	void bishopCanCapturePieceOfOtherPlayer() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_BISHOP, E4, ADD),
				new BoardMutation(BLACK_QUEEN, F5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertTrue(moves.toString().contains("Be4xQf5"), moves.toString());
		assertMovesContain(moves,
				WHITE_BISHOP.removeFrom(E4),
				BLACK_QUEEN.removeFrom(F5),
				WHITE_BISHOP.addTo(F5));
		assertFalse(moves.toString().contains("Be4-g6"), moves.toString());
	}
}