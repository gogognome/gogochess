package nl.gogognome.gogochess.game.ai;

import java.util.*;
import nl.gogognome.gogochess.game.*;

public class ConfigurableLookAheadArtificialIntelligence {

	private final int maxRecursionLevel;
	private final Random random = new Random(System.currentTimeMillis());
	private final BoardEvaluator boardEvaluator = new CheckMateBoardEvaluator();

	public ConfigurableLookAheadArtificialIntelligence(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
	}

	public Move nextMove(Board board, Player player) {
		return nextMove(board, player, maxRecursionLevel);
	}

	public Move nextMove(Board board, Player player, int remainingRecursionLevel) {
		List<Move> moves = board.validMoves(player);
		sortMovesOnValue(moves, board);

		List<Move> bestMoves = new ArrayList<>();
		int bestValue = MoveValues.minValue(player);

		for (Move move : moves) {
			board.process(move);
			if (remainingRecursionLevel > 0) {
				Move nextMove = nextMove(board, player.other(), remainingRecursionLevel-1);
				if (nextMove != null) {
					move.setValue(MoveValues.reduce(nextMove.getValue(), 1));
				} else {
					move.setValue(boardEvaluator.value(board, move.getStatus()));
				}
			} else {
				move.setValue(boardEvaluator.value(board, move.getStatus()));
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
		}

		if (bestMoves.isEmpty()) {
			return null;
		}
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

	private void sortMovesOnValue(List<Move> moves, Board board) {
		for (Move move : moves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board, move.getStatus()));
		}

		moves.sort((m1, m2) -> MoveValues.compareTo(m2.getValue(), m1.getValue(), m1.getPlayer()));
	}

}
