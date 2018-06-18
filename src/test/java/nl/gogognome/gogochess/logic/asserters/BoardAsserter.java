package nl.gogognome.gogochess.logic.asserters;

import org.assertj.core.api.*;
import nl.gogognome.gogochess.logic.*;

public class BoardAsserter extends AbstractAssert<BoardAsserter, Board> {

	public BoardAsserter(Board actual) {
		super(actual, BoardAsserter.class);
	}

	public static BoardAsserter assertThat(Board board) {
		return new BoardAsserter(board);
	}

	public BoardAsserter lastMoveStatusIsEqualTo(Status status) {
		isNotNull();
		if (actual.lastMove().getStatus() != status) {
			failWithMessage("Expected last move status to be %s but was %s",
					status.toString(), actual.lastMove().getStatus().toString());
		}
		return this;
	}
}
