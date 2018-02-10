package nl.gogognome.gogochess.game.ai;

class OneMoveLookAheadArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new OneMoveLookAheadArtificialIntelligence();
	}
}