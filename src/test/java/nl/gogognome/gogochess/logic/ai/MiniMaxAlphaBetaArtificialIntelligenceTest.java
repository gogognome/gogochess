package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.*;
import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

class MiniMaxAlphaBetaArtificialIntelligenceTest {

	private Board board = new Board();
	private ReverseAlgebraicNotation moveNotation = new ReverseAlgebraicNotation();

	@Test
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		ArtificialIntelligence ai = buildAI(1);
		Move move = ai.nextMove(board, WHITE, new ProgressListener());

		assertThat("Qg1-h1++, Qg1-h2++, Qg1-g7++, Qg1-g8++").contains(moveNotation.format(move));
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

		assertNextMoves(3,
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

		assertNextMovesOneOf(5,
				BLACK,
				asList("Nc6-d4+", "Ke2xRd1", "Ng4-e3+", "Kd1-c1", "Nd4-e2++"),
				asList("Rd1-h1", "Bc4xf7+", "Ke8xBf7", "Bd8xc7", "Nc6-d4++"),
				asList("Rd1xNb1", "Bd8xc7", "Rb1xRa1", "Bc7xd6", "Nc6-d4++"));
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

		assertNextMovesOneOf(7,
				BLACK,
				asList("Qd4-f2", "Kh5-h6", "Rc3-c7", "Kh6-g6", "Qf2-f7+", "Kg6-h6", "Qf7-h7++"),
				asList("Qd4-f2", "g5-g6", "Qf2-f5+", "Kh5-h6", "Rc3-c7", "g6-g7", "Rc7-c6++"),
				asList("Rc3-c8", "Kh5-g6", "Rc8-d8", "Kg6-h7", "Rd8-e8", "g5-g6", "Re8-h8++"),
				asList("Qd4-f2", "Kh5-h6", "Rc3-c7", "Kh6-h5", "Rc7-h7+", "Kh5-g6", "Qf2-f7++"),
				asList("d3-d2", "Kh5-g6", "Qd4-e5", "Kg6-h7", "Qe5xg5", "Kh7-h8", "Rc3-h3++"),
				asList("Qd4-f2", "g5-g6", "Rc3-c7", "g6-g7", "Qf2-f5+", "Kh5-h6", "Rc7-c6++"));
	}

	@Test
	void aiShouldNotThrowNullPointerException() {
		new BoardSetup(moveNotation)
				.setupBoard(board,
						"e2-e4", "d7-d5",
						"Nb1-c3", "d5-d4",
						"Nc3-d5", "c7-c6",
						"Nd5-f4", "e7-e5",
						"Nf4-e2", "d4-d3",
						"Ne2-c3", "d3xc2");

		ArtificialIntelligence ai = buildAI(3);
		Move nextMove = ai.nextMove(board, WHITE, new ProgressListener());

		String actualMove = moveNotation.format(nextMove);
		assertThat(asList("Qd1-e2", "Qd1xc2", "Qd1-f3")).contains(actualMove);
	}

	@Test
	void depth5CausesVeryDeepQuiesenceSearch_aiShouldFindSolutionQuickly() {
		new BoardSetup(moveNotation)
				.setupBoard(board,
						"e2-e4", "d7-d6",
						"Ng1-f3", "Ng8-f6",
						"Bf1-c4", "Nf6xe4",
						"d2-d3", "Ne4-c5",
						"O-O", "Nb8-c6",
						"Bc4-b5", "Bc8-f5",
						"Bc1-f4");

		ArtificialIntelligence ai = buildAI(3);
		Move nextMove = ai.nextMove(board, board.currentPlayerOpponent(), new ProgressListener());

		assertThat(moveNotation.format(nextMove)).isEqualTo("e7-e5");
	}

	@Test
	void aiShouldNotSacrifceQueen() {
		new BoardSetup(moveNotation)
				.setupBoard(board,
						"e2-e4", "e7-e5",
						"d2-d4", "d7-d6",
						"Ng1-f3", "Bc8-g4",
						"Nb1-c3", "Bg4xNf3");

		assertNextMovesOneOf(3,
				WHITE,
				asList("g2xBf3", "e5xd4", "Qd1xd4", "Qd8-f6"),
				asList("g2xBf3", "e5xd4", "Qd1xd4", "Qd8-f6"));
	}

	@Test
	void quiescenceNeededToFigureOutCaptures() {
		Move initialMove = new Move(BLACK,
				WHITE_KING.addTo(A1),
				WHITE_ROOK.addTo(B1),
				WHITE_PAWN.addTo(A2),
				WHITE_PAWN.addTo(B2),
				WHITE_QUEEN.addTo(C4),
				WHITE_KNIGHT.addTo(E3),
				WHITE_BISHOP.addTo(E4),
				BLACK_KING.addTo(H8),
				BLACK_ROOK.addTo(G8),
				BLACK_PAWN.addTo(H7),
				BLACK_PAWN.addTo(G7),
				BLACK_QUEEN.addTo(F7),
				BLACK_BISHOP.addTo(F4),
				BLACK_ROOK.addTo(D5));
		board.process(initialMove);

		assertNextMovesOneOf(1,
				WHITE,
				asList("Be4xRd5", "Qf7-c7", "Bd5xRg8", "Qc7xQc4", "Bg8xQc4", "Bf4xNe3"),
				asList("Be4xRd5", "Qf7-c7", "Bd5xRg8", "Bf4xNe3", "Qc4xQc7", "Kh8xBg8", "Qc7xg7+", "Kg8xQg7"),
				asList("Qc4xRd5", "Qf7xQd5", "Ne3xQd5"),
				asList("Be4xRd5", "Rg8-f8", "Bd5xQf7", "Rf8xBf7", "Qc4xBf4", "Rf7xQf4"),
				asList("Be4xRd5", "Qf7-c7", "Qc4xQc7", "Bf4xQc7", "Bd5xRg8", "Kh8xBg8"));
	}

	private void assertNextMoves(int maxDepth, Player player, String... expectedMoves) {
		ArtificialIntelligence ai = buildAI(maxDepth);

		AtomicReference<List<Move>> actualMoves = new AtomicReference<>();
		ai.nextMove(board, player, new ProgressListener().withBestMovesConsumer(actualMoves::set));

		String actualMovesString = format(actualMoves.get());
		List<String> expectedMoveStrings = takePartNoLongerThanActualMoves(actualMoves.get(), asList(expectedMoves));
		assertThat(actualMovesString).isEqualTo(expectedMoveStrings.toString());
	}

	@Test
	void quiescenceNeededToFigureOutCaptures2() {
		Move initialMove = new Move(BLACK,
				WHITE_KING.addTo(H1),
				WHITE_QUEEN.addTo(H5),
				BLACK_KING.addTo(H8),
				BLACK_ROOK.addTo(G6),
				BLACK_PAWN.addTo(H7),
				BLACK_PAWN.addTo(G7),
				BLACK_QUEEN.addTo(F7));
		board.process(initialMove);

		ArtificialIntelligence ai = buildAI(0);
		Move nextMove = ai.nextMove(board, board.currentPlayerOpponent(), new ProgressListener());

		assertThat(moveNotation.format(nextMove)).isNotEqualTo("Qh5xRg6");
	}

	@SafeVarargs
	private final void assertNextMovesOneOf(int maxDepth, Player player, List<String>... possibleExpectedMoves) {
		ArtificialIntelligence ai = buildAI(maxDepth);

		AtomicReference<List<Move>> actualMoves = new AtomicReference<>();
		ai.nextMove(board, player, new ProgressListener().withBestMovesConsumer(actualMoves::set));
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

	private ArtificialIntelligence buildAI(int maxDepth) {
		MiniMaxAlphaBetaArtificialIntelligence ai = Guice.createInjector(new Module()).getInstance(MiniMaxAlphaBetaArtificialIntelligence.class);
		ai.setMaxDepth(maxDepth);
		return ai;
	}
}