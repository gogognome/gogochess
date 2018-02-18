package nl.gogognome.gogochess.game.ai;

class MiniMaxAlphaBetaPruningArtificialIntelligenceTest extends ArtificalIntelligenceTest {


	@Override
	protected ArtificialIntelligence buildAI() {
		return new MiniMaxAlphaBetaPruningArtificialIntelligence(7, 0);
	}

}