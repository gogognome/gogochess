package nl.gogognome.gogochess.logic.ai;

import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

class OpeningsDatabaseArtificialIntelligenceWrapperTest {

	private ReverseAlgebraicNotation moveNotation = new ReverseAlgebraicNotation();

	@Test
	void allOpeningsConsistOfValidMoves() {
		for (String[] openingMoves : OpeningsDatabaseArtificialIntelligenceWrapper.OPENINGS) {
			assertAllMovesValid(openingMoves);
		}
	}

	private void assertAllMovesValid(String[] openingMoves) {
		List<Move> moves = new BoardSetup(moveNotation).parseMoves(openingMoves);
		assertThat(moves).hasSize(openingMoves.length);
	}
}