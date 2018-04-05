package nl.gogognome.gogochess.logic.ai;

import org.junit.jupiter.api.*;

//maxDepth: 3
//evaluating 46561 positions took 23.735353679 s (1961.672896460571 positions/s
//generatin191589 positions took 23.735353679 s (8071.88309013948 positions/s
//maxDepth: 5
//evaluating 18083 positions took 37.205764346 s (486.02683798764923 positions/s
//generatin482175 positions took 37.205764346 s (12959.685373372493 positions/s
//maxDepth: 5
//evaluating 15192 positions took 1.49804405 s (10141.22381781764 positions/s
//generatin40555 positions took 1.49804405 s (27071.967610031224 positions/s
//maxDepth: 6
//evaluating 1822357 positions took 48.30310163 s (37727.53588287535 positions/s
//generatin2155884 positions took 48.30310163 s (44632.41339063468 positions/s
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