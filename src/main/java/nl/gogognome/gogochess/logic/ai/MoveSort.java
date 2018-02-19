package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class MoveSort {

	public void sort(List<Move> moves) {
		if (!moves.isEmpty()) {
			moves.sort((m1, m2) -> MoveValues.compareTo(m2.getValue(), m1.getValue(), moves.get(0).getPlayer()));
		}
	}
}
