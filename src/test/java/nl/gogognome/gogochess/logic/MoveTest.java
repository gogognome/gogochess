package nl.gogognome.gogochess.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;

class MoveTest {

	@Test
	void initalBoard() {
		Board board = new Board();
		board.process(Move.INITIAL_BOARD);
		String actualBoard = board.toString();

		String boardWithoutEmptySquares = actualBoard.replaceAll("[ *\n]", "");

		assertEquals(
				"RKBQKBKR" +
				"PPPPPPPP" +
				"pppppppp" +
				"rkbqkbkr",
				boardWithoutEmptySquares);
	}

}