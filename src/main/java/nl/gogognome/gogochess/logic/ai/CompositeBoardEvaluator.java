package nl.gogognome.gogochess.logic.ai;

import static java.util.Arrays.asList;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class CompositeBoardEvaluator implements BoardEvaluator {

	private final List<BoardEvaluator> evaluators;

	public CompositeBoardEvaluator(BoardEvaluator... evaluators) {
		this.evaluators = asList(evaluators);
	}

	@Override
	public int value(Board board) {
		int value = 0;
		for (int i=0; i<evaluators.size(); i++) {
			value = MoveValues.add(value, evaluators.get(i).value(board));
			if (value == Integer.MAX_VALUE || value == Integer.MIN_VALUE) {
				break;
			}
		}
		return value;
	}
}
