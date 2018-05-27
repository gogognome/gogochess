package nl.gogognome.gogochess.juice;

import com.google.inject.*;
import nl.gogognome.gogochess.gui.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.ai.positionalanalysis.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class Module extends AbstractModule {

	@Override
	protected void configure() {
		bind(MoveNotation.class).to(ReverseAlgebraicNotation.class);
	}

	@Provides
	@Singleton
	BoardEvaluator provideBoardEvaluator() {
		return new CompositeBoardEvaluator(
				new CheckMateBoardEvaluator(),
				new PieceValueEvaluator());
	}

	@Provides
	ArtificialIntelligence provideArtificialIntelligence(
			MiniMaxAlphaBetaArtificialIntelligence miniMaxAlphaBetaArtificialIntelligence) {
		return new OpeningsDatabaseArtificialIntelligenceWrapper(miniMaxAlphaBetaArtificialIntelligence);
	}

	@Provides
	MiniMaxAlphaBetaArtificialIntelligence provideMiniMaxAlphaBetaArtificialIntelligence(
			BoardEvaluator boardEvaluator,
			PositionalAnalysis positionalAnalysis,
			MoveSort moveSort) {
		Statistics statistics = new Statistics();
		QuiescenceSearch quiescenceSearch = new QuiescenceSearch(boardEvaluator, statistics);
		return new MiniMaxAlphaBetaArtificialIntelligence(boardEvaluator, positionalAnalysis, moveSort, quiescenceSearch, statistics);
	}

	@Provides
	@Singleton
	GamePresentationModel provideGamePresentationModel(ArtificialIntelligence ai, Board board) {
		return new GamePresentationModel(ai, board);
	}

	@Provides
	BoardMovesAndSettingsPanel boardMovesAndSettingsPanel(GamePresentationModel presentationModel, MoveNotation moveNotation) {
		BoardPanel boardPanel = new BoardPanel(presentationModel);
		MovesPanel movesPanel = new MovesPanel(moveNotation, presentationModel);
		ProgressBar progressBar = new ProgressBar(presentationModel);
		SettingsPanel settingsPanel = new SettingsPanel(presentationModel);
		return new BoardMovesAndSettingsPanel(boardPanel, movesPanel, progressBar, settingsPanel);
	}
}
