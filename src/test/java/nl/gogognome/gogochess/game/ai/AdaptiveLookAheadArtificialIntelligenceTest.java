package nl.gogognome.gogochess.game.ai;

class AdaptiveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new AdaptiveLookAheadArtificialIntelligence(20000, 2000000);
	}
}