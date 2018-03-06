package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public class MiniMaxAlphaBetaArtificialIntelligence implements ArtificialIntelligence {

	private final int maxDepth;
	private final int initialAlpha;
	private final int initialBeta;

	// TODO: introduce DI framework
	private final BoardEvaluator boardEvaluator = BoardEvaluatorFactory.newInstance();
	private final MoveSort moveSort = new MoveSort();

	public MiniMaxAlphaBetaArtificialIntelligence(int maxDepth) {
		this(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public MiniMaxAlphaBetaArtificialIntelligence(int maxDepth, int initialAlpha, int initialBeta) {
		this.maxDepth = maxDepth;
		this.initialAlpha = initialAlpha;
		this.initialBeta = initialBeta;
	}

	@Override
	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer) {
		Move lastMove = board.lastMove();
		alphaBeta(board, board.lastMove(), 1, initialAlpha, initialBeta, new Progress(progressUpdateConsumer));

		List<Move> moves = lastMove.getFollowingMoves();
		moveSort.sort(moves);
		return moves.get(0);
	}

	private int alphaBeta(Board board, Move move, int depth, int alpha, int beta, Progress progress) {
		Progress.Job job = null;
		if (depth == maxDepth || move.getStatus().isGameOver()) {
			return evaluateMove(board, move);
		}

		List<Move> childMoves = move.getFollowingMoves();
		if (childMoves == null) {
			board.process(move);
			childMoves = board.validMoves();
		}
		if (childMoves.isEmpty()) {
			return evaluateMove(board, move);
		}
		if (depth <= 2) {
			job = progress.onStartJobWithNrSteps(childMoves.size());
		}
		evaluateMoves(board, childMoves);
		moveSort.sort(childMoves);
		if (childMoves.get(0).getPlayer() == Player.WHITE) {
			int value = Integer.MIN_VALUE;
			for (Move childMove  : childMoves) {
				value = max(value, MoveValues.reduce(alphaBeta(board, childMove, depth+1, alpha, beta, progress), 1));
				alpha = max(alpha, value);

				if (job != null) {
					job.onNextStep();
				}
				if (beta <= alpha) {
					break; // beta cut-off
				}
			}
			move.setValue(value);
		} else {
			int value = Integer.MAX_VALUE;
			for (Move childMove  : childMoves) {
				value = min(value, MoveValues.reduce(alphaBeta(board, childMove, depth+1, alpha, beta, progress), 1));
				beta = min(beta, value);

				if (job != null) {
					job.onNextStep();
				}

				if (beta <= alpha) {
					break; // alpha cut-off
				}
			}
			move.setValue(value);
		}

		if (depth >= 5) {
			StringBuilder sb = new StringBuilder(40);
			Move currentMove = move;
			for (int i=1; i<depth; i++) {
				sb.insert(0, currentMove.getDescription() + " ");
				currentMove = currentMove.getPrecedingMove();
			}
		}
		return move.getValue();
	}

	private void evaluateMoves(Board board, List<Move> moves) {
		for (Move move : moves) {
			evaluateMove(board, move);
		}
	}

	private int evaluateMove(Board board, Move move) {
		board.process(move);
		move.setValue(boardEvaluator.value(board));
		return move.getValue();
	}

}
