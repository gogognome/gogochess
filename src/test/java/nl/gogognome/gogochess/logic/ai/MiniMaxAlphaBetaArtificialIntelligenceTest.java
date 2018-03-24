package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

class MiniMaxAlphaBetaArtificialIntelligenceTest extends ArtificialIntelligenceTest {

	private int maxDepth = 2;
	private int initialAlfa = Integer.MIN_VALUE;
	private int initialBeta = Integer.MAX_VALUE;

	@Override
	protected ArtificialIntelligence buildAI() {
		return new MiniMaxAlphaBetaArtificialIntelligence(maxDepth, initialAlfa, initialBeta);
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		maxDepth = 1;
		initialAlfa = Integer.MAX_VALUE - 100;
		super.aiFindsMoveLeadingToCheckMateInOneMove();
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInThreeMoves() {
		maxDepth = 5;
		initialBeta = Integer.MIN_VALUE + 100;
		super.aiFindsMoveLeadingToCheckMateInThreeMoves();
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateWithQueenAndRook() {
		maxDepth = 2;
		initialBeta = Integer.MIN_VALUE + 100;
		super.aiFindsMoveLeadingToCheckMateWithQueenAndRook();
	}
}