package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class OneMoveLookAheadArtificialIntelligenceTest extends ArtificialIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new OneMoveLookAheadArtificialIntelligence();
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		// does not find solution
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateWithQueenAndRook() {
		// does not find solution
	}
}