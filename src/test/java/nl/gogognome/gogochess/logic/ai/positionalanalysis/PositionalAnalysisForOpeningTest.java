package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class PositionalAnalysisForOpeningTest {

	private PositionalAnalysisForOpening positionalAnalysisForOpening = new PositionalAnalysisForOpening(
			new CastlingHeuristics(),
			new CentralControlHeuristic(),
			new PawnHeuristicsOpeningAndMiddleGame(-10));

	private SingleMoveEvaluator evaluator = SingleMoveEvaluator.forConsumer((board, move) -> positionalAnalysisForOpening.evaluate(board, asList(move)));
	
	@Test
	void whiteKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
		MoveValue towardsCenterValue = evaluator.valueOfMove(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(C3));
		MoveValue awayFromCenterValue = evaluator.valueOfMove(WHITE_KNIGHT.removeFrom(C3), WHITE_KNIGHT.addTo(B1));
		assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
	}

	@Test
	void blackKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
		MoveValue towardsCenterValue = evaluator.valueOfMove(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));
		MoveValue awayFromCenterValue = evaluator.valueOfMove(BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8));
		assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
	}

	@Test
	void e2_e4_mustBe30Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		assertThat(value).isEqualTo(forWhite(30 + 3 + 2, "")); // 30 for move, 3 for centrality and 2 for piece moving from king side
	}

	@Test
	void e3_e4_mustBe2Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_PAWN.removeFrom(E3), WHITE_PAWN.addTo(E4));
		assertThat(value).isEqualTo(forWhite(2 + 1 + 2, "")); // 2 for move, 1 for centrality and 2 for piece moving from king side
	}

	@Test
	void d2_d4_mustBe20Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_PAWN.removeFrom(D2), WHITE_PAWN.addTo(D4));
		assertThat(value).isEqualTo(forWhite(20 + 3, "")); // 20 for move and 3 for centrality
	}

	@Test
	void d3_d4_mustBe2Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_PAWN.removeFrom(D3), WHITE_PAWN.addTo(D4));
		assertThat(value).isEqualTo(forWhite(2 + 1, "")); // 2 for move and 1 for centrality
	}

	@Test
	void whiteShortCastlingMustBe30Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_KING.removeFrom(E1), WHITE_ROOK.removeFrom(H1), WHITE_KING.addTo(G1), WHITE_ROOK.addTo(F1));
		assertThat(value).isEqualTo(forWhite(30 + 4, "")); // 30 for move and 4 for centrality
	}

	@Test
	void blackShortCastlingMustBe30Points() {
		MoveValue value = evaluator.valueOfMove(BLACK_KING.removeFrom(E8), BLACK_ROOK.removeFrom(H8), BLACK_KING.addTo(G8), BLACK_ROOK.addTo(F8));
		assertThat(value).isEqualTo(forBlack(30 + 4, "")); // 30 for move and 4 for centrality
	}

	@Test
	void whiteLongCastlingMustBe10Points() {
		MoveValue value = evaluator.valueOfMove(WHITE_KING.removeFrom(E1), WHITE_ROOK.removeFrom(A1), WHITE_KING.addTo(C1), WHITE_ROOK.addTo(D1));
		assertThat(value).isEqualTo(forWhite(10 + 3, "")); // 10 for move and 3 for centrality
	}

	@Test
	void blackLongCastlingMustBe10Points() {
		MoveValue value = evaluator.valueOfMove(BLACK_KING.removeFrom(E8), BLACK_ROOK.removeFrom(A8), BLACK_KING.addTo(C8), BLACK_ROOK.addTo(D8));
		assertThat(value).isEqualTo(forBlack(10 + 3, "")); // 10 for move and 3 for centrality
	}

	@Test
	void whitePieceMovingToE3BlockingPawnMustBeMinus50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E2), WHITE_BISHOP.addTo(D2)),
				WHITE_BISHOP.removeFrom(D2), WHITE_BISHOP.addTo(E3));
		assertThat(value).isEqualTo(new MoveValue(6, 50, "")); // -50 for blocking pawn and 6 for centrality
	}

	@Test
	void blackPieceMovingToD3BlockingPawnMustBeMinus50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, WHITE_PAWN.addTo(D2), BLACK_BISHOP.addTo(C2)),
				BLACK_BISHOP.removeFrom(C2), BLACK_BISHOP.addTo(D3));
		assertThat(value).isEqualTo(forBlack(50 + 9, "")); // -50 for blocking pawn and -9 for centrality
	}

	@Test
	void whitePieceMovingToE6BlockingPawnMustBe50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, BLACK_PAWN.addTo(E7), WHITE_BISHOP.addTo(D7)),
				WHITE_BISHOP.removeFrom(D7), WHITE_BISHOP.addTo(E6));
		assertThat(value).isEqualTo(forWhite(50 + 6, "")); // 50 for blocking pawn and 6 for centrality
	}

	@Test
	void blackPieceMovingToD7BlockingPawnMustBe50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, WHITE_PAWN.addTo(D7), BLACK_BISHOP.addTo(C7)),
				BLACK_BISHOP.removeFrom(C7), BLACK_BISHOP.addTo(D6));
		assertThat(value).isEqualTo(new MoveValue(50, 9, "")); // 50 for blocking pawn and -9 for centrality
	}

	@Test
	void blackPawnCapturesKnightAndMovesRightTowardsCenterMustBeMinus5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(C7), WHITE_KNIGHT.addTo(D6)),
				BLACK_PAWN.removeFrom(C7), WHITE_KNIGHT.removeFrom(D6), BLACK_PAWN.addTo(D6));
		assertThat(value).isEqualTo(forBlack(8, "")); // -5 for moving towards center and -3 for centrality
	}

	@Test
	void whitePawnCapturesBishopAndMovesRightTowardsCenterMustBe5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(B2), BLACK_BISHOP.addTo(C3)),
				WHITE_PAWN.removeFrom(B2), BLACK_BISHOP.removeFrom(C3), WHITE_PAWN.addTo(C3));
		assertThat(value).isEqualTo(forWhite(8, "")); // 5 for moving towards center and 3 for centrality
	}

	@Test
	void blackPawnCapturesKnightAndMovesLeftTowardsCenterMustBeMinus5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(G7), WHITE_KNIGHT.addTo(F6)),
				BLACK_PAWN.removeFrom(G7), WHITE_KNIGHT.removeFrom(F6), BLACK_PAWN.addTo(F6));
		assertThat(value).isEqualTo(forBlack(10, "")); // -5 for moving towards center, -3 for centrality and -2 for moving from king side
	}

	@Test
	void whitePawnCapturesBishopAndMovesLeftTowardsCenterMustBeMinus5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(G2), BLACK_BISHOP.addTo(F3)),
				WHITE_PAWN.removeFrom(G2), BLACK_BISHOP.removeFrom(F3), WHITE_PAWN.addTo(F3));
		assertThat(value).isEqualTo(forWhite(10, "")); // 5 for moving towards center, 3 for centrality and 2 for moving from king side
	}

	@Test
	void blackPawnCapturesKnightAndMovesLeftAwayFromCenterMustBe5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(C7), WHITE_KNIGHT.addTo(B6)),
				BLACK_PAWN.removeFrom(C7), WHITE_KNIGHT.removeFrom(B6), BLACK_PAWN.addTo(B6));
		assertThat(value).isEqualTo(forBlack(-5, "")); //5 for moving away from center
	}

	@Test
	void whitePawnCapturesBishopAndMovesLeftAwayFromCenterMustBeMinus5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(C2), BLACK_BISHOP.addTo(B3)),
				WHITE_PAWN.removeFrom(C2), BLACK_BISHOP.removeFrom(B3), WHITE_PAWN.addTo(B3));
		assertThat(value).isEqualTo(forWhite(-5, "")); // -5 for moving away from center
	}

	@Test
	void blackPawnCapturesKnightAndMovesRightAwayFromCenterMustBe5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(F7), WHITE_KNIGHT.addTo(G6)),
				BLACK_PAWN.removeFrom(F7), WHITE_KNIGHT.removeFrom(G6), BLACK_PAWN.addTo(G6));
		assertThat(value).isEqualTo(forBlack(-3, "")); // 5 for moving towards center and -2 for moving from king side
	}

	@Test
	void whitePawnCapturesBishopAndMovesRightAwayFromCenterMustBeMinus5Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(F2), BLACK_BISHOP.addTo(G3)),
				WHITE_PAWN.removeFrom(F2), BLACK_BISHOP.removeFrom(G3), WHITE_PAWN.addTo(G3));
		assertThat(value).isEqualTo(forWhite(-3, "")); // -5 for moving away from center and 2 for moving from king side
	}

	@Test
	void blackPawnCapturesKnightAndResultsInMultipledIsolatedPawnsMustBeMinus10Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(F7), BLACK_PAWN.addTo(G7), WHITE_KNIGHT.addTo(G6)),
				BLACK_PAWN.removeFrom(F7), WHITE_KNIGHT.removeFrom(G6), BLACK_PAWN.addTo(G6));
		assertThat(value).isEqualTo(forBlack(-13, "")); // 10 for multipled isolated pawns, 5 for moving towards center and -2 for moving from king side
	}

	@Test
	void whitePawnCapturesBishopAndResultsInMultipledIsolatedPawnsMustBeMinus10Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(F2), WHITE_PAWN.addTo(G2), BLACK_BISHOP.addTo(G3)),
				WHITE_PAWN.removeFrom(F2), BLACK_BISHOP.removeFrom(G3), WHITE_PAWN.addTo(G3));
		assertThat(value).isEqualTo(forWhite(-13, "")); // -10 for multipled isolated pawns, -5 for moving away from center and 2 for moving from king side
	}

	@Test
	void blackPawnCapturesIsolatedCenterPawnMustBe50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(F7), WHITE_PAWN.addTo(E6)),
				BLACK_PAWN.removeFrom(F7), WHITE_PAWN.removeFrom(E6), BLACK_PAWN.addTo(E6));
		assertThat(value).isEqualTo(forBlack(60, "")); // -50 for capturing isolated center pawn, -3 for moving towards center, -5 for capturing towards center and -2 for moving from king side
	}

	@Test
	void whitePawnCapturesIsolatedCenterPawnMustBe50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(C2), BLACK_PAWN.addTo(D3)),
				WHITE_PAWN.removeFrom(C2), BLACK_PAWN.removeFrom(D3), WHITE_PAWN.addTo(D3));
		assertThat(value).isEqualTo(forWhite(50 + 3 + 5, "")); // 50 for capturing isolated center pawn, 3 for moving towards center and 5 for capturing towards center
	}

	@Test
	void blackPawnCapturesSupportedCenterPawnMustBeMinus15Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(F7), WHITE_PAWN.addTo(E6), WHITE_PAWN.addTo(D5)),
				BLACK_PAWN.removeFrom(F7), WHITE_PAWN.removeFrom(E6), BLACK_PAWN.addTo(E6));
		assertThat(value).isEqualTo(forBlack(-5, "")); // 15 for capturing supported center pawn, -3 for moving towards center, -5 for capturing towards center and -2 for moving from king side
	}

	@Test
	void whitePawnCapturesSupportedCenterPawnMustBeMinus50Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(C2), BLACK_PAWN.addTo(D3), BLACK_PAWN.addTo(E4)),
				WHITE_PAWN.removeFrom(C2), BLACK_PAWN.removeFrom(D3), WHITE_PAWN.addTo(D3));
		assertThat(value).isEqualTo(forWhite(-7, "")); // -15 for capturing supported center pawn, 3 for moving towards center and 5 for capturing towards center
	}
	@Test
	void blackWingPawnAdvanceMustBeMinus10Points() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(H7)),
				BLACK_PAWN.removeFrom(H7), BLACK_PAWN.addTo(H5));
		assertThat(value).isEqualTo(forBlack(-8, "")); // 10 for advancing wing pawn, -2 for moving towards center and
	}

	@Test
	void whiteWingPawnAdvanceMustBeMinus10Points() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(A2)),
				WHITE_PAWN.removeFrom(A2), WHITE_PAWN.addTo(A4));
		assertThat(value).isEqualTo(forWhite(-8, "")); // -10 for advancing wing pawn and 2 for moving towards center
	}

	@Test
	void whiteKingSideRookMovingDoesNotGetPointsForMovingFromKingSide() {
		MoveValue value = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(H1)),
				WHITE_ROOK.removeFrom(H1), WHITE_ROOK.addTo(G1));

		MoveValue expectedValue = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(A1)),
				WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(B1));

		assertThat(value).isEqualTo(expectedValue);
	}

	@Test
	void blackKingSideRookMovingDoesNotGetPointsForMovingFromKingSide() {
		MoveValue value = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(H8)),
				BLACK_ROOK.removeFrom(H8), BLACK_ROOK.addTo(G8));

		MoveValue expectedValue = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(A8)),
				BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(B8));

		assertThat(value).isEqualTo(expectedValue);
	}

	@Test
	void pawnHeuristicsDoNotThrowExceptionForWhiteEnPassentCapture() {
		Move setup = new Move(WHITE, BLACK_PAWN.addTo(D7), WHITE_PAWN.addTo(E5));
		MoveValue value = evaluator.valueOfMove(new Move(setup, BLACK_PAWN.removeFrom(D7), BLACK_PAWN.addTo(D5)),
				WHITE_PAWN.removeFrom(E5), BLACK_PAWN.removeFrom(D5), WHITE_PAWN.addTo(D6));
		assertThat(value).isEqualTo(forWhite(56, ""));
	}

	@Test
	void pawnHeuristicsDoNotThrowExceptionForBlackEnPassentCapture() {
		Move setup = new Move(BLACK, WHITE_PAWN.addTo(D2), BLACK_PAWN.addTo(E4));
		MoveValue value = evaluator.valueOfMove(new Move(setup, WHITE_PAWN.removeFrom(D2), WHITE_PAWN.addTo(D4)),
				BLACK_PAWN.removeFrom(E4), WHITE_PAWN.removeFrom(D4), BLACK_PAWN.addTo(D3));
		assertThat(value).isEqualTo(forBlack(56, ""));
	}

}