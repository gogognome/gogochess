package nl.gogognome.gogochess.logic.ai;

import static java.util.stream.Collectors.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class AdaptiveLookAheadArtificialIntelligence implements ArtificialIntelligence {

	private final Random random = new Random(System.currentTimeMillis());
	// TODO: use DI framework
	private final BoardEvaluator boardEvaluator = ComplexBoardEvaluator.newInstance();
	private final MoveSort moveSort = new MoveSort();

	private final int maxThinkTimeMs;
	private final int maxNrMoves;
	private int nextMoveDepthInTree;

	/** Key is a node. Value is a list of leave nodes of the subtree with the key node as root. */
	private final Map<Move, List<Move>> nextMoveToLeaveMoves = new HashMap<>();

	public AdaptiveLookAheadArtificialIntelligence(int maxThinkTimeMs, int maxNrMoves) {
		this.maxThinkTimeMs = maxThinkTimeMs;
		this.maxNrMoves = maxNrMoves;
	}

	public Move nextMove(Board board, Player player) {
		long endTime = System.currentTimeMillis() + maxThinkTimeMs;
		List<Move> moves = board.validMoves(player);
		nextMoveDepthInTree = moves.get(0).depthInTree();
		checkInvariant(moves);
		sortMovesOnValue(moves, board);

		fillNextMoveToLeaveMoves(moves);

		while (System.currentTimeMillis() < endTime && nrLeaveMoves() < maxNrMoves) {
			Set<Integer> indicesToFurtherInvestigate = findIndicesToFurtherInvestigate(moves);
			List<Move> movesToFurtherInvestigate = indicesToFurtherInvestigate.stream()
					.map(moves::get)
					.collect(toList());

			for (Move moveToFurtherInvestigate : movesToFurtherInvestigate) {
				List<Move> leaveMoves = nextMoveToLeaveMoves.get(moveToFurtherInvestigate);
				if (!leaveMoves.isEmpty()) {
					sortMovesOnTreeDepthAscendingly(leaveMoves);
					int index = random.nextInt(leaveMoves.size() > 10 ? leaveMoves.size() / 10 : leaveMoves.size());
					Move leaveMoveToBeFurtherInvestigated = leaveMoves.remove(index);

					replaceLeaveMoveWithDeeperLeaveMove(board, leaveMoves, leaveMoveToBeFurtherInvestigated);
					updateValuesInPrecedingMoves(leaveMoveToBeFurtherInvestigated);
					checkInvariant(moves);
				}
			}

			sortMovesOnValue(moves, board);
			if (cannotImprove(moves.get(0))) {
				break;
			}
		}

		logMoves(moves);
		int index = 1;
		while (index < moves.size() && moves.get(index).getValue() == moves.get(0).getValue()) {
			index++;
		}
		return moves.get(random.nextInt(index));
	}

	private void checkInvariant(List<Move> moves) {
		for (Move move : moves) {
			if (move.getFollowingMoves() == null) {
				continue;
			}
			if (move.getFollowingMoves().stream().noneMatch(fm -> fm.getValue() == move.getValue())) {
				if (move.getFollowingMoves().stream().anyMatch(fm -> fm.getValue() != 0 && fm.getValue() != Integer.MIN_VALUE && fm.getValue() != Integer.MAX_VALUE )) {
					throw new IllegalStateException("move " + System.identityHashCode(move) + " " +  move.getDescription() + " (" + move.getValue() + ") at depth " + move.depthInTree() + " has following moves with different values: " +
							move.getFollowingMoves().stream().map(fm -> fm.getDescription() + " (" + fm.getValue() + ")").collect(toList()));
				}
			}
			checkInvariant(move.getFollowingMoves());
		}
	}

	private void replaceLeaveMoveWithDeeperLeaveMove(Board board, List<Move> leaveMoves, Move leaveMoveToBeFurtherInvestigated) {
		board.process(leaveMoveToBeFurtherInvestigated);
		Player investigatePlayer = leaveMoveToBeFurtherInvestigated.getPlayer().other();
		List<Move> validMoves = board.validMoves(investigatePlayer);

		for (Move move : validMoves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board));
			updateValuesInPrecedingMoves(move);
			leaveMoves.add(move);
		}
	}

	private void updateValuesInPrecedingMoves(Move move) {
		do {
			Player player = move.getPlayer().other();
			int bestValue = MoveValues.minValue(player);
			for (Move followingMove : move.getFollowingMoves()) {
				if (MoveValues.compareTo(followingMove.getValue(), bestValue, player) > 0) {
					bestValue = followingMove.getValue();
				}
			}
			if (move.getValue() != bestValue) {
//				System.out.println("changing value of " + System.identityHashCode(move) + " " + move.getDescription() + " from " + move.getValue() + " to " + bestValue +  " (level " + move.depthInTree() + ")");
			}
			move.setValue(bestValue);
			move = move.getPrecedingMove();
		} while (nextMoveDepthInTree <= move.depthInTree());
	}

	private int nrLeaveMoves() {
		return nextMoveToLeaveMoves.values().stream()
				.mapToInt(List::size)
				.sum();
	}

	private void fillNextMoveToLeaveMoves(List<Move> moves) {
		nextMoveToLeaveMoves.clear();
		for (Move move : moves) {
			List<Move> leaveMoves = new ArrayList<>();
			nextMoveToLeaveMoves.put(move, leaveMoves);
			addLeaveMoves(move, leaveMoves);
		}
	}

	private void addLeaveMoves(Move move, List<Move> leaveMoves) {
		if (move.getFollowingMoves() == null) {
			leaveMoves.add(move);
		} else {
			for (Move followingMove : move.getFollowingMoves()) {
				addLeaveMoves(followingMove, leaveMoves);
			}
		}
	}

	private Set<Integer> findIndicesToFurtherInvestigate(List<Move> moves) {
		int segmentSize = moves.size() / 3;
		Set<Integer> moveIndicesToFurtherInvestigate = new HashSet<>();
		for (int i=0; i<10; i++) {
			moveIndicesToFurtherInvestigate.add(random.nextInt(segmentSize));
		}
		for (int i=0; i<5; i++) {
			moveIndicesToFurtherInvestigate.add(moves.size() - 1 - random.nextInt(segmentSize));
		}
		moveIndicesToFurtherInvestigate.add(random.nextInt(segmentSize) + segmentSize);
		return moveIndicesToFurtherInvestigate;
	}

	private boolean cannotImprove(Move bestMove) {
		return bestMove.getValue() == MoveValues.maxValue(bestMove.getPlayer());
	}

	private void logMoves(List<Move> moves) {
		for (Move move : moves) {
			List<Move> movesToLog = new ArrayList<>();
			movesToLog.add(move);
			movesToLog.addAll(Move.bestMovesForward(move));
//			System.out.println(movesToLog.stream().map(m -> m.getDescription() + " " + m.getValue()).collect(toList()).toString());
		}
	}

	private void sortMovesOnValue(List<Move> moves, Board board) {
		evaluateMoves(moves, board);
		updateValuesInPrecedingMoves(moves);
		moveSort.sort(moves);
	}

	private void updateValuesInPrecedingMoves(List<Move> moves) {
		for (Move move : moves) {
			updateValuesInPrecedingMoves(move);
		}
	}

	private void evaluateMoves(List<Move> moves, Board board) {
		for (Move move : moves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board));
		}
	}

	private void sortMovesOnTreeDepthAscendingly(List<Move> moves) {
		moves.sort(Comparator.comparing(m -> m.depthInTree()));
	}

}
