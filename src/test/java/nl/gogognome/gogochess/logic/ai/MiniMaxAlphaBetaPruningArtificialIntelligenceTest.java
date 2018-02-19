package nl.gogognome.gogochess.logic.ai;

class MiniMaxAlphaBetaPruningArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new MiniMaxAlphaBetaPruningArtificialIntelligence(7, 2, 400);
	}

}