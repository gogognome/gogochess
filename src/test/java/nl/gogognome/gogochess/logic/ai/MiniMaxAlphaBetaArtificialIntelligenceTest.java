package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;

class MiniMaxAlphaBetaArtificialIntelligenceTest extends ArtificialIntelligenceTest {

	private int maxDepth = 2;
	private int initialAlpha = Integer.MIN_VALUE;
	private int initialBeta = Integer.MAX_VALUE;

	@Override
	protected ArtificialIntelligence buildAI() {
		MiniMaxAlphaBetaArtificialIntelligence ai = Guice.createInjector(new Module()).getInstance(MiniMaxAlphaBetaArtificialIntelligence.class);
		ai.setInitialMaxDepth(maxDepth);
		ai.setInitialAlpha(initialAlpha);
		ai.setInitialBeta(initialBeta);
		return ai;
	}

	@Test
	@Override
	void aiFindsMoveLeadingToCheckMateInOneMove() {
		maxDepth = 1;
		initialAlpha = Integer.MAX_VALUE - 100;
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