package nl.gogognome.gogochess.logic.ai;

import static java.lang.Math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;
import javax.inject.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.positionalanalysis.*;

public class MiniMaxAlphaBetaArtificialIntelligence implements ArtificialIntelligence, RecursiveSearchAI {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int initialMaxDepth;
	private int initialAlpha;
	private int initialBeta;
	private int maxDepth;
	private AtomicInteger maxDepthDelta;
	private int razorMargin = 200;

	private AtomicBoolean canceled = new AtomicBoolean();

	private final BoardEvaluator boardEvaluator;
	private final PositionalAnalysis positonalAnalysis;
	private final MoveSort moveSort;
	private final Statistics statistics;
	private final KillerHeuristic killerHeuristic;
	private final QuiescenceSearch quiescenceSearch;
	private final TranspositionTable transpositionTable = new TranspositionTable();

	@Inject
	public MiniMaxAlphaBetaArtificialIntelligence(
			BoardEvaluator boardEvaluator,
			PositionalAnalysis positionalAnalysis,
			MoveSort moveSort,
			Statistics statistics,
			QuiescenceSearch quiescenceSearch, KillerHeuristic killerHeuristic) {
		this.boardEvaluator = boardEvaluator;
		this.positonalAnalysis = positionalAnalysis;
		this.moveSort = moveSort;
		this.statistics = statistics;
		this.quiescenceSearch = quiescenceSearch;
		this.killerHeuristic = killerHeuristic;
		this.initialMaxDepth = 3;
		this.initialAlpha = Integer.MIN_VALUE;
		this.initialBeta = Integer.MAX_VALUE;
	}

	public void setMaxDepth(int initialMaxDepth) {
		this.initialMaxDepth = initialMaxDepth;
	}

	@Override
	public Move nextMove(Board board, Player player, ProgressListener progressListener) {
		canceled.set(false);
		maxDepth = initialMaxDepth;
		maxDepthDelta = progressListener.getMaxDepthDelta();
		statistics.reset();
		transpositionTable.clear();

		long startTime = System.nanoTime();
		logger.debug("maxDepth: " + maxDepth);
		List<Move> nextMoves = board.currentPlayer().validMoves(board);
		positonalAnalysis.evaluate(board, nextMoves);
		moveSort.sort(nextMoves);
		Map<Move, MoveValue> positionalValues = nextMoves.stream().collect(Collectors.toMap(m -> m, Move::getValue));

		statistics.onPositionsGenerated(nextMoves.size());
		Progress progress = new Progress(progressListener.getProgressUpdateConsumer());
		Progress.Job job = progress.onStartJobWithNrSteps(nextMoves.size());
		Map<Move, Move> moveToBestDeepestMove = new HashMap<>();
		for (Move move : nextMoves) {
			Move bestDeepestMove = alphaBeta(board, move, 0, initialAlpha, initialBeta, progress);
			moveToBestDeepestMove.put(move, bestDeepestMove);
			job.onNextStep();
		}

		nextMoves.forEach(m -> m.setValue(m.getValue().add(positionalValues.get(m), "positional value")));
		moveSort.sort(nextMoves);
		Move nextMove = nextMoves.get(0);
		progressListener.consumeBestMoves(nextMove.pathTo(moveToBestDeepestMove.get(nextMove)));
		long endTime = System.nanoTime();
		logStatistics(startTime, endTime);
		return nextMove;
	}

	private void logStatistics(long startTime, long endTime) {
		double durationMillis = (endTime - startTime) / 1000000000.0;
		logger.debug("evaluating " + statistics.getNrPositionsEvaluated()+ " positions took " + durationMillis + " s (" + (statistics.getNrPositionsEvaluated() / (durationMillis)) + " positions/s");
		logger.debug("generating " + statistics.getNrPositionsGenerated() + " positions took " + durationMillis + " s (" + (statistics.getNrPositionsGenerated() / (durationMillis)) + " positions/s");
		logger.debug("nr cut offs caused by killer heuristic: " + statistics.getNrCutOffsByKillerMove());
		logger.debug("nr cache hits: " + statistics.getNrCacheHits());
	}

	private Move alphaBeta(Board board, Move move, int depth, int alpha, int beta, Progress progress) {
		board.process(move);
		long hash = board.getBoardHash();
		TranspositionTable.BoardPosition cachedBoardPosition = transpositionTable.getCachedBoardPosition(hash, alpha, beta, move.depthInTree());
		if (cachedBoardPosition != null) {
			statistics.onCacheHit();
			move.setValue(cachedBoardPosition.getValue());
			return cachedBoardPosition.getBestDeepestMove();
		}

		if (depth >= maxDepth + maxDepthDelta.get()) {
			Move bestDeepestMove = quiescenceSearch.search(board, move, alpha, beta);
			storeBestDeepestMoveInCache(move, hash, alpha, beta, bestDeepestMove);
			return bestDeepestMove;
		}

		List<Move> childMoves = getChildMoves(board, move);
		statistics.onPositionsGenerated(childMoves.size());
		if (childMoves.isEmpty()) {
			evaluateMove(board, move);
			storeBestDeepestMoveInCache(move, hash, alpha, beta, move);
			return move;
		}

		killerHeuristic.putKillerMoveFirst(childMoves);
		Move bestDeepestMove = alphaBetaWithChildMoves(board, move, depth, alpha, beta, progress, childMoves);
		storeBestDeepestMoveInCache(move, hash, alpha, beta, bestDeepestMove);
		return bestDeepestMove;
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
			MoveValue value = MoveValue.WHITE_MIN_VALUE;
			for (Move childMove  : childMoves) {
				Move deepestChildMove = alphaBeta(board, childMove, depth + 1, alpha, beta, progress);
				MoveValue childMoveValue = childMove.getValue();
				if (childMoveValue.isGreaterThan(value)) {
					value = childMoveValue;
					bestDeepestMove = deepestChildMove;
				}
				alpha = max(alpha, value.getCombinedScore());

				if (job != null) {
					job.onNextStep();
				}
				if (beta <= alpha) {
					if (killerHeuristic.markAsKiller(childMove)) {
						statistics.onCutOffByKillerMove();
					}
					break; // beta cut-off
				}
			}
			move.setValue(value);
		} else {
			MoveValue value = MoveValue.BLACK_MIN_VALUE;
			for (Move childMove  : childMoves) {
				Move deepestChildMove = alphaBeta(board, childMove, depth + 1, alpha, beta, progress);
				MoveValue childMoveValue = childMove.getValue();
				if (childMoveValue.isLessThan(value)) {
					value = childMoveValue;
					bestDeepestMove = deepestChildMove;
				}
				beta = min(beta, value.getCombinedScore());

				if (job != null) {
					job.onNextStep();
				}

				if (beta <= alpha) {
					if (killerHeuristic.markAsKiller(childMove)) {
						statistics.onCutOffByKillerMove();
					}
					break; // alpha cut-off
				}
			}
			move.setValue(value);
		}

		return bestDeepestMove;
	}

	private void storeBestDeepestMoveInCache(Move move, long hash, int alpha, int beta, Move bestDeepestMove) {
		transpositionTable.store(hash, alpha, beta, move.getValue(), move.depthInTree(), bestDeepestMove);
	}

	private List<Move> getChildMoves(Board board, Move move) {
		board.process(move);
		Player player = board.currentPlayer();
		List<Move> childMoves = player.validMoves(board);

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
		statistics.onPositionEvaluated();
	}

	public void cancel() {
		canceled.set(true);
	}

}
