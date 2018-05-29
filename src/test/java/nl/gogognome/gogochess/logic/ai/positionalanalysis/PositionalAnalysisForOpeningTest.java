package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class PositionalAnalysisForOpeningTest {

	private PositionalAnalysisForOpening analysis = new PositionalAnalysisForOpening(new CentralControlHeuristic(), new PawnHeuristics());

	@Test
	void whiteKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
		int towardsCenterValue = valueOfMove(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(C3));
		int awayFromCenterValue = valueOfMove(WHITE_KNIGHT.removeFrom(C3), WHITE_KNIGHT.addTo(B1));
		assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
	}

	@Test
	void blackKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
		int towardsCenterValue = valueOfMove(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));
		int awayFromCenterValue = valueOfMove(BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8));
		assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
	}

	@Test
	void e2_e4_mustBe30Points() {
		int value = valueOfMove(WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		assertThat(value).isEqualTo(30 + 3 + 2); // 30 for move, 3 for centrality and 2 for piece moving from king side
	}

	@Test
	void e3_e4_mustBe2Points() {
		int value = valueOfMove(WHITE_PAWN.removeFrom(E3), WHITE_PAWN.addTo(E4));
		assertThat(value).isEqualTo(2 + 1 + 2); // 2 for move, 1 for centrality and 2 for piece moving from king side
	}

	@Test
	void d2_d4_mustBe20Points() {
		int value = valueOfMove(WHITE_PAWN.removeFrom(D2), WHITE_PAWN.addTo(D4));
		assertThat(value).isEqualTo(20 + 3); // 20 for move and 3 for centrality
	}

	@Test
	void d3_d4_mustBe2Points() {
		int value = valueOfMove(WHITE_PAWN.removeFrom(D3), WHITE_PAWN.addTo(D4));
		assertThat(value).isEqualTo(2 + 1); // 2 for move and 1 for centrality
	}

	@Test
	void whiteShortCastlingMustBe30Points() {
		int value = valueOfMove(WHITE_KING.removeFrom(E1), WHITE_ROOK.removeFrom(H1), WHITE_KING.addTo(G1), WHITE_ROOK.addTo(F1));
		assertThat(value).isEqualTo(30 + 4); // 30 for move and 4 for centrality
	}

	@Test
	void blackShortCastlingMustBe30Points() {
		int value = valueOfMove(BLACK_KING.removeFrom(E8), BLACK_ROOK.removeFrom(H8), BLACK_KING.addTo(G8), BLACK_ROOK.addTo(F8));
		assertThat(value).isEqualTo(-30 - 4); // 30 for move and 4 for centrality
	}

	@Test
	void whiteLongCastlingMustBe10Points() {
		int value = valueOfMove(WHITE_KING.removeFrom(E1), WHITE_ROOK.removeFrom(A1), WHITE_KING.addTo(C1), WHITE_ROOK.addTo(D1));
		assertThat(value).isEqualTo(10 + 3); // 10 for move and 3 for centrality
	}

	@Test
	void blackLongCastlingMustBe10Points() {
		int value = valueOfMove(BLACK_KING.removeFrom(E8), BLACK_ROOK.removeFrom(A8), BLACK_KING.addTo(C8), BLACK_ROOK.addTo(D8));
		assertThat(value).isEqualTo(-10 - 3); // 10 for move and 3 for centrality
	}

	@Test
	void whitePieceMovingToE3BlockingPawnMustBeMinus50Points() {
		int value = valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E2), WHITE_BISHOP.addTo(D2)),
				WHITE_BISHOP.removeFrom(D2), WHITE_BISHOP.addTo(E3));
		assertThat(value).isEqualTo(-50 + 6); // -50 for blocking pawn and 6 for centrality
	}

	@Test
	void blackPieceMovingToD3BlockingPawnMustBeMinus50Points() {
		int value = valueOfMove(new Move(WHITE, WHITE_PAWN.addTo(D2), BLACK_BISHOP.addTo(C2)),
				BLACK_BISHOP.removeFrom(C2), BLACK_BISHOP.addTo(D3));
		assertThat(value).isEqualTo(-50 - 9); // -50 for blocking pawn and -9 for centrality
	}

	private int valueOfMove(BoardMutation... mutations) {
		Move setup = buildSetupMove(mutations);
		return valueOfMove(setup, mutations);
	}

	private int valueOfMove(Move setup, BoardMutation... mutations) {
		Move move = new Move(setup, mutations);
		Board board = new Board();
		board.process(move);
		analysis.evaluate(board, asList(move));
		return move.getValue();
	}

	private Move buildSetupMove(BoardMutation[] mutations) {
		Player player = mutations[0].getPlayerPiece().getPlayer().other();
		return new Move(player,
				Arrays.stream(mutations)
						.filter(m -> m.getMutation() == REMOVE)
						.map(m -> new BoardMutation(m.getPlayerPiece(), m.getSquare(), ADD))
						.toArray(size -> new BoardMutation[size]));
	}
}