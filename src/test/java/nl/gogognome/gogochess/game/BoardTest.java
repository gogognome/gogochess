package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.PlayerPiece.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.*;

class BoardTest {

	private static final Square A2 = new Square("A2");

	private Board board = new Board();

	@Test
	public void processAddMutationToEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, new Square("A2"), ADD));

		assertEquals(WHITE_PAWN, board.pieceAt(new Square("A2")));
	}

	@Test
	public void processAddMutationToNonEmptySquareFails() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, ADD)));

		assertEquals("The square A2 is not empty. It contains white pawn", exception.getMessage());
	}
}