package nl.gogognome.gogochess.game.ai;

import static nl.gogognome.gogochess.game.Player.*;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class MoveSort {

	public void sort(List<Move> moves) {
		if (!moves.isEmpty()) {
			if (moves.get(0).getPlayer() == WHITE) {
				moves.sort((m1, m2) -> MoveValues.compareTo(m2.getValue(), m1.getValue(), WHITE));
			} else {
				moves.sort((m1, m2) -> MoveValues.compareTo(m1.getValue(), m2.getValue(), BLACK));
			}
		}
	}
}
