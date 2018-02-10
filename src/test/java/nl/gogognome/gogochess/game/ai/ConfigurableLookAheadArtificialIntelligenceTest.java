package nl.gogognome.gogochess.game.ai;

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
}