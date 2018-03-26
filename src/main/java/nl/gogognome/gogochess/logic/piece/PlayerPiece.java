package nl.gogognome.gogochess.logic.piece;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public abstract class PlayerPiece {

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

	public abstract void addPossibleMoves(List<Move> moves, Square square, Board board);

	boolean addMoveToEmptyFieldOrCapture(List<Move> moves, Board board, Square square, Square to) {
		if (to == null) {
			return false;
		}
		PlayerPiece capturedPiece = board.pieceAt(to);
		if (capturedPiece == null) {
			moves.add(new Move(board.lastMove(), removeFrom(square), addTo(to)));
			return true;
		} else {
			if (capturedPiece.getPlayer() != getPlayer()) {
				moves.add(new Move(board.lastMove(), removeFrom(square), capturedPiece.removeFrom(to), addTo(to)));
			}
			return false;
		}
	}

	public BoardMutation addTo(Square square) {
		return new BoardMutation(this, square, ADD);
	}

	public BoardMutation removeFrom(Square square) {
		return new BoardMutation(this, square, REMOVE);
	}

	/**
	 * Checks whether this player piece attacks a specific square.
	 * @param pieceSquare the square where this piece is located
	 * @param attackedSquare the square that must be checked
	 * @param board the board
	 * @return true if this player piece attacks attackedSquare; false otherwise
	 */
	public abstract boolean attacks(Square pieceSquare, Square attackedSquare, Board board);

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerPiece)) {
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
