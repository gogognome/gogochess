package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class PlayerPiece {

	protected MoveNotation moveNotation = new MoveNotation();

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

	protected boolean addMoveToEmptyFieldOCapture(List<Move> moves, Board board, Square square, Square to) {
		if (to == null) {
			return false;
		}
		PlayerPiece capturedPiece = board.pieceAt(to);
		if (capturedPiece == null) {
			moves.add(new Move(moveNotation(square, to), board.lastMove(),
					removeFrom(square), addTo(to)));
			return true;
		} else {
			if (capturedPiece.getPlayer() != getPlayer()) {
				moves.add(new Move(captureNotation(square, to, capturedPiece), board.lastMove(),
						removeFrom(square), capturedPiece.removeFrom(to), addTo(to)));
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

	public String moveNotation(Square from, Square to) {
		return moveNotation.move(this, from, to);
	}

	public String captureNotation(Square from, Square to, PlayerPiece capturedPiece) {
		return moveNotation.capture(this, from, to, capturedPiece);
	}

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
