package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

abstract class ArtificalIntelligenceTest {

	private Board board = new Board();

	protected abstract ArtificialIntelligence buildAI();

	@Test
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		board.process(new Move("initial setup", BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		ArtificialIntelligence ai = buildAI();
		Move move = ai.nextMove(board, WHITE, percentage -> {});

		assertTrue("Qg1-h1++, Qg1-h2++, Qg1-g7++, Qg1-g8++".contains(move.getDescription()), move.getDescription());
		assertEquals(CHECK_MATE, move.getStatus());
	}

	@Test
	void aiFindsMoveLeadingToCheckMateInTwoMoves() {
		Move initialMove = new Move("initial setup", BLACK,
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

		assertNextMoves(WHITE, "Qh3xh7+", "Kh8xQh7", "Rf5-h5++");
	}

	@Test
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		Move initialMove = new Move("initial setup", WHITE,
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

		assertNextMoves(BLACK, "Nc6-d4+", "Ke2xRd1", "Ng4-e3+", "Kd1-c1", "Nd4-e2++");
	}

	private void assertNextMoves(Player player, String... expectedMoves) {
		ArtificialIntelligence ai = buildAI();

		Move move = ai.nextMove(board, player, percentage -> {});
		assertNotNull(move);

		List<Move> actualMoves = Move.bestMovesForward(move.getPrecedingMove());
		List<String> expectedMoveStrings = asList(expectedMoves).subList(0, min(actualMoves.size(), expectedMoves.length));
		assertEquals(expectedMoveStrings.toString(), actualMoves.toString());
	}
}
