package nl.gogognome.gogochess.logic.piece;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class KingTest {

	private final Board board = new Board();

	@Test
	void validMovesForKingOnMiddleOfBoard() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertEquals("[Ke4-f5, Ke4-f3, Ke4-d5, Ke4-d3, Ke4-f4, Ke4-d4, Ke4-e5, Ke4-e3]", Moves.formatMoves(moves).toString());
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
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(BLACK_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "Ke4xRe5");
	}

	@Test
	void kingCanCapturePieceOfOtherPlayer() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(WHITE_ROOK, E5, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "Ke4xRe5");
		assertMovesContain(moves,
				BLACK_KING.removeFrom(E4),
				WHITE_ROOK.removeFrom(E5),
				BLACK_KING.addTo(E5));
	}

	@Test
	void kingAndLeftTowerNeverMoved_castlingLongIsAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "O-O-O");
		assertMovesContain(moves,
				BLACK_KING.removeFrom(E8),
				BLACK_ROOK.removeFrom(A8),
				BLACK_KING.addTo(C8),
				BLACK_ROOK.addTo(D8));
	}

	@Test
	void kingMovedBefore_castlingLongIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);
		Move move1 = new Move(setup, BLACK_KING.removeFrom(E8), BLACK_KING.addTo(D8));
		board.process(move1);
		Move move2 = new Move(move1, BLACK_KING.removeFrom(D8), BLACK_KING.addTo(E8));
		board.process(move2);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void leftRookMovedBefore_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);
		Move move1 = new Move(setup, BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(B8));
		board.process(move1);
		Move move2 = new Move(move1, BLACK_ROOK.removeFrom(B8), BLACK_ROOK.addTo(A8));
		board.process(move2);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void square_e8_isAttacked_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, E1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void square_d8_isAttacked_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, D1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void square_c8_isAttacked_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, C1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void square_b8_isAttacked_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, B1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O");
	}

	@Test
	void square_a8_isAttacked_castlingLongIsAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, A8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, A1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "O-O-O");
	}

	@Test
	void kingAndRightTowerNeverMoved_castlingShortIsAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "O-O");
		assertMovesContain(moves,
				BLACK_KING.removeFrom(E8),
				BLACK_ROOK.removeFrom(H8),
				BLACK_KING.addTo(G8),
				BLACK_ROOK.addTo(F8));
	}

	@Test
	void kingMovedBefore_castlingShortIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);
		Move move1 = new Move(setup, BLACK_KING.removeFrom(E8), BLACK_KING.addTo(D8));
		board.process(move1);
		Move move2 = new Move(move1, BLACK_KING.removeFrom(D8), BLACK_KING.addTo(E8));
		board.process(move2);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O-O");
	}

	@Test
	void rightRookMovedBefore_castlingLongIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD));
		board.process(setup);
		Move move1 = new Move(setup, BLACK_ROOK.removeFrom(H8), BLACK_ROOK.addTo(G8));
		board.process(move1);
		Move move2 = new Move(move1, BLACK_ROOK.removeFrom(G8), BLACK_ROOK.addTo(H8));
		board.process(move2);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O-O");
	}

	@Test
	void square_e8_isAttacked_castlingLongIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, E1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O-O");
	}

	@Test
	void square_f8_isAttacked_castlingLongIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, F1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O-O");
	}

	@Test
	void square_g8_isAttacked_castlingLongIsNotAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, G1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesDoNotContain(moves, "O-O-O");
	}

	@Test
	void square_h8_isAttacked_castlingLongIsAllowed() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_ROOK, H8, ADD),
				new BoardMutation(BLACK_KING, E8, ADD),
				new BoardMutation(WHITE_ROOK, H1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "O-O");
	}

	@Test
	void kingAttacksSquareContainingPiece() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(BLACK_ROOK, E5, ADD));
		board.process(setup);

		assertTrue(BLACK_KING.attacks(E4, E5, board));
	}

	@Test
	void kingAttacksSquareContainingOtherPlayersPiece() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD),
				new BoardMutation(WHITE_ROOK, E5, ADD));
		board.process(setup);

		assertTrue(BLACK_KING.attacks(E4, E5, board));
	}

	@Test
	void kingDoesNotAttackUnreachableSquare() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KING, E4, ADD));
		board.process(setup);

		assertFalse(BLACK_KING.attacks(E4, E6, board));
	}

}