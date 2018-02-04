package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.Board.BLACK_PAWN;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static nl.gogognome.gogochess.game.Squares.*;
import static nl.gogognome.gogochess.game.Squares.H7;
import static nl.gogognome.gogochess.game.Status.CHECK_MATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.*;

class AdaptiveLookAheadArtificialIntelligenceTest {

	private Board board = new Board();

	@Test
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		board.process(new Move("initial setup", BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		AdaptiveLookAheadArtificialIntelligence ai = new AdaptiveLookAheadArtificialIntelligence(10000, 100000);
		Move move = ai.nextMove(board, WHITE);
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

		AdaptiveLookAheadArtificialIntelligence ai = new AdaptiveLookAheadArtificialIntelligence(10000, 1000000);
		Move move = ai.nextMove(board, WHITE);
		assertNotNull(move);
		assertEquals("[Qh3xh7+, Kh8xQh7, Rf5-h5++]", Move.bestMovesForward(initialMove).toString());
	}

	@Test
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		Move initialMove = new Move("initial setup", WHITE,
				WHITE_PAWN.addTo(A2),
				WHITE_KING.addTo(E1),
				WHITE_BISHOP.addTo(E2),
				WHITE_ROOK.addTo(H1),
				WHITE_PAWN.addTo(H2),
				WHITE_QUEEN.addTo(H8),
				BLACK_PAWN.addTo(D6),
				BLACK_KNIGHT.addTo(E4),
				BLACK_PAWN.addTo(E5),
				BLACK_KING.addTo(E7),
				BLACK_QUEEN.addTo(F4),
				BLACK_PAWN.addTo(F7),
				BLACK_PAWN.addTo(G7),
				BLACK_PAWN.addTo(H7));
		board.process(initialMove);

		AdaptiveLookAheadArtificialIntelligence ai = new AdaptiveLookAheadArtificialIntelligence(20000, 1000000);
		Move move = ai.nextMove(board, BLACK);
		assertNotNull(move);
		assertEquals("[Qh3xh7+, Kh8xQh7, Rf5-h5++]", Move.bestMovesForward(initialMove).toString());
	}
}