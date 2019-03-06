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
	BoardEvaluator provideBoardEvaluator() {
		return new CompositeBoardEvaluator(
				new EndOfGameBoardEvaluator(),
				new PieceValueEvaluator());
	}

	@Provides
	ArtificialIntelligence provideArtificialIntelligence(
			MiniMaxAlphaBetaArtificialIntelligence miniMaxAlphaBetaArtificialIntelligence) {
		return new OpeningsDatabaseArtificialIntelligenceWrapper(miniMaxAlphaBetaArtificialIntelligence);
	}

	@Provides
	AiController provideArtiAiController(ArtificialIntelligence ai, MoveNotation moveNotation) {
		return new AiController(ai, moveNotation);
	}

	@Provides
	MiniMaxAlphaBetaArtificialIntelligence provideMiniMaxAlphaBetaArtificialIntelligence(
			BoardEvaluator boardEvaluator,
			PositionalAnalysis positionalAnalysis,
			MoveSort moveSort) {
		Statistics statistics = new Statistics();
		KillerHeuristic killerHeuristic = new KillerHeuristic();
		QuiescenceSearch quiescenceSearch = new QuiescenceSearch(boardEvaluator, statistics, killerHeuristic);
		return new MiniMaxAlphaBetaArtificialIntelligence(boardEvaluator, positionalAnalysis, moveSort, statistics, quiescenceSearch, killerHeuristic);
	}

	@Provides
	@Singleton
	GamePresentationModel provideGamePresentationModel(AiController aiController, Board board, MoveNotation moveNotation) {
		return new GamePresentationModel(aiController, board, moveNotation);
	}

	@Provides
	BoardMovesAndSettingsPanel provideBoardMovesAndSettingsPanel(GamePresentationModel presentationModel, MoveNotation moveNotation) {
		BoardPanel boardPanel = new BoardPanel(presentationModel);
		MovesPanel movesPanel = new MovesPanel(moveNotation, presentationModel);
		ProgressBar progressBar = new ProgressBar(presentationModel);
		SettingsPanel settingsPanel = new SettingsPanel(presentationModel);
		return new BoardMovesAndSettingsPanel(boardPanel, movesPanel, progressBar, settingsPanel);
	}
	
	@Provides
	PositionalAnalysis providePositionalAnalysis(PieceValueEvaluator pieceValueEvaluator) {
		return new PositionalAnalysis(pieceValueEvaluator);
	}
}
