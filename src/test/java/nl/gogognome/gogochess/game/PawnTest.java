package nl.gogognome.gogochess.game;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.Board.WHITE_PAWN;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.REMOVE;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static nl.gogognome.gogochess.game.Squares.*;
import static nl.gogognome.gogochess.game.Squares.A8;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

class PawnTest {

	private Board board = new Board();

	@Test
	void validMovesForPawnAtInitialPosition() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, A2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

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
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, A3, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertEquals("[]", moves.toString());
		assertEquals(emptySet(), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				emptyList(),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void validMovesForPawnAtInitialPositionThatCannotMoveForwardOneStepButNotTwo() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, A2, ADD),
				new BoardMutation(BLACK_PAWN, A4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertEquals("[a2-a3]", moves.toString());
		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(
						asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPiece() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, B2, ADD),
				new BoardMutation(BLACK_PAWN, A3, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertTrue(moves.toString().contains("b2xa3"), "actual moves: " + moves);
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(
				new BoardMutation(WHITE_PAWN, B2, REMOVE),
				new BoardMutation(BLACK_PAWN, A3, REMOVE),
				new BoardMutation(WHITE_PAWN, A3, ADD))),
				actualMoves.toString());
	}

	@Test
	void validMovesForPawnThatCanCaptureAnotherPawnEnPassant() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A7, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move("a7-a5", setup,
				new BoardMutation(BLACK_PAWN, A7, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves(WHITE);

		assertTrue(moves.toString().contains("b5xa6"), "actual moves: " + moves);
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(
				new BoardMutation(WHITE_PAWN, B5, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, REMOVE),
				new BoardMutation(WHITE_PAWN, A6, ADD))),
				actualMoves.toString());
	}

	@Test
	void validMovesForPawnThatCannotCaptureAnotherPawnEnPassantBeacuseOtherPawnDidNotMoveTwoStepsInPreviousMove() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, B5, ADD),
				new BoardMutation(BLACK_PAWN, A6, ADD));
		board.process(setup);
		Move blackPawnMoves2 = new Move("a6-a5", setup,
				new BoardMutation(BLACK_PAWN, A6, REMOVE),
				new BoardMutation(BLACK_PAWN, A5, ADD));
		board.process(blackPawnMoves2);

		List<Move> moves = board.validMoves(WHITE);

		assertFalse(moves.toString().contains("b5xa6"), "actual moves: " + moves);
	}

	@Test
	void validMovesForPawnThatCanMoveAndPromote() {
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, B7, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

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
		Move setup = new Move("setup", null,
				new BoardMutation(WHITE_PAWN, B7, ADD),
				new BoardMutation(BLACK_ROOK, A8, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertTrue(moves.toString().contains("b7xRa8(N), b7xRa8(B), b7xRa8(R), b7xRa8(Q)"), "actual moves: " + moves);
		List<List<BoardMutation>> actualMoves = moves.stream().map(Move::getBoardMutations).collect(toList());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_KNIGHT.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_BISHOP.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_ROOK.addTo(A8))), actualMoves.toString());
		assertTrue(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_QUEEN.addTo(A8))), actualMoves.toString());
		assertFalse(actualMoves.contains(asList(WHITE_PAWN.removeFrom(B7), BLACK_ROOK.removeFrom(A8), WHITE_PAWN.addTo(A8))), actualMoves.toString());
	}

}
