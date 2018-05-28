package nl.gogognome.gogochess.logic.ai;

import static java.util.Collections.swap;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

/**
 * This class keeps track of the killer move per depth of the search tree. The idea behind the killer heuristic is that
 * a move that leads to alpha or beta cutoff very often leads to cutoff again for the next move of one level higher
 * in the tree. Thus by trying a killer move as first move at a certain level, a cutoff is very probable.
 */
public class KillerHeuristic {

	private final List<Move> levelToKillerMove = new ArrayList<>();

	/**
	 * Marks the specified move as killer move.
	 * @param move the killer move
	 * @return true if the move was already the killer move at the move's level (tree depth); false otherwise
	 */
	public boolean markAsKiller(Move move) {
		int level = move.depthInTree();
		while (levelToKillerMove.size() <= level) {
			levelToKillerMove.add(null);
		}
		Move previousKillerMove = levelToKillerMove.set(level, move);
		return (previousKillerMove != null && areEqual(previousKillerMove, move));
	}

	public List<Move> putKillerMoveFirst(List<Move> moves) {
		if (moves.isEmpty()) {
			return moves;
		}

		Move killer = findKillerMove(moves);
		if (killer == null) {
			return moves;
		}

		for (int index=0; index<moves.size(); index++) {
			if (areEqual(killer, moves.get(index))) {
				swap(moves, 0, index);
				break;
			}
		}

		return moves;
	}

	private Move findKillerMove(List<Move> moves) {
		int level = moves.get(0).depthInTree();
		return level < levelToKillerMove.size() ? levelToKillerMove.get(level) : null;
	}

	private boolean areEqual(Move m1, Move m2) {
		return m1.getBoardMutations().equals(m2.getBoardMutations());
	}
}
