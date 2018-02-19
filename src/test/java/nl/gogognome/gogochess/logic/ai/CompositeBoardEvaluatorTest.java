package nl.gogognome.gogochess.logic.ai;

import static java.lang.Integer.*;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class CompositeBoardEvaluatorTest {

	private Board board = new Board();

	@BeforeEach
	void initBoard() {
		Move setup = new Move("setup", WHITE,
				BLACK_KING.addTo(E4));
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

	@Test
	void blackDoesNotSacrificeItsPieces() {
		board = new Board();
		board.process(new Move("setup", WHITE,
				WHITE_PAWN.addTo(E5),
				BLACK_PAWN.addTo(F7)));
		int goodSituation = ComplexBoardEvaluator.newInstance().value(board);

		board = new Board();
		board.process(new Move("setup", WHITE,
				WHITE_PAWN.addTo(E5)));
		int badSituation = ComplexBoardEvaluator.newInstance().value(board);

		assertTrue(goodSituation < badSituation, goodSituation + " should be smaller than " + badSituation);
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