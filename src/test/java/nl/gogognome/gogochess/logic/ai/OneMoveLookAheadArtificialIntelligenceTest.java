package nl.gogognome.gogochess.logic.ai;

class OneMoveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new OneMoveLookAheadArtificialIntelligence();
	}
}