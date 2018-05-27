package nl.gogognome.gogochess.logic.ai;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Board.BLACK_ROOK;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Squares.C3;
import static nl.gogognome.gogochess.logic.Status.CHECK_MATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

class MiniMaxAlphaBetaArtificialIntelligenceTest {

	private Board board = new Board();

	@Test
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		ArtificialIntelligence ai = buildAI(1, MAX_VALUE - 100, MAX_VALUE);
		Move move = ai.nextMove(board, WHITE, percentage -> {}, bestMoves -> {});

		assertThat("Qg1-h1++, Qg1-h2++, Qg1-g7++, Qg1-g8++").contains(new ReverseAlgebraicNotation().format(move));
		assertThat(move.getStatus()).isEqualTo(CHECK_MATE);
	}

	@Test
	void aiFindsMoveLeadingToCheckMateInTwoMoves() {
		Move initialMove = new Move(BLACK,
				WHITE_PAWN.addTo(A2),
				WHITE_KING.addTo(B1),
				WHITE_BISHOP.addTo(B3),
				WHITE_PAWN.addTo(C2),
				WHITE_ROOK.addTo(F5),
				WHITE_PAWN.addTo(G2),
				WHITE_QUEEN.addTo(H3),
				BLACK_ROOK.addTo(A8),
				BLACK_PAWN.addTo(B5),
				BLACK_QUEEN.addTo(B4),
				BLACK_PAWN.addTo(E5),
				BLACK_KNIGHT.addTo(G6),
				BLACK_PAWN.addTo(G7),
				BLACK_PAWN.addTo(H7),
				BLACK_KING.addTo(H8));
		board.process(initialMove);

		assertNextMoves(3, MIN_VALUE, MAX_VALUE,
				WHITE,
				"Qh3xh7+", "Kh8xQh7", "Rf5-h5++");
	}

	@Test
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		Move initialMove = new Move(WHITE,
				WHITE_ROOK.addTo(A1),
				WHITE_PAWN.addTo(A2),
				WHITE_KNIGHT.addTo(B1),
				WHITE_PAWN.addTo(B2),
				WHITE_PAWN.addTo(C2),
				WHITE_BISHOP.addTo(C4),
				WHITE_KNIGHT.addTo(D2),
				WHITE_PAWN.addTo(D3),
				WHITE_BISHOP.addTo(D8),
				WHITE_KING.addTo(E2),
				WHITE_PAWN.addTo(E4),
				WHITE_PAWN.addTo(G2),
				BLACK_PAWN.addTo(A7),
				BLACK_ROOK.addTo(A8),
				BLACK_PAWN.addTo(B7),
				BLACK_KNIGHT.addTo(C6),
				BLACK_PAWN.addTo(C7),
				BLACK_ROOK.addTo(D1),
				BLACK_PAWN.addTo(D6),
				BLACK_PAWN.addTo(E5),
				BLACK_KING.addTo(E8),
				BLACK_PAWN.addTo(F2),
				BLACK_PAWN.addTo(F7),
				BLACK_KNIGHT.addTo(G4),
				BLACK_PAWN.addTo(G7));
		board.process(initialMove);

		assertNextMovesOneOf(5, MIN_VALUE, MIN_VALUE + 100,
				BLACK,
				asList("Nc6-d4+", "Ke2xRd1", "Ng4-e3+", "Kd1-c1", "Nd4-e2++"),
				asList("Rd1-h1", "Bc4xf7+", "Ke8xBf7", "Bd8xc7", "Nc6-d4++"));
	}

	@Test
	void aiFindsMoveLeadingToCheckMateWithQueenAndRook() {
		Move initialMove = new Move(WHITE,
				WHITE_KING.addTo(H5),
				WHITE_PAWN.addTo(G5),
				BLACK_KING.addTo(B8),
				BLACK_PAWN.addTo(E4),
				BLACK_PAWN.addTo(D3),
				BLACK_QUEEN.addTo(D4),
				BLACK_ROOK.addTo(C3));
		board.process(initialMove);

		assertNextMovesOneOf(2, MIN_VALUE, MAX_VALUE,
				BLACK,
				asList("Qd4-f2", "Kh5-h6", "Rc3-c7", "Kh6-g6", "Qf2-f7+", "Kg6-h6", "Qf7-h7++"),
				asList("Qd4-f2", "g5-g6", "Qf2-f5+", "Kh5-h6", "Rc3-c7", "g6-g7", "Rc7-c6++"),
				asList("Rc3-c8", "Kh5-g6", "Rc8-d8", "Kg6-h7", "Rd8-e8", "g5-g6", "Re8-h8++"),
				asList("Qd4-f2", "Kh5-h6", "Rc3-c7", "Kh6-h5", "Rc7-h7+", "Kh5-g6", "Qf2-f7++"),
				asList("d3-d2", "Kh5-g6", "Qd4-e5", "Kg6-h7", "Qe5xg5", "Kh7-h8", "Rc3-h3++"));

	}

	private void assertNextMoves(int maxDepth, int initialAlpha, int initialBeta, Player player, String... expectedMoves) {
		ArtificialIntelligence ai = buildAI(maxDepth, initialAlpha, initialBeta);

		AtomicReference<List<Move>> actualMoves = new AtomicReference<>();
		ai.nextMove(board, player, percentage -> {}, actualMoves::set);

		String actualMovesString = format(actualMoves.get());
		List<String> expectedMoveStrings = takePartNoLongerThanActualMoves(actualMoves.get(), asList(expectedMoves));
		assertThat(actualMovesString).isEqualTo(expectedMoveStrings.toString());
	}

	@SafeVarargs
	private final void assertNextMovesOneOf(int maxDepth, int initialAlpha, int initialBeta, Player player, List<String>... possibleExpectedMoves) {
		ArtificialIntelligence ai = buildAI(maxDepth, initialAlpha, initialBeta);

		AtomicReference<List<Move>> actualMoves = new AtomicReference<>();
		ai.nextMove(board, player, percentage -> {}, actualMoves::set);
		String actualMovesString = format(actualMoves.get());

		for (List<String> expectedMoves : possibleExpectedMoves) {
			List<String> expectedMoveStrings = takePartNoLongerThanActualMoves(actualMoves.get(), expectedMoves);
			if (actualMovesString.equals(expectedMoveStrings.toString())) {
				return; // test passed
			}
		}
		fail("Actual moves " + actualMovesString + " is not equal to any of " + Arrays.toString(possibleExpectedMoves));
	}

	private String format(List<Move> actualMovesList) {
		return Moves.formatMoves(actualMovesList).toString();
	}

	private List<String> takePartNoLongerThanActualMoves(List<Move> actualMoves, List<String> expectedMoves) {
		return expectedMoves.subList(0, min(actualMoves.size(), expectedMoves.size()));
	}

	private ArtificialIntelligence buildAI(int maxDepth, int initialAlpha, int initialBeta) {
		MiniMaxAlphaBetaArtificialIntelligence ai = Guice.createInjector(new Module()).getInstance(MiniMaxAlphaBetaArtificialIntelligence.class);
		ai.setInitialMaxDepth(maxDepth);
		ai.setInitialAlpha(initialAlpha);
		ai.setInitialBeta(initialBeta);
		return ai;
	}
}