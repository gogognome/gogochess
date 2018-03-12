package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public class ConfigurableLookAheadArtificialIntelligence implements ArtificialIntelligence {

	private final int maxRecursionLevel;
	private final Random random = new Random(System.currentTimeMillis());
	private final BoardEvaluator boardEvaluator = BoardEvaluatorFactory.newInstance();
	private final AtomicBoolean canceled = new AtomicBoolean();

	public ConfigurableLookAheadArtificialIntelligence(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
	}

	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer, Consumer<List<Move>> bestMovesConsumer) {
		Result result = nextMove(board, player, 0, new Progress(progressUpdateConsumer));
		bestMovesConsumer.accept(result.nextMove.pathTo(result.bestDescendentMove));
		return result.nextMove;
	}

	private Result nextMove(Board board, Player player, int currentLevel, Progress progress) {
		if (canceled.get()) {
			throw new ArtificalIntelligenceCanceledException();
		}

		List<Move> moves = board.validMoves();
		List<Result> bestMoves = new ArrayList<>();
		int bestValue = MoveValues.minValue(player);

		Progress.Job job = null;
		if (currentLevel < 2) {
			job = progress.onStartJobWithNrSteps(moves.size());
		}
		for (Move move : moves) {
			board.process(move);
			Result result;
			if (currentLevel < maxRecursionLevel) {
				result = nextMove(board, player.other(), currentLevel+1, progress);
				if (result.nextMove != null) {
					move.setValue(MoveValues.reduce(result.nextMove.getValue(), 1));
					result = new Result(move, result.bestDescendentMove);
				} else {
					move.setValue(boardEvaluator.value(board));
					result = new Result(move, move);
				}
			} else {
				move.setValue(boardEvaluator.value(board));
				result = new Result(move, move);
			}
			int signum = MoveValues.compareTo(move.getValue(), bestValue, player);
			if (signum > 0) {
				bestValue = move.getValue();
				bestMoves.clear();
			}
			if (signum >= 0) {
				bestMoves.add(result);
			}

			if (bestValue == MoveValues.maxValue(player)) {
				break;
			}
			if (job != null) {
				job.onNextStep();
			}
		}

		if (bestMoves.isEmpty()) {
			return new Result(null, null);
		}
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

	@Override
	public void cancel() {
		canceled.set(true);
	}

	private static class Result {
		private final Move nextMove;
		private final Move bestDescendentMove;

		Result(Move nextMove, Move bestDescendentMove) {
			this.nextMove = nextMove;
			this.bestDescendentMove = bestDescendentMove;
		}
	}
}
