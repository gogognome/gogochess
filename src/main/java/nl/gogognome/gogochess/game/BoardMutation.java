package nl.gogognome.gogochess.game;

public class BoardMutation {

	public enum Mutation {
		ADD,
		REMOVE
	}

	private final PlayerPiece playerPiece;
	private final Square square;
	private final Mutation mutation;

	public BoardMutation(PlayerPiece playerPiece, Square square, Mutation mutation) {
		if (playerPiece == null || square == null || mutation == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}

		this.playerPiece = playerPiece;
		this.square = square;
		this.mutation = mutation;
	}

	public PlayerPiece getPlayerPiece() {
		return playerPiece;
	}

	public Square getSquare() {
		return square;
	}

	public Mutation getMutation() {
		return mutation;
	}
}
