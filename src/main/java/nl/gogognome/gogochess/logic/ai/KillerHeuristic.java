package nl.gogognome.gogochess.logic.ai;

import static java.util.Collections.swap;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

/**
 * This class keeps track of the killer move per depth of the search tree. The idea behind the killer heuristic is that
 * a move that leads to alpha or beta cutoff very often leads to cutoff again for the next move of one level higher
 * in the tree. Thus by trying a killer move as first move at a certain level, a cutoff is very probable.
 */
public class KillerHeuristic {

	private static class KillerMove {
		private final Move move;
		private int count;

		private KillerMove(Move move) {
			this.move = move;
		}

		public Move getMove() {
			return move;
		}

		public void incrementCount() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	private final static KillerMove NullMove = new KillerMove(new Move(WHITE));

	private final List<KillerMove> levelToKillerMove1 = new ArrayList<>();
	private final List<KillerMove> levelToKillerMove2 = new ArrayList<>();

	/**
	 * Marks the specified move as killer move.
	 * @param move the killer move
	 * @return true if the move was already the killer move at the move's level (tree depth); false otherwise
	 */
	public boolean markAsKiller(Move move) {
		int level = move.depthInTree();
		while (levelToKillerMove1.size() <= level) {
			levelToKillerMove1.add(NullMove);
			levelToKillerMove2.add(NullMove);
		}
		if (moveIsAlreadyKillerMove(move, level, levelToKillerMove1)) {
			return true;
		}
		if (moveIsAlreadyKillerMove(move, level, levelToKillerMove2)) {
			return true;
		}

		List<KillerMove> levelToKillerMove = getLeastUsedKillerMove(level);
		levelToKillerMove.set(level, new KillerMove(move));
		return false;
	}

	private boolean moveIsAlreadyKillerMove(Move move, int level, List<KillerMove> levelToKillerMove) {
		if (areEqual(levelToKillerMove.get(level).getMove(), move)) {
			levelToKillerMove.get(level).incrementCount();
			return true;
		}
		return false;
	}

	public List<Move> putKillerMoveFirst(List<Move> moves) {
		if (moves.isEmpty()) {
			return moves;
		}

		List<Move> killers = findKillerMoves(moves.get(0).depthInTree());
		int killerIndex = 0;
		for (Move killer : killers) {
			for (int index = killerIndex; index < moves.size(); index++) {
				if (areEqual(killer, moves.get(index))) {
					swap(moves, killerIndex, index);
					killerIndex++;
					break;
				}
			}
		}
		return moves;
	}

	private List<Move> findKillerMoves(int level) {
		List<Move> killerMoves = new ArrayList<>();
		if (level < levelToKillerMove1.size()) {
			killerMoves.add(getMostUsedKillerMove(level).get(level).getMove());
			killerMoves.add(getLeastUsedKillerMove(level).get(level).getMove());
		}
		return killerMoves;
	}

	private List<KillerMove> getMostUsedKillerMove(int level) {
		return levelToKillerMove1.get(level).getCount() >= levelToKillerMove2.get(level).getCount() ? levelToKillerMove1 : levelToKillerMove2;
	}

	private List<KillerMove> getLeastUsedKillerMove(int level) {
		return levelToKillerMove1.get(level).getCount() < levelToKillerMove2.get(level).getCount() ? levelToKillerMove1 : levelToKillerMove2;
	}

	private boolean areEqual(Move m1, Move m2) {
		return m1.getBoardMutations().equals(m2.getBoardMutations());
	}
}
