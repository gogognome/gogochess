package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class QuiescenceSearch {

	private final BoardEvaluator boardEvaluator;
	private final Statistics statistics;

	public QuiescenceSearch(BoardEvaluator boardEvaluator, Statistics statistics) {
		this.boardEvaluator = boardEvaluator;
		this.statistics = statistics;
	}

	public Move search(Board board, Move move, int alpha, int beta) {
		board.process(move);
		statistics.onPositionEvaluated();
		int value = boardEvaluator.value(board);
		move.setValue(value);

		Player playerForNextMove = move.getPlayer().other();
		if (playerForNextMove == WHITE) {
			if (value >= beta) {
				move.setValue(beta);
				return move;
			}
			if (alpha < value) {
				alpha = value;
			}
		} else {
			if (value <= alpha) {
				move.setValue(alpha);
				return move;
			}
			if (beta > value) {
				beta = value;
			}
		}

		Move bestDeepestMove = move;
		List<Move> childMoves = board.validMoves();
		statistics.onPositionsGenerated(childMoves.size());
		for (Move childMove : childMoves) {
			if (!childMove.isCapture()) {
				continue;
			}

			statistics.onPositionEvaluated();
			Move deepestMove = search(board, childMove, alpha, beta);
			value = deepestMove.getValue();
			if (playerForNextMove == WHITE) {
				if (value >= beta) {
					move.setValue(beta);
					return deepestMove;
				}
				if (alpha < value) {
					alpha = value;
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			} else {
				if (value <= alpha) {
					move.setValue(alpha);
					return deepestMove;
				}
				if (beta > value) {
					beta = value;
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			}
		}

		return bestDeepestMove;
	}
}
