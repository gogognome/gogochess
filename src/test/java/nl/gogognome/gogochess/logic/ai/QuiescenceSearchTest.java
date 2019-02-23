package nl.gogognome.gogochess.logic.ai;

import static java.lang.Integer.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;

class QuiescenceSearchTest {

	private Board board = new Board();
	private Statistics statistics = new Statistics();

	@Test
	void quiescenceRecurses() {
		Move initialMove = new Move(WHITE,
				WHITE_QUEEN.addTo(B1),
				BLACK_BISHOP.addTo(D3),
				BLACK_QUEEN.addTo(C2),
				BLACK_PAWN.addTo(A7));
		board.process(initialMove);

		Move move = new Move(initialMove, BLACK_PAWN.removeFrom(A7), BLACK_PAWN.addTo(A6));

		QuiescenceSearch quiescenceSearch = buildQuiecenceSearch();
		quiescenceSearch.search(board, move, MIN_VALUE, MAX_VALUE);

		assertThat(move.getValue().getCombinedScore()).isEqualTo(-430);
		assertThat(statistics.getNrPositionsGenerated()).isEqualTo(26);
		assertThat(statistics.getNrPositionsEvaluated()).isEqualTo(5);
	}

	@Test
	void whiteQueenTakesBishop_quiescenceDetectsThatBlackCanTakeWhiteQueen() {
		Move initialMove = new Move(BLACK,
				WHITE_QUEEN.addTo(B1),
				BLACK_BISHOP.addTo(C2),
				BLACK_QUEEN.addTo(D3));
		board.process(initialMove);

		Move move = new Move(initialMove, WHITE_QUEEN.removeFrom(B1), BLACK_BISHOP.removeFrom(C2), WHITE_QUEEN.addTo(C2));

		QuiescenceSearch quiescenceSearch = buildQuiecenceSearch();
		quiescenceSearch.search(board, move, MIN_VALUE, MAX_VALUE);

		assertThat(move.getValue().getCombinedScore()).isEqualTo(-900);
		assertThat(statistics.getNrPositionsGenerated()).isEqualTo(24);
		assertThat(statistics.getNrPositionsEvaluated()).isEqualTo(3);
	}

	private QuiescenceSearch buildQuiecenceSearch() {
		Injector injector = Guice.createInjector(new Module());
		BoardEvaluator boardEvaluator = injector.getInstance(BoardEvaluator.class);
		statistics.reset();
		return new QuiescenceSearch(boardEvaluator, statistics, new KillerHeuristic());
	}

}