package nl.gogognome.gogochess.game;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.piece.PlayerPiece.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

class BoardTest {

	private static final Square A2 = new Square("A2");
	private static final Square A3 = new Square("A3");
	private static final Square A4 = new Square("A4");
	private static final Square B1 = new Square("B1");
	private static final Square B2 = new Square("B2");

	private Board board = new Board();

	@Test
	void processAddMutationToEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, new Square("A2"), ADD));

		assertEquals(WHITE_PAWN, board.pieceAt(new Square("A2")));
	}

	@Test
	void processAddMutationToNonEmptySquareFails() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, ADD)));

		assertEquals("The square A2 is not empty. It contains white pawn.", exception.getMessage());
	}

	@Test
	void processRemoveMutationToNonEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		board.process(new BoardMutation(WHITE_PAWN, new Square("A2"), REMOVE));

		assertNull(board.pieceAt(new Square("A2")));
	}

	@Test
	void processRemoveMutationToEmptySquareFails() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)));

		assertEquals("The square A2 is empty, instead of containing white pawn.", exception.getMessage());
	}

	@Test
	void processRemoveMutationToSquareContainingWrongPieceFails() {
		board.process(new BoardMutation(WHITE_KNIGHT, A2, ADD));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)));

		assertEquals("The square A2 does not contain white pawn. It contains white knight.", exception.getMessage());
	}

	@Test
	void toStringAfterInitialMove() {
		board.process(Move.INITIAL_BOARD);
		String actualBoard = board.toString();

		assertEquals(
				"RKBQKBKR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* * * * \n" +
				" * * * *\n" +
				"pppppppp\n" +
				"rkbqkbkr\n",
				actualBoard);
	}

	@Test
	public void validMovesForEmptyBoard() {
		assertEquals(emptyList(), board.validMoves(WHITE));
		assertEquals(emptyList(), board.validMoves(BLACK));
	}

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
}