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
				new PieceValueEvaluator()));

	}

	@Provides
	OpeningsDatabaseArtificialIntelligenceWrapper provideAIIncludingOpeningDatabase(ArtificialIntelligence ai) {
		return new OpeningsDatabaseArtificialIntelligenceWrapper(ai);
	}

	@Provides
	MovesPanel movesPanel(MoveNotation moveNotation) {
		return new MovesPanel(moveNotation, 130, 8*100);
	}
	@Provides
	BoardPanel boardPanel(Board board, MovesPanel movesPanel) {
		return new BoardPanel(board, 100, movesPanel);
	}
}
