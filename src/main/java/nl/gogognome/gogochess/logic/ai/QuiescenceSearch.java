package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

/**
 * Implementation of quiescence search based on https://chessprogramming.wikispaces.com/Quiescence+Search
 */
public class QuiescenceSearch {

	private final BoardEvaluator boardEvaluator;
	private final Statistics statistics;
	private final KillerHeuristic killerHeuristic;
	private int margin = 200;

	public QuiescenceSearch(
			BoardEvaluator boardEvaluator, Statistics statistics,
			KillerHeuristic killerHeuristic) {
		this.boardEvaluator = boardEvaluator;
		this.statistics = statistics;
		this.killerHeuristic = killerHeuristic;
	}

	Move search(Board board, Move move, int alpha, int beta) {
		board.process(move);
		statistics.onPositionEvaluated();
		MoveValue value = boardEvaluator.value(board);
		move.setValue(value);

		Player playerForNextMove = move.getPlayer().opponent();
		if (playerForNextMove == WHITE) {
			if (value.getCombinedScore() - margin >= beta) {
				move.setValue(MoveValue.forWhite(beta, "Beta cut off"));
				if (killerHeuristic.markAsKiller(move)) {
					statistics.onCutOffByKillerMove();
				}
				return move;
			}
			if (alpha < value.getCombinedScore()) {
				alpha = value.getCombinedScore();
			}
		} else {
			if (value.getCombinedScore() + margin <= alpha) {
				move.setValue(forBlack(alpha, "Alpha cut off"));
				if (killerHeuristic.markAsKiller(move)) {
					statistics.onCutOffByKillerMove();
				}
				return move;
			}
			if (beta > value.getCombinedScore()) {
				beta = value.getCombinedScore();
			}
		}

		Move bestDeepestMove = move;
		List<Move> childMoves = board.currentPlayer().validCaptures(board);
		statistics.onPositionsGenerated(childMoves.size());
		killerHeuristic.putKillerMoveFirst(childMoves);
		for (Move childMove : childMoves) {
			statistics.onPositionEvaluated();
			Move deepestMove = search(board, childMove, alpha, beta);
			value = deepestMove.getValue();
			if (playerForNextMove == WHITE) {
				if (value.getCombinedScore() >= beta) {
					move.setValue(MoveValue.forWhite(beta, "Beta cut off"));
					if (killerHeuristic.markAsKiller(childMove)) {
						statistics.onCutOffByKillerMove();
					}
					return deepestMove;
				}
				if (value.getCombinedScore() > alpha) {
					alpha = value.getCombinedScore();
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			} else {
				if (value.getCombinedScore() <= alpha) {
					move.setValue(forBlack(-alpha, "Alpha cut off"));
					if (killerHeuristic.markAsKiller(childMove)) {
						statistics.onCutOffByKillerMove();
					}
					return deepestMove;
				}
				if (value.getCombinedScore() < beta) {
					beta = value.getCombinedScore();
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			}
		}

		return bestDeepestMove;
	}
}
