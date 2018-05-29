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

	private final CentralControlHeuristic centralControlHeuristic = new CentralControlHeuristic();
	private final PawnHeuristics pawnHeuristics = new PawnHeuristics();
	private final PositionalAnalysisForOpening positionalAnalysisForOpening = new PositionalAnalysisForOpening(centralControlHeuristic, pawnHeuristics);
	private final PositionalAnalysisForMiddleGame positionalAnalysisForMiddleGame = new PositionalAnalysisForMiddleGame(centralControlHeuristic, new KingField());
	private final PieceValueEvaluator pieceValueEvaluator = new PieceValueEvaluator();

	public void evaluate(Board board, List<Move> moves) {
		if (moves.isEmpty()) {
			return;
		}

		if (isOpening(board)) {
			logger.debug("Using positional analysis for opening");
			positionalAnalysisForOpening.evaluate(board, moves);
		} else if (isMiddleGame(board)) {
			logger.debug("Using positional analysis for middle game");
			positionalAnalysisForMiddleGame.evaluate(board, moves);
		} else {
			logger.debug("Using positional analysis for end game");
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

}
