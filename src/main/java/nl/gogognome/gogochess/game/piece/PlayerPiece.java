package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.REMOVE;
import static nl.gogognome.gogochess.game.Piece.*;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.game.*;

public class PlayerPiece {

	public static final PlayerPiece WHITE_PAWN = new Pawn(WHITE);
	public static final PlayerPiece WHITE_KNIGHT = new PlayerPiece(WHITE, KNIGHT);
	public static final PlayerPiece WHITE_BISHOP = new PlayerPiece(WHITE, BISHOP);
	public static final PlayerPiece WHITE_ROOK = new PlayerPiece(WHITE, ROOK);
	public static final PlayerPiece WHITE_QUEEN = new PlayerPiece(WHITE, QUEEN);
	public static final PlayerPiece WHITE_KING = new PlayerPiece(WHITE, KING);
	public static final PlayerPiece BLACK_PAWN = new Pawn(BLACK);
	public static final PlayerPiece BLACK_KNIGHT = new PlayerPiece(BLACK, KNIGHT);
	public static final PlayerPiece BLACK_BISHOP = new PlayerPiece(BLACK, BISHOP);
	public static final PlayerPiece BLACK_ROOK = new PlayerPiece(BLACK, ROOK);
	public static final PlayerPiece BLACK_QUEEN = new PlayerPiece(BLACK, QUEEN);
	public static final PlayerPiece BLACK_KING = new PlayerPiece(BLACK, KING);

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
