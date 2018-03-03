package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public class ConfigurableLookAheadArtificialIntelligence implements ArtificialIntelligence {

	private final int maxRecursionLevel;
	private final Random random = new Random(System.currentTimeMillis());
	private final BoardEvaluator boardEvaluator = BoardEvaluatorFactory.newInstance();

	public ConfigurableLookAheadArtificialIntelligence(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
	}

	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer) {
		return nextMove(board, player, 0, new Progress(progressUpdateConsumer));
	}

	private Move nextMove(Board board, Player player, int currentLevel, Progress progress) {
		List<Move> moves = board.validMoves(player);
		List<Move> bestMoves = new ArrayList<>();
		int bestValue = MoveValues.minValue(player);

		Progress.Job job = null;
		if (currentLevel < 2) {
			job = progress.onStartJobWithNrSteps(moves.size());
		}
		for (Move move : moves) {
			board.process(move);
			if (currentLevel < maxRecursionLevel) {
				Move nextMove = nextMove(board, player.other(), currentLevel+1, progress);
				if (nextMove != null) {
					move.setValue(MoveValues.reduce(nextMove.getValue(), 1));
				} else {
					move.setValue(boardEvaluator.value(board));
				}
			} else {
				move.setValue(boardEvaluator.value(board));
			}
			int signum = MoveValues.compareTo(move.getValue(), bestValue, player);
			if (signum > 0) {
				bestValue = move.getValue();
				bestMoves.clear();
			}
			if (signum >= 0) {
				bestMoves.add(move);
			}

			if (bestValue == MoveValues.maxValue(player)) {
				break;
			}
			if (job != null) {
				job.onNextStep();
			}
		}

		if (bestMoves.isEmpty()) {
			return null;
		}
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

}
