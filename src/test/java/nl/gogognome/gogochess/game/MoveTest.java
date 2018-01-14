package nl.gogognome.gogochess.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;

class MoveTest {

	@Test
	void initialMove() {
		Board board = new Board();
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
}