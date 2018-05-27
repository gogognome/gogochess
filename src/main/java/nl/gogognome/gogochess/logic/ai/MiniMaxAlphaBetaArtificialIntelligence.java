package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import javax.inject.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.positionalanalysis.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class MiniMaxAlphaBetaArtificialIntelligence implements ArtificialIntelligence {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int initialMaxDepth;
	private int initialAlpha;
	private int initialBeta;
	private int maxDepth;

	private AtomicBoolean canceled = new AtomicBoolean();

	private final BoardEvaluator boardEvaluator;
	private final PositionalAnalysis positonalAnalysis;
	private final MoveSort moveSort;
	private final QuiescenceSearch quiescenceSearch;
	private final Statistics statistics;

	@Inject
	public MiniMaxAlphaBetaArtificialIntelligence(BoardEvaluator boardEvaluator, PositionalAnalysis positionalAnalysis, MoveSort moveSort,
			QuiescenceSearch quiescenceSearch, Statistics statistics) {
		this.boardEvaluator = boardEvaluator;
		this.positonalAnalysis = positionalAnalysis;
		this.moveSort = moveSort;
		this.quiescenceSearch = quiescenceSearch;
		this.statistics = statistics;
		this.initialMaxDepth = 3;
		this.initialAlpha = Integer.MIN_VALUE;
		this.initialBeta = Integer.MAX_VALUE;
	}

	public void setInitialMaxDepth(int initialMaxDepth) {
		this.initialMaxDepth = initialMaxDepth;
	}

	public void setInitialAlpha(int initialAlpha) {
		this.initialAlpha = initialAlpha;
	}

	public void setInitialBeta(int initialBeta) {
		this.initialBeta = initialBeta;
	}

	@Override
	public Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer, Consumer<List<Move>> bestMovesConsumer) {
		initMaxDepth(board);
		statistics.reset();
		long startTime = System.nanoTime();
		logger.debug("maxDepth: " + maxDepth);
		List<Move> nextMoves = board.validMoves();
		positonalAnalysis.evaluate(board, nextMoves);
		moveSort.sort(nextMoves);
		ReverseAlgebraicNotation notation = new ReverseAlgebraicNotation();
		nextMoves.stream().forEach(m -> logger.debug(notation.format(m) + "\t" + m.getValue()));

		statistics.onPositionsGenerated(nextMoves.size());
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
		long endTime = System.nanoTime();
		double durationMillis = (endTime - startTime) / 1000000000.0;
		logger.debug("evaluating " + statistics.getNrPositionsEvaluated()+ " positions took " + durationMillis + " s (" + (statistics.getNrPositionsEvaluated() / (durationMillis)) + " positions/s");
		logger.debug("generating " + statistics.getNrPositionsGenerated() + " positions took " + durationMillis + " s (" + (statistics.getNrPositionsGenerated() / (durationMillis)) + " positions/s");
		return nextMove;
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

	private Move alphaBeta(Board board, Move move, int depth, int alpha, int beta, Progress progress) {
		if (move.getStatus().isGameOver()) {
			evaluateMove(board, move);
			statistics.onPositionEvaluated();
			return move;
		}
		if (depth >= maxDepth) {
			return quiescenceSearch.search(board, move, alpha, beta);
		}

		List<Move> childMoves = getChildMoves(board, move);
		statistics.onPositionsGenerated(childMoves.size());
		if (childMoves.isEmpty()) {
			evaluateMove(board, move);
			statistics.onPositionEvaluated();
			return move;
		}

		return alphaBetaWithChildMoves(board, move, depth, alpha, beta, progress, childMoves);
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
				int childMoveValue = MoveValues.reduce(childMove.getValue(), 1);
				if (childMoveValue > value) {
					value = childMoveValue;
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
				int childMoveValue = MoveValues.reduce(childMove.getValue(), 1);
				if (childMoveValue < value) {
					value = childMoveValue;
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

}
