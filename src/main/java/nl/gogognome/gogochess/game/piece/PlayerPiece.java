package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class PlayerPiece {

	private MoveNotation moveNotation = new MoveNotation();

	private final Piece piece;
	private final Player player;

	public PlayerPiece(Player player, Piece piece) {
		if (piece == null || player == null) {
			throw new IllegalArgumentException("The arguments must not be null");
		}

		this.piece = piece;
		this.player = player;
	}

	public Piece getPiece() {
		return piece;
	}

	public Player getPlayer() {
		return player;
	}

	// TODO: make this abstract after all pieces have been implemented
	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
	}

	protected BoardMutation addTo(Square square) {
		return new BoardMutation(this, square, ADD);
	}

	protected BoardMutation removeFrom(Square square) {
		return new BoardMutation(this, square, REMOVE);
	}

	protected String moveNotation(Square from, Square to) {
		return moveNotation.move(this, from, to);
	}

	protected String captureNotation(Square from, Square to, PlayerPiece capturedPiece) {
		return moveNotation.capture(this, from, to, capturedPiece);
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
