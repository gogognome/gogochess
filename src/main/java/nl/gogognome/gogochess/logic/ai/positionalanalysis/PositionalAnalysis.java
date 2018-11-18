package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import java.util.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

/**
 * This class evaluates the opening positions. Based on James J.Gillogly, "The Technology Chess Program" (1972).
 */
public class PositionalAnalysis {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public PositionalAnalysis(PieceValueEvaluator pieceValueEvaluator) {
		this.pieceValueEvaluator = pieceValueEvaluator;
	}

	private enum Phase {
		OPENING,
		MIDDLE_GAME,
		END_GAME
	}
	
	private final CentralControlHeuristic centralControlHeuristic = new CentralControlHeuristic();
	private final PawnHeuristics pawnHeuristics = new PawnHeuristics();
	private final PositionalAnalysisForOpening positionalAnalysisForOpening = new PositionalAnalysisForOpening(centralControlHeuristic, pawnHeuristics);
	private final PositionalAnalysisForMiddleGame positionalAnalysisForMiddleGame = new PositionalAnalysisForMiddleGame(centralControlHeuristic, new KingField());
	private final PieceValueEvaluator pieceValueEvaluator;
	
	private Phase currentPhase;

	public void evaluate(Board board, List<Move> moves) {
		if (moves.isEmpty()) {
			return;
		}

		if (isOpening(board)) {
			if (currentPhase != Phase.OPENING) {
				currentPhase = Phase.OPENING;
				logger.debug("Using positional analysis for opening");
			}
			positionalAnalysisForOpening.evaluate(board, moves);
		} else if (isMiddleGame(board)) {
			if (currentPhase != Phase.MIDDLE_GAME) {
				readjusePieceValues(board);
				currentPhase = Phase.MIDDLE_GAME;
				logger.debug("Using positional analysis for middle game");
			}
			positionalAnalysisForMiddleGame.evaluate(board, moves);
		} else {
			if (currentPhase != Phase.END_GAME) {
				readjusePieceValues(board);
				currentPhase = Phase.END_GAME;
				logger.debug("Using positional analysis for end game");
			}
			positionalAnalysisForMiddleGame.evaluate(board, moves);
		}
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
