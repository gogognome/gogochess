package nl.gogognome.gogochess.logic;

public enum Status {
	NORMAL,
	CHECK,
	CHECK_MATE,
	STALE_MATE,
	DRAW_BECAUSE_OF_THREEFOLD_REPETITION;

	public boolean isGameOver() {
		return this == CHECK_MATE || this == STALE_MATE || this == DRAW_BECAUSE_OF_THREEFOLD_REPETITION;
	}

}