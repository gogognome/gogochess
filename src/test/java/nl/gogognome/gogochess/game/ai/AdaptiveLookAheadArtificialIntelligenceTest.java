package nl.gogognome.gogochess.game.ai;

class AdaptiveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new AdaptiveLookAheadArtificialIntelligence(10000, 1000000);
	}
}