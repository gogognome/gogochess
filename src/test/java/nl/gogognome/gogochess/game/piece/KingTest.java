package nl.gogognome.gogochess.game.piece;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.Moves.*;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.Squares.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.*;

class KingTest {

	private final Board board = new Board();

	@Test
	void validMovesForKingOnMiddleOfBoard() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertEquals("[Ke4-f5, Ke4-f3, Ke4-d5, Ke4-d3, Ke4-f4, Ke4-d4, Ke4-e5, Ke4-e3]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, F5, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, F3, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, D5, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, D3, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, F4, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, D4, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, E5, ADD)),
						asList(new BoardMutation(BLACK_KING, E4, REMOVE), new BoardMutation(BLACK_KING, E3, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void kingCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(BLACK_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertFalse(moves.toString().contains("Ke4xRe5"), moves.toString());
	}

	@Test
	void kingCanCapturePieceOfOtherPlayer() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(WHITE_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(BLACK);

		assertTrue(moves.toString().contains("Ke4xRe5"), moves.toString());
		assertMovesContain(moves,
				BLACK_KING.removeFrom(E4),
				WHITE_ROOK.removeFrom(E5),
				BLACK_KING.addTo(E5));
	}


	@Test
	void kingAttacksSquareContainingOPiece() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(BLACK_ROOK, E5, ADD));
		board.process(setup);

		assertTrue(BLACK_KING.attacks(E4, E5, board));
	}

	@Test
	void kingAttacksSquareContainingOtherPlayersPiece() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(WHITE_ROOK, E5, ADD));
		board.process(setup);

		assertTrue(BLACK_KING.attacks(E4, E5, board));
	}

	@Test
	void kingDoesNotAttackUnreachableSquare() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD));
		board.process(setup);

		assertFalse(BLACK_KING.attacks(E4, E6, board));
	}


}