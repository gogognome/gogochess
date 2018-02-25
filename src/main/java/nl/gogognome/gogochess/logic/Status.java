package nl.gogognome.gogochess.logic;

public enum Status {
	NORMAL,
	CHECK,
	CHECK_MATE,
	STALE_MATE;

	public boolean isGameOver() {
		return this == CHECK_MATE || this == STALE_MATE;
	}

}