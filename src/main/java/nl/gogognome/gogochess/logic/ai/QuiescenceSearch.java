package nl.gogognome.gogochess.logic.ai;

import static java.util.stream.Collectors.*;
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
			if (value.getCombinedScore() >= beta) {
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
			if (value.getCombinedScore() <= alpha) {
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
		List<Move> childMoves = board.currentPlayer().validMoves(board);
		statistics.onPositionsGenerated(childMoves.size());
		childMoves = childMoves.stream()
				.filter(Move::isCapture)
				.collect(toList());
		killerHeuristic.putKillerMoveFirst(childMoves);
		for (Move childMove : childMoves) {
			if (!childMove.isCapture()) {
				continue;
			}

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
				if (alpha <= value.getCombinedScore()) {
					alpha = value.getCombinedScore();
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			} else {
				if (value.getCombinedScore() <= alpha) {
					move.setValue(forBlack(alpha, "Alpha cut off"));
					if (killerHeuristic.markAsKiller(childMove)) {
						statistics.onCutOffByKillerMove();
					}
					return deepestMove;
				}
				if (beta > value.getCombinedScore()) {
					beta = value.getCombinedScore();
					move.setValue(value);
					bestDeepestMove = deepestMove;
				}
			}
		}

		return bestDeepestMove;
	}
}
