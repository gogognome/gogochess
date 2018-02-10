package nl.gogognome.gogochess.game.ai;

import static java.util.stream.Collectors.toList;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class AdaptiveLookAheadArtificialIntelligence implements ArtificialIntelligence {

	private final Random random = new Random(System.currentTimeMillis());
	private final BoardEvaluator boardEvaluator = ComplexBoardEvaluator.newInstance();

	private final int maxThinkTimeMs;
	private final int maxNrMoves;
	private final Map<Move, List<Move>> nextMoveToLeaveMoves = new HashMap<>();

	public AdaptiveLookAheadArtificialIntelligence(int maxThinkTimeMs, int maxNrMoves) {
		this.maxThinkTimeMs = maxThinkTimeMs;
		this.maxNrMoves = maxNrMoves;
	}

	public Move nextMove(Board board, Player player) {
		long endTime = System.currentTimeMillis() + maxThinkTimeMs;
		List<Move> moves = board.validMoves(player);
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
					updateValuesInPrecedingMoves(moveToFurtherInvestigate, leaveMoveToBeFurtherInvestigated);
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

	private void replaceLeaveMoveWithDeeperLeaveMove(
			Board board, List<Move> leaveMoves, Move leaveMoveToBeFurtherInvestigated) {
		board.process(leaveMoveToBeFurtherInvestigated);
		Player investigatePlayer = leaveMoveToBeFurtherInvestigated.getPlayer().other();
		List<Move> validMoves = board.validMoves(investigatePlayer);

		for (Move move : validMoves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board));
			leaveMoves.add(move);
		}
	}

	private void updateValuesInPrecedingMoves(Move parentMove, Move childMove) {
		do {
			int bestValue = MoveValues.minValue(childMove.getPlayer().other());
			for (Move followingMove : childMove.getFollowingMoves()) {
				if (MoveValues.compareTo(followingMove.getValue(), bestValue, childMove.getPlayer().other()) > 0) {
					bestValue = followingMove.getValue();
				}
			}
			childMove.setValue(bestValue);
			childMove = childMove.getPrecedingMove();
		} while (parentMove.depthInTree() <= childMove.depthInTree());
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
			System.out.println(movesToLog.stream().map(m -> m.getDescription() + " " + m.getValue()).collect(toList()).toString());
		}
	}

	private void sortMovesOnValue(List<Move> moves, Board board) {
		for (Move move : moves) {
			board.process(move);
			move.setValue(boardEvaluator.value(board));
		}

		if (!moves.isEmpty()) {
			if (moves.get(0).getPlayer() == WHITE) {
				moves.sort((m1, m2) -> MoveValues.compareTo(m2.getValue(), m1.getValue(), WHITE));
			} else {
				moves.sort((m1, m2) -> MoveValues.compareTo(m1.getValue(), m2.getValue(), BLACK));
			}
		}
	}

	private void sortMovesOnTreeDepthAscendingly(List<Move> moves) {
		moves.sort(Comparator.comparing(m -> m.depthInTree()));
	}

}
