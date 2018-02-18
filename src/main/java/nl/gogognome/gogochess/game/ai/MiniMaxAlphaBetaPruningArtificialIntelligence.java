package nl.gogognome.gogochess.game.ai;

import java.util.*;
import nl.gogognome.gogochess.game.*;

public class MiniMaxAlphaBetaPruningArtificialIntelligence implements ArtificialIntelligence {

	private final int maxDepth;
	private final int pruneFactor;

	// TODO: introduce DI framework
	private final BoardEvaluator boardEvaluator = ComplexBoardEvaluator.newInstance();
	private final MiniMax miniMax = new MiniMax();
	private final MoveSort moveSort = new MoveSort();
	private final Random random = new Random();

	public MiniMaxAlphaBetaPruningArtificialIntelligence(int maxDepth, int pruneFactor) {
		this.maxDepth = maxDepth;
		this.pruneFactor = pruneFactor;
	}

	@Override
	public Move nextMove(Board board, Player player) {
		List<Move> moves = board.validMoves(player);
		evaluateMoves(board, moves);
		moveSort.sort(moves);
		miniMaxAlphaBetaPruning(board, moves, board.lastMove(), 1);
		moveSort.sort(moves);
		return moves.get(0);
	}

	private void miniMaxAlphaBetaPruning(Board board, List<Move> movesToInvestigate, Move lastMove, int depth) {
		if (depth < maxDepth) {
			for (Move move  : movesToInvestigate) {
				board.process(move);
				List<Move> followingMoves = board.validMoves(move.getPlayer().other());
				evaluateMoves(board, followingMoves);
				moveSort.sort(followingMoves);

				if (!followingMoves.isEmpty()) {
					int currentPruneFactor = pruneFactor;
					Player player = followingMoves.get(0).getPlayer();
					int value = followingMoves.get(0).getValue();
					int lastIndex = 1;
					while (lastIndex < followingMoves.size() && Math.abs(followingMoves.get(lastIndex).getValue() - MoveValues.negateForBlack(value, player)) <= currentPruneFactor) {
						lastIndex++;
						currentPruneFactor = Math.max(currentPruneFactor-1, 0);
					}
					miniMaxAlphaBetaPruning(board, followingMoves.subList(0, lastIndex), lastMove, depth + 1);
				}
			}
		} else {
			for (Move move : movesToInvestigate) {
				miniMax.updateValuesInPrecedingMoves(move, lastMove);
			}
		}
	}

	private void evaluateMoves(Board board, List<Move> followingMoves) {
		for (Move followingMove : followingMoves) {
			board.process(followingMove);
			followingMove.setValue(boardEvaluator.value(board));
		}
	}

}
