package nl.gogognome.gogochess.logic.piece;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class RookTest {

	private final Board board = new Board();

	@Test
	void validMovesForRookOnMiddleOfBoard() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, E4, ADD));
		board.process(setup);

		List<Move> moves = board.currentPlayer().validMoves(board);

		assertEquals("[Re4-f4, Re4-g4, Re4-h4, Re4-d4, Re4-c4, Re4-b4, Re4-a4, Re4-e5, Re4-e6, Re4-e7, Re4-e8, Re4-e3, Re4-e2, Re4-e1]", Moves.formatMoves(moves).toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, F4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, G4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, H4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, D4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, C4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, B4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, A4, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E5, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E6, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E7, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E8, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E3, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E2, ADD)),
						asList(new BoardMutation(BLACK_ROOK, E4, REMOVE), new BoardMutation(BLACK_ROOK, E1, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void rookCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, E4, ADD),
				new BoardMutation(BLACK_BISHOP, E5, ADD));
		board.process(setup);

		List<Move> moves = board.currentPlayer().validMoves(board);

		assertFalse(moves.toString().contains("Re4xBe5"), moves.toString());
		assertFalse(moves.toString().contains("Re4-e6"), moves.toString());
	}

	@Test
	void rookCanCapturePieceOfOtherPlayer() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, E4, ADD),
				new BoardMutation(WHITE_BISHOP, E5, ADD));
		board.process(setup);

		List<Move> moves = board.currentPlayer().validMoves(board);

		assertThat(Moves.formatMoves(moves).toString()).contains("Re4xBe5");
		assertMovesContain(moves,
				BLACK_ROOK.removeFrom(E4),
				WHITE_BISHOP.removeFrom(E5),
				BLACK_ROOK.addTo(E5));
		assertThat(Moves.formatMoves(moves).toString()).doesNotContain("Be4-e6");
	}

	@Test
	void rookDoesNotAttacksSquareItCannotReach() {
		Move setup = new Move(WHITE,
				new BoardMutation(WHITE_ROOK, E4, ADD));
		board.process(setup);

		assertFalse(WHITE_ROOK.attacks(E4, F5, board));
		assertFalse(WHITE_ROOK.attacks(E4, F3, board));
		assertFalse(WHITE_ROOK.attacks(E4, D5, board));
		assertFalse(WHITE_ROOK.attacks(E4, D3, board));
	}

	@Test
	void rookAttacksSquareContainingOwnPiece() {
		Move setup = new Move(WHITE,
				new BoardMutation(WHITE_ROOK, E4, ADD),
				new BoardMutation(WHITE_QUEEN, E5, ADD));
		board.process(setup);

		assertTrue(WHITE_ROOK.attacks(E4, E5, board));
		assertFalse(WHITE_ROOK.attacks(E4, E6, board));
	}

	@Test
	void rookAttacksSquareContainingOtherPlayersPiece() {
		Move setup = new Move(WHITE,
				new BoardMutation(WHITE_ROOK, E4, ADD),
				new BoardMutation(BLACK_QUEEN, E5, ADD));
		board.process(setup);

		assertTrue(WHITE_ROOK.attacks(E4, E5, board));
		assertFalse(WHITE_ROOK.attacks(E4, E6, board));
	}

}