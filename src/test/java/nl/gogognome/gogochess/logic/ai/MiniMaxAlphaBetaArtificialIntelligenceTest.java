package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class MiniMaxAlphaBetaArtificialIntelligenceTest extends ArtificalIntelligenceTest {

	private int maxDepth = 5;
	private int initialAlfa = Integer.MIN_VALUE;
	private int initialBeta = Integer.MAX_VALUE;

	@Override
	protected ArtificialIntelligence buildAI() {
		return new MiniMaxAlphaBetaArtificialIntelligence(maxDepth, initialAlfa, initialBeta);
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		maxDepth = 6;
		initialBeta = -19000;
		super.aiFindsMoveLeadingToCheckMateInThreeMoves();
	}
}