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

class KnightTest {

	private Board board = new Board();

	@Test
	void validMovesForKnightAtMiddleOfBoard() {
		Move setup = new Move("setup", null,
				new BoardMutation(BLACK_KNIGHT, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertEquals("[Ne4-f6, Ne4-f2, Ne4-d6, Ne4-d2, Ne4-g5, Ne4-g3, Ne4-c5, Ne4-c3]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, F6, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, F2, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, D6, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, D2, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, G5, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, G3, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, C5, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, C3, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));

	}

	@Test
	void knightCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move("setup", null,
				new BoardMutation(BLACK_KNIGHT, E4, ADD),
				new BoardMutation(BLACK_BISHOP, D2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertFalse(moves.toString().contains("Ne4xBd2"), moves.toString());
	}

	@Test
	void knightCanCapturePieceOfOtherPlayer() {
		Move setup = new Move("setup", null,
				new BoardMutation(BLACK_KNIGHT, E4, ADD),
				new BoardMutation(WHITE_BISHOP, D2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertTrue(moves.toString().contains("Ne4xBd2"), moves.toString());
		assertMovesContain(moves,
				BLACK_KNIGHT.removeFrom(E4),
				WHITE_BISHOP.removeFrom(D2),
				BLACK_KNIGHT.addTo(D2));
	}
}