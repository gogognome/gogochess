package nl.gogognome.gogochess.logic.ai;

class AdaptiveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new AdaptiveLookAheadArtificialIntelligence(20000, 2000000);
	}
}