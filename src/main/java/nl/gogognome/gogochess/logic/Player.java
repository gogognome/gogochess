package nl.gogognome.gogochess.logic;

public enum Player {
	WHITE,
	BLACK;

	public Player other() {
		switch (this) {
			case WHITE: return BLACK;
			case BLACK: return WHITE;
			default: throw new IllegalStateException("Unknown player found: " + this);
		}
	}
}
