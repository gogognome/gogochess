package nl.gogognome.gogochess.game;

public enum Piece {
	PAWN(1),
	KNIGHT(3),
	BISHOP(3),
	ROOK(5),
	QUEEN(10),
	KING(1000000);

	private final int value;

	Piece(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}
}
