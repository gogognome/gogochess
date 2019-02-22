package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;

class CompositeBoardEvaluatorTest {

	private Board board = new Board();

	@BeforeEach
	void initBoard() {
		Move setup = new Move(WHITE,
				BLACK_KING.addTo(E4));
		board.process(setup);
	}

	@Test
	void compositeAddsValues() {
		CompositeBoardEvaluator compositeBoardEvaluator = new CompositeBoardEvaluator(
				new ConstEvaluator(forWhite(10, "")),
				new ConstEvaluator(forWhite(20, "")));
		assertEquals(forWhite(30, ""), compositeBoardEvaluator.value(board));
	}

	@Test
	void blackDoesNotSacrificeItsPieces() {
		Injector injector = Guice.createInjector(new Module());
		BoardEvaluator boardEvaluator = injector.getInstance(BoardEvaluator.class);

		board = new Board();
		board.process(new Move(WHITE,
				WHITE_PAWN.addTo(E5),
				BLACK_PAWN.addTo(F7)));
		MoveValue goodSituation = boardEvaluator.value(board);

		board = new Board();
		board.process(new Move(WHITE,
				WHITE_PAWN.addTo(E5)));
		MoveValue badSituation = boardEvaluator.value(board);

		assertTrue(goodSituation.isLessThan(badSituation), goodSituation + " should be smaller than " + badSituation);
	}

	private static class ConstEvaluator implements BoardEvaluator {

		private final MoveValue value;

		private ConstEvaluator(MoveValue value) {
			this.value = value;
		}

		@Override
		public MoveValue value(Board board) {
			return value;
		}
	}
}