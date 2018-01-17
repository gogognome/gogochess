package nl.gogognome.gogochess.game;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.PlayerPiece.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

class BoardTest {

	private static final Square A2 = new Square("A2");
	private static final Square A3 = new Square("A3");

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
	void validMovesForPawn() {
		Move setup = new Move("setup", null, new BoardMutation(WHITE_PAWN, A2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves(WHITE);

		assertEquals(singleton(setup), moves.stream().map(Move::getPrecedingMove).collect(toSet()));
		assertEquals(
				asList(asList(new BoardMutation(WHITE_PAWN, A2, REMOVE), new BoardMutation(WHITE_PAWN, A3, ADD))),
				moves.stream().map(Move::getBoardMutations).collect(toList()));
	}
}