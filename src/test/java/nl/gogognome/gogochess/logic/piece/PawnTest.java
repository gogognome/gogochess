package nl.gogognome.gogochess.logic.piece;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class PawnTest {

	private Board board = new Board();

	@Test
	void validMovesForPawnAtInitialPosition() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContainsExactlyInAnyOrder(moves, "a2-a3", "a2-a4");
		assertThat(moves.stream().map(Move::getPrecedingMove).collect(toSet())).containsExactly(setup);
		assertThat(moves.stream().map(Move::getBoardMutations).collect(toList())).containsExactlyInAnyOrder(
				asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD)),
				asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A4, ADD)));
	}

	@Test
	void validMovesForPawnThatCannotMoveForward() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, A3, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertEquals("[]", moves.toString());
		assertEquals(emptySet(), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				emptyList(),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void validMovesForPawnAtInitialPositionThatCannotMoveForwardOneStepButNotTwo() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, A4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContainsExactlyInAnyOrder(moves, "a2-a3");
		assertThat(moves.stream().map(Move::getPrecedingMove).collect(toSet())).containsExactly(setup);
		assertThat(moves.stream().map(Move::getBoardMutations).collect(toList())).containsExactlyInAnyOrder(
				asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD)));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPiece() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B2, ADD),
				new BoardMutation(BLACK_PAWN, A3, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "b2xa3");
		assertMovesContain(moves,
				new BoardMutation(WHITE_PAWN, B2, REMOVE),
				new BoardMutation(BLACK_PAWN, A3, REMOVE),
				new BoardMutation(WHITE_PAWN, A3, ADD));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPawnEnPassant() {
		Move setup = new Move(WHITE,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A7, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move(setup,
				new BoardMutation(BLACK_PAWN, A7, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "b5xa6");
		assertMovesContain(moves,
				new BoardMutation(WHITE_PAWN, B5, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, REMOVE),
				new BoardMutation(WHITE_PAWN, A6, ADD));
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecauseOtherPawnDidNotMoveTwoStepsInPreviousMove() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A6, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move(setup,
				new BoardMutation(BLACK_PAWN, A6, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("b5xa6"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecausePawnIsStillAtInitialPosition() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, B2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("x"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecausePawnIsOneRowFromPromotion() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, A7, ADD),
				new BoardMutation(BLACK_PAWN, B7, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("x"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCanMoveAndPromote() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "b7-b8(N)");
		assertMovesContain(moves, "b7-b8(B)");
		assertMovesContain(moves, "b7-b8(R)");
		assertMovesContain(moves, "b7-b8(Q)");
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_KNIGHT.addTo(B8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_BISHOP.addTo(B8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_ROOK.addTo(B8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_QUEEN.addTo(B8)));
		assertThat(actualMoves).doesNotContain(asList(WHITE_PAWN.removeFrom(B7), WHITE_PAWN.addTo(B8)));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPieceAndPromote() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD),
				new BoardMutation(BLACK_ROOK, A8, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "b7xRa8(N)");
		assertMovesContain(moves, "b7xRa8(B)");
		assertMovesContain(moves, "b7xRa8(R)");
		assertMovesContain(moves, "b7xRa8(Q)");
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_KNIGHT.addTo(A8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_BISHOP.addTo(A8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_ROOK.addTo(A8)));
		assertThat(actualMoves).contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_QUEEN.addTo(A8)));
		assertThat(actualMoves).doesNotContain(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_PAWN.addTo(A8)));
	}

	@Test
	void pawnAttacksForwardLeftAndForwardRightSquares() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD),
				new BoardMutation(BLACK_ROOK, A8, ADD));
		board.process(setup);

		assertTrue(WHITE_PAWN.attacks(B7, A8, board));
		assertTrue(WHITE_PAWN.attacks(B7, C8, board));
	}

	@Test
	void pawnDoesNotAttacksUnreachableSquare() {
		Move setup = new Move(BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD));
		board.process(setup);

		assertFalse(WHITE_PAWN.attacks(B7, B8, board));
	}
}
