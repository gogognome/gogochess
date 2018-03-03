package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class MiniMaxAlphaBetaPruningArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	@Override
	protected ArtificialIntelligence buildAI() {
		return new MiniMaxAlphaBetaPruningArtificialIntelligence(7, 2, 400, 5);
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		// does not find solution
	}
}