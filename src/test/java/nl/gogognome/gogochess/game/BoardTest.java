package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.PlayerPiece.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class BoardTest {

	private static final Square A2 = new Square("A2");

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
}