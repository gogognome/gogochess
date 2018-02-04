package nl.gogognome.gogochess.game.ai;

import static java.lang.Integer.MAX_VALUE;
import static nl.gogognome.gogochess.game.Board.BLACK_KING;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static nl.gogognome.gogochess.game.Squares.E4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.*;

class CompositeBoardEvaluatorTest {

	private final Board board = new Board();

	@BeforeEach
	public void initBoard() {
		Move setup = new Move("setup", WHITE,
				new BoardMutation(BLACK_KING, E4, ADD));
		board.process(setup);
	}

	@Test
	void compositeAddsValues() {
		CompositeBoardEvaluator compositeBoardEvaluator = new CompositeBoardEvaluator(new ConstEvaluator(10), new ConstEvaluator(20));
		assertEquals(30, compositeBoardEvaluator.value(board));
	}

	@Test
	void compositeCapsAddedNumbersToMaxValue() {
		CompositeBoardEvaluator compositeBoardEvaluator = new CompositeBoardEvaluator(new ConstEvaluator(MAX_VALUE), new ConstEvaluator(10));
		assertEquals(MAX_VALUE, compositeBoardEvaluator.value(board));
	}

	private static class ConstEvaluator implements BoardEvaluator {

		private final int value;

		private ConstEvaluator(int value) {
			this.value = value;
		}

		@Override
		public int value(Board board) {
			return value;
		}
	}
}