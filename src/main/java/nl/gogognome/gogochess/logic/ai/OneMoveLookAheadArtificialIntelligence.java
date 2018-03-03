package nl.gogognome.gogochess.logic.ai;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public class OneMoveLookAheadArtificialIntelligence implements ArtificialIntelligence {

	private final Random random = new Random(System.currentTimeMillis());
	private final BoardEvaluator boardEvaluator = BoardEvaluatorFactory.newInstance();

	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer) {
		List<Move> moves = board.validMoves(player);

		List<Move> bestMoves = new ArrayList<>();
		int bestValue = player == WHITE ? MIN_VALUE : MAX_VALUE;

		Progress progress = new Progress(progressUpdateConsumer);
		Progress.Job job = progress.onStartJobWithNrSteps(moves.size());
		for (Move move : moves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board));
			int signum = compareTo(move.getValue(), bestValue, player);
			if (signum > 0) {
				bestValue = move.getValue();
				bestMoves.clear();
			}
			if (signum >= 0) {
				bestMoves.add(move);
			}
			job.onNextStep();
		}

		if (bestMoves.isEmpty()) {
			return null;
		}
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

	private int compareTo(int value1, int value2, Player player) {
		if (player == WHITE) {
			return Integer.compare(value1, value2);
		} else {
			return Integer.compare(value2, value1);
		}
	}
}
