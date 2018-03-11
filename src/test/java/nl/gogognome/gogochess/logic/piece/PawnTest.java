package nl.gogognome.gogochess.logic.piece;

import static java.util.Arrays.*;
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

class PawnTest {

	private Board board = new Board();

	@Test
	void validMovesForPawnAtInitialPosition() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertEquals("[a2-a3, a2-a4]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD)),
						asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A4, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void validMovesForPawnThatCannotMoveForward() {
		Move setup = new Move("setup", BLACK,
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
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, A4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertEquals("[a2-a3]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPiece() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B2, ADD),
				new BoardMutation(BLACK_PAWN, A3, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertTrue(moves.toString().contains("b2xa3"), "actual moves: " + moves);
		assertMovesContain(moves,
				new BoardMutation(WHITE_PAWN, B2, REMOVE),
				new BoardMutation(BLACK_PAWN, A3, REMOVE),
				new BoardMutation(WHITE_PAWN, A3, ADD));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPawnEnPassant() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A7, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move("a7-a5", setup,
				new BoardMutation(BLACK_PAWN, A7, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves();

		assertTrue(moves.toString().contains("b5xa6"), "actual moves: " + moves);
		assertMovesContain(moves,
				new BoardMutation(WHITE_PAWN, B5, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, REMOVE),
				new BoardMutation(WHITE_PAWN, A6, ADD));
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecauseOtherPawnDidNotMoveTwoStepsInPreviousMove() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A6, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move("a6-a5", setup,
				new BoardMutation(BLACK_PAWN, A6, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("b5xa6"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecausePawnIsStillAtInitialPosition() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, B2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("x"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBecausePawnIsOneRowFromPromotion() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, A7, ADD),
				new BoardMutation(BLACK_PAWN, B7, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("x"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCanMoveAndPromote() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertTrue(moves.toString().contains("b7-b8(N), b7-b8(B), b7-b8(R), b7-b8(Q)"), "actual moves: " + moves);
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_KNIGHT.addTo(B8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_BISHOP.addTo(B8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_ROOK.addTo(B8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_QUEEN.addTo(B8))), actualMoves.toString());
		assertFalse(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), WHITE_PAWN.addTo(B8))), actualMoves.toString());
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPieceAndPromote() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD),
				new BoardMutation(BLACK_ROOK, A8, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertTrue(moves.toString().contains("b7xRa8(N), b7xRa8(B), b7xRa8(R), b7xRa8(Q)"), "actual moves: " + moves);
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_KNIGHT.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_BISHOP.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_ROOK.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_QUEEN.addTo(A8))), actualMoves.toString());
		assertFalse(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_PAWN.addTo(A8))), actualMoves.toString());
	}

	@Test
	void pawnAttacksForwardLeftAndForwardRightSquares() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD),
				new BoardMutation(BLACK_ROOK, A8, ADD));
		board.process(setup);

		assertTrue(WHITE_PAWN.attacks(B7, A8, board));
		assertTrue(WHITE_PAWN.attacks(B7, C8, board));
	}

	@Test
	void pawnDoesNotAttacksUnreachableSquare() {
		Move setup = new Move("setup", BLACK,
				new BoardMutation(WHITE_PAWN, B7, ADD));
		board.process(setup);

		assertFalse(WHITE_PAWN.attacks(B7, B8, board));
	}
}
