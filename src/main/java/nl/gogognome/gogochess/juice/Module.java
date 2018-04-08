package nl.gogognome.gogochess.juice;

import com.google.inject.*;
import nl.gogognome.gogochess.gui.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class Module extends AbstractModule {
	@Override
	protected void configure() {
		bind(MoveNotation.class).to(ReverseAlgebraicNotation.class);

		bind(ArtificialIntelligence.class).to(MiniMaxAlphaBetaArtificialIntelligence.class);
		bind(BoardEvaluator.class).toInstance(new CompositeBoardEvaluator(
				new CheckMateBoardEvaluator(),
				new PieceValueEvaluator(1000)));

	}

	@Provides
	OpeningsDatabaseArtificialIntelligenceWrapper provideAIIncludingOpeningDatabase(ArtificialIntelligence ai) {
		return new OpeningsDatabaseArtificialIntelligenceWrapper(ai);
	}

	@Provides
	BoardPanel boardPanel(Board board, MoveNotation moveNotation) {
		return new BoardPanel(board, moveNotation, 100);
	}
}
