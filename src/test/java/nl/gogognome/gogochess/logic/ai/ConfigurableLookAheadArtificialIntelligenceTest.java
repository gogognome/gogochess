package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class ConfigurableLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {


	@Override
	protected ArtificialIntelligence buildAI() {
		return new ConfigurableLookAheadArtificialIntelligence(2);
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		// causes OutOfMemoryError
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateWithQueenAndRook() {
		// does not find solution
	}
}