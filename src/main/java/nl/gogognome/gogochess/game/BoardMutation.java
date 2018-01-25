package nl.gogognome.gogochess.game;

import nl.gogognome.gogochess.game.piece.*;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj.getClass().equals(this.getClass()))) {
			return false;
		}
		BoardMutation that = (BoardMutation) obj;
		return this.playerPiece == that.playerPiece
				&& this.square.equals(that.square)
				&& this.mutation == that.mutation;
	}

	@Override
	public int hashCode() {
		return playerPiece.hashCode() * 23 + square.hashCode() * 131 * mutation.hashCode();
	}

	@Override
	public String toString() {
		return mutation + " " + playerPiece + "@" + square;
	}
}
