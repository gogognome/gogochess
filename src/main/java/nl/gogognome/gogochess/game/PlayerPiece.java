package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.Piece.PAWN;
import static nl.gogognome.gogochess.game.Player.WHITE;

public class PlayerPiece {

	public static final PlayerPiece WHITE_PAWN = new PlayerPiece(WHITE, PAWN);

	private final Piece piece;
	private final Player player;

	public PlayerPiece(Player player, Piece piece) {
		if (piece == null || player == null) {
			throw new IllegalArgumentException("The arguments must not be null");
		}

		this.piece = piece;
		this.player = player;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		PlayerPiece that = (PlayerPiece) obj;
		return this.piece == that.piece
				&& this.player == that.player;
	}

	@Override
	public int hashCode() {
		return piece.hashCode() * 83 + player.hashCode();
	}

	@Override
	public String toString() {
		return (player + " " + piece).toLowerCase();
	}
}
