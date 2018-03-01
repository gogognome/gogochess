package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class MiniMaxAlphaBetaPruningArtificialIntelligence implements ArtificialIntelligence {

	private final int maxDepth;
	private final int minPruneDepth;
	private final int pruneFactor;

	// TODO: introduce DI framework
	private final BoardEvaluator boardEvaluator = ComplexBoardEvaluator.newInstance();
	private final MiniMax miniMax = new MiniMax();
	private final MoveSort moveSort = new MoveSort();

	public MiniMaxAlphaBetaPruningArtificialIntelligence(int maxDepth, int minPruneDepth, int pruneFactor) {
		this.maxDepth = maxDepth;
		this.minPruneDepth = minPruneDepth;
		this.pruneFactor = pruneFactor;
	}

	@Override
	public Move nextMove(Board board, Player player, ProgressListener progressListener) {
		List<Move> moves = board.validMoves(player);
		evaluateMoves(board, moves);
		moveSort.sort(moves);
		miniMaxAlphaBetaPruning(board, moves, board.lastMove(), 1, progressListener);
		moveSort.sort(moves);
		return moves.get(0);
	}

	private void miniMaxAlphaBetaPruning(Board board, List<Move> movesToInvestigate, Move lastMove, int depth, ProgressListener progressListener) {
		if (depth <= 2) {
			progressListener.setNrSteps(depth - 1, movesToInvestigate.size());
		}
		if (depth < maxDepth) {
			for (Move move  : movesToInvestigate) {
				board.process(move);
				List<Move> followingMoves = board.validMoves(move.getPlayer().other());
				evaluateMoves(board, followingMoves);
				moveSort.sort(followingMoves);

				if (!followingMoves.isEmpty()) {
					List<Move> prunedFollowingMoves = depth >= minPruneDepth ? prune(followingMoves) : followingMoves;
					miniMaxAlphaBetaPruning(board, prunedFollowingMoves, lastMove, depth + 1, progressListener);
				}
				if (depth <= 2) {
					progressListener.nextStep(depth - 1);
				}
			}
		} else {
			for (Move move : movesToInvestigate) {
				miniMax.updateValuesInPrecedingMoves(move, lastMove);
			}
		}
	}

	private List<Move> prune(List<Move> moves) {
		List<Move> prunedFollowingMoves;
		int currentPruneFactor = pruneFactor;
		Player player = moves.get(0).getPlayer();
		int value = moves.get(0).getValue();
		int lastIndex = 1;
		while (lastIndex < moves.size() && Math.abs(moves.get(lastIndex).getValue() - MoveValues.negateForBlack(value, player)) <= currentPruneFactor) {
			lastIndex++;
			currentPruneFactor = Math.max(currentPruneFactor - 1, 0);
		}
		prunedFollowingMoves = moves.subList(0, lastIndex);
		return prunedFollowingMoves;
	}

	private void evaluateMoves(Board board, List<Move> followingMoves) {
		for (Move followingMove : followingMoves) {
			board.process(followingMove);
			followingMove.setValue(boardEvaluator.value(board));
		}
	}

}
