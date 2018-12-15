package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import java.util.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

/**
 * This class evaluates the opening positions. The implementation is based on the article
 * James J.Gillogly, "The Technology Chess Program" (1972).
 */
public class PositionalAnalysis implements MovesEvaluator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public PositionalAnalysis(PieceValueEvaluator pieceValueEvaluator) {
		this.pieceValueEvaluator = pieceValueEvaluator;

		positionalAnalysisForMiddleGame = new PositionalAnalysisForMiddleGame(
				new CastlingHeuristics(),
				centralControlHeuristic,
				new KingFieldHeuristic(),
				new PawnHeuristicsOpeningAndMiddleGame(-5),
				pieceValueEvaluator);
	}

	private enum Phase {
		OPENING,
		MIDDLE_GAME,
		END_GAME
	}
	
	private final CentralControlHeuristic centralControlHeuristic = new CentralControlHeuristic();
	private final PositionalAnalysisForOpening positionalAnalysisForOpening = new PositionalAnalysisForOpening(
			new CastlingHeuristics(),
			centralControlHeuristic,
			new PawnHeuristicsOpeningAndMiddleGame(-10));
	private final PositionalAnalysisForMiddleGame positionalAnalysisForMiddleGame;
	private final PositionalAnalysisForEndGame positionalAnalysisForEndGame = new PositionalAnalysisForEndGame(
			new PassedPawnFieldHeuristic(),
			centralControlHeuristic,
			new KingFieldHeuristic(),
			new PawnHeuristicsEndgame());
	private final PieceValueEvaluator pieceValueEvaluator;
	
	private Phase currentPhase;

	public void evaluate(Board board, List<Move> moves) {
		if (moves.isEmpty()) {
			return;
		}

		MovesEvaluator evaluator;
		if (isOpening(board)) {
			if (currentPhase != Phase.OPENING) {
				currentPhase = Phase.OPENING;
				logger.debug("Using positional analysis for opening");
			}
			evaluator = positionalAnalysisForOpening;
		} else if (isMiddleGame(board)) {
			if (currentPhase != Phase.MIDDLE_GAME) {
				readjusePieceValues(board);
				currentPhase = Phase.MIDDLE_GAME;
				logger.debug("Using positional analysis for middle game");
			}
			evaluator = positionalAnalysisForMiddleGame;
		} else {
			if (currentPhase != Phase.END_GAME) {
				readjusePieceValues(board);
				currentPhase = Phase.END_GAME;
				logger.debug("Using positional analysis for end game");
			}
			evaluator = positionalAnalysisForEndGame;
		}
		evaluator.evaluate(board, moves);
	}

	private boolean isOpening(Board board) {
		return board.lastMove().depthInTree() + 1 <= 15;
	}

	private boolean isMiddleGame(Board board) {
		for (Player player : Player.values()) {
			int valueForPieces = pieceValueEvaluator.getValueForPieces(board, player);
			logger.debug("value of " + player + " pieces: " + valueForPieces);
			if (valueForPieces > 16950) {
				return true;
			}
		}
		return false;
	}

	private void readjusePieceValues(Board board) {
		int whiteMatieral = pieceValueEvaluator.getValueForPieces(board, Player.WHITE);
		int blackMaterial = pieceValueEvaluator.getValueForPieces(board, Player.BLACK);
		if (board.currentPlayer() == Player.WHITE && whiteMatieral - blackMaterial >= 200) {
			pieceValueEvaluator.readjustWhitePieceValues(Math.max(0.6f, ((float) blackMaterial) / ((float) whiteMatieral)));
			logger.debug("Piece values have been readjusted to: " + pieceValueEvaluator);
		}
		if (board.currentPlayer() == Player.BLACK && blackMaterial - whiteMatieral >= 200) {
			pieceValueEvaluator.readjustBlackPieceValues(Math.max(0.6f, ((float) whiteMatieral) / ((float) blackMaterial)));
			logger.debug("Piece values have been readjusted to: " + pieceValueEvaluator);
		}
	}

}
