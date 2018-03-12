package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public class MiniMaxAlphaBetaArtificialIntelligence implements ArtificialIntelligence {

	private final int initialMaxDepth;
	private final int initialAlpha;
	private final int initialBeta;
	private int maxDepth;

	private AtomicBoolean canceled = new AtomicBoolean();

	// TODO: introduce DI framework
	private final BoardEvaluator boardEvaluator = BoardEvaluatorFactory.newInstance();
	private final MoveSort moveSort = new MoveSort();

	public MiniMaxAlphaBetaArtificialIntelligence(int maxDepth) {
		this(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public MiniMaxAlphaBetaArtificialIntelligence(int maxDepth, int initialAlpha, int initialBeta) {
		this.initialMaxDepth = maxDepth;
		this.initialAlpha = initialAlpha;
		this.initialBeta = initialBeta;
	}

	@Override
	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer, Consumer<List<Move>> bestMovesConsumer) {
		initMaxDepth(board);
		System.out.println("maxDepth: " + maxDepth);
		List<Move> nextMoves = board.validMoves();
		Progress progress = new Progress(progressUpdateConsumer);
		Progress.Job job = progress.onStartJobWithNrSteps(nextMoves.size());
		Map<Move, Move> moveToBestDeepestMove = new HashMap<>();
		for (Move move : nextMoves) {
			Move bestDeepestMove = alphaBeta(board, move, 0, initialAlpha, initialBeta, progress);
			moveToBestDeepestMove.put(move, bestDeepestMove);
			job.onNextStep();
		}

		moveSort.sort(nextMoves);
		Move nextMove = nextMoves.get(0);
		bestMovesConsumer.accept(nextMove.pathTo(moveToBestDeepestMove.get(nextMove)));
		System.out.println(nextMove.getDescription() + ", value: " + nextMove.getValue());
		return nextMove;
	}

	private Move alphaBeta(Board board, Move move, int depth, int alpha, int beta, Progress progress) {
		if (depth == maxDepth || move.getStatus().isGameOver()) {
			evaluateMove(board, move);
//			System.out.println(move);
			return move;
		}

		List<Move> childMoves = getChildMoves(board, move);
		if (childMoves.isEmpty()) {
			evaluateMove(board, move);
//			System.out.println(move);
			return move;
		}

//		System.out.print(move + "\t");
		return alphaBetaWithChildMoves(board, move, depth, alpha, beta, progress, childMoves);
	}

	private void initMaxDepth(Board board) {
		maxDepth = initialMaxDepth;
		double numberNonPawnPieces = board.numberNonPawnPieces();
		if (numberNonPawnPieces <= 8) {
			maxDepth++;
		}
		if (numberNonPawnPieces <= 6) {
			maxDepth++;
		}
		if (numberNonPawnPieces <= 4) {
			maxDepth += 2;
		}
	}

	private Move alphaBetaWithChildMoves(Board board, Move move, int depth, int alpha, int beta, Progress progress, List<Move> childMoves) {
		if (canceled.get()) {
			throw new ArtificalIntelligenceCanceledException();
		}

		Progress.Job job = null;
		if (depth <= 1) {
			job = progress.onStartJobWithNrSteps(childMoves.size());
		}

		Move bestDeepestMove = null;
		if (childMoves.get(0).getPlayer() == Player.WHITE) {
			int value = Integer.MIN_VALUE;
			for (Move childMove  : childMoves) {
				Move deepestChildMove = alphaBeta(board, childMove, depth + 1, alpha, beta, progress);
				int bestMoveValue = MoveValues.reduce(childMove.getValue(), 1);
				if (bestMoveValue > value) {
					value = bestMoveValue;
					bestDeepestMove = deepestChildMove;
				}
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
				Move deepestChildMove = alphaBeta(board, childMove, depth + 1, alpha, beta, progress);
				int bestMoveValue = MoveValues.reduce(childMove.getValue(), 1);
				if (bestMoveValue < value) {
					value = bestMoveValue;
					bestDeepestMove = deepestChildMove;
				}
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
		return bestDeepestMove;
	}

	private List<Move> getChildMoves(Board board, Move move) {
		board.process(move);
		List<Move> childMoves = board.validMoves();

		evaluateMoves(board, childMoves);
		moveSort.sort(childMoves);

		return childMoves;
	}

	private void evaluateMoves(Board board, List<Move> moves) {
		for (Move move : moves) {
			evaluateMove(board, move);
		}
	}

	private void evaluateMove(Board board, Move move) {
		board.process(move);
		move.setValue(boardEvaluator.value(board));
	}

	public void cancel() {
		canceled.set(true);
	}

	private static class Result {
		private final Move bestMove;
		private final Move deepestBestMove;

		public Result(Move bestMove, Move deepestBestMove) {
			this.bestMove = bestMove;
			this.deepestBestMove = deepestBestMove;
		}

		public Move getBestMove() {
			return bestMove;
		}

		public Move getDeepestBestMove() {
			return deepestBestMove;
		}
	}
}
