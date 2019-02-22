package nl.gogognome.gogochess.logic.ai;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.MoveValue.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class CompositeBoardEvaluator implements BoardEvaluator {

	private final List<BoardEvaluator> evaluators;

	public CompositeBoardEvaluator(BoardEvaluator... evaluators) {
		this.evaluators = asList(evaluators);
	}

	@Override
	public MoveValue value(Board board) {
		MoveValue value = ZERO;
		// This method is called very, very often. To prevent garbage on the heap, use index instead of iterator.
		//noinspection ForLoopReplaceableByForEach
		for (int i=0; i<evaluators.size(); i++) {
			value = value.add(evaluators.get(i).value(board), "composite board evaluation");
		}
		return value;
	}
}
