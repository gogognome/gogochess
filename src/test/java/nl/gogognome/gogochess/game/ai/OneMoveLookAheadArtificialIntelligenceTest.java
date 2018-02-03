package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.Squares.*;
import static nl.gogognome.gogochess.game.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.*;

class OneMoveLookAheadArtificialIntelligenceTest {

	private Board board = new Board();

	@Test
	void aiFindsMoveLeadingToCheckMate() {
		board.process(new Move("initial setup", BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		OneMoveLookAheadArtificialIntelligence ai = new OneMoveLookAheadArtificialIntelligence();
		Move move = ai.nextMove(board, WHITE);
		assertTrue("Qg1-h1++, Qg1-h2++, Qg1-g7++, Qg1-g8++".contains(move.getDescription()), move.getDescription());
		assertEquals(CHECK_MATE, move.getStatus());
	}
}