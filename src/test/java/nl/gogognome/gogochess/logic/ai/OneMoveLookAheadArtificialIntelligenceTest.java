package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class OneMoveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new OneMoveLookAheadArtificialIntelligence();
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		// does not find solution
	}
}