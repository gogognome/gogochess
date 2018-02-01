package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.Piece.*;
import static nl.gogognome.gogochess.game.Piece.KING;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.game.piece.*;

public class Board {

	public static final PlayerPiece WHITE_PAWN = new Pawn(WHITE);
	public static final PlayerPiece WHITE_KNIGHT = new Knight(WHITE);
	public static final PlayerPiece WHITE_BISHOP = new PlayerPiece(WHITE, BISHOP);
	public static final PlayerPiece WHITE_ROOK = new PlayerPiece(WHITE, ROOK);
	public static final PlayerPiece WHITE_QUEEN = new PlayerPiece(WHITE, QUEEN);
	public static final PlayerPiece WHITE_KING = new PlayerPiece(WHITE, KING);
	public static final PlayerPiece BLACK_PAWN = new Pawn(BLACK);
	public static final PlayerPiece BLACK_KNIGHT = new Knight(BLACK);
	public static final PlayerPiece BLACK_BISHOP = new PlayerPiece(BLACK, BISHOP);
	public static final PlayerPiece BLACK_ROOK = new PlayerPiece(BLACK, ROOK);
	public static final PlayerPiece BLACK_QUEEN = new PlayerPiece(BLACK, QUEEN);
	public static final PlayerPiece BLACK_KING = new PlayerPiece(BLACK, KING);

	private Move lastMove;

	private PlayerPiece[] playerPiecesPerSquare = new PlayerPiece[8*8];

	public void process(Move move) {
		for (BoardMutation boardMutation : move.getBoardMutations()) {
			process(boardMutation);
		}
		lastMove = move;
	}

	void process(BoardMutation mutation) {
		switch (mutation.getMutation()) {
			case ADD:
				addPlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			case REMOVE:
				removePlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			default:
				throw new IllegalArgumentException("Unexpected mutation found: " + mutation.getMutation());
		}
	}

	private void addPlayerPiece(PlayerPiece playerPieceToAdd, Square square) {
		int index = square.boardIndex();
		PlayerPiece playerPiece = playerPiecesPerSquare[index];
		if (playerPiece != null) {
			throw new IllegalArgumentException("The square " + square + " is not empty. It contains " + playerPiece + '.');
		}
		playerPiecesPerSquare[index] = playerPieceToAdd;
	}

	private void removePlayerPiece(PlayerPiece playerPieceToRemove, Square square) {
		int index = square.boardIndex();
		PlayerPiece playerPiece = playerPiecesPerSquare[index];
		if (playerPiece == null) {
			throw new IllegalArgumentException("The square " + square + " is empty, instead of containing " + playerPieceToRemove + '.');
		}
		if (!playerPiece.equals(playerPieceToRemove)) {
			throw new IllegalArgumentException("The square " + square + " does not contain " + playerPieceToRemove + ". It contains " + playerPiece + '.');
		}
		playerPiecesPerSquare[index] = null;
	}

	public PlayerPiece pieceAt(Square square) {
		return playerPiecesPerSquare[square.boardIndex()];
	}

	public boolean empty(Square square) {
		return pieceAt(square) == null;
	}

	public List<Move> validMoves(Player player) {
		List<Move> moves = new ArrayList<>(40);
		for (int index=0; index < playerPiecesPerSquare.length; index++) {
			PlayerPiece playerPiece = playerPiecesPerSquare[index];
			if (playerPiece != null && playerPiece.getPlayer() == player) {
				playerPiece.addPossibleMoves(moves, new Square(index), this);
			}
		}
		return moves;
	}

	public Move lastMove() {
		return lastMove;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(9*8);
		for (int row = 7; row >= 0; row--) {
			for (int column = 0; column < 8; column++) {
				char c = ((row ^ column) & 1) == 1 ? '*' : ' ';
				PlayerPiece playerPiece = playerPiecesPerSquare[new Square(column, row).boardIndex()];
				if (playerPiece != null) {
					c = formatPiece(playerPiece);
				}
				sb.append(c);
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private char formatPiece(PlayerPiece playerPiece) {
		char c;
		switch (playerPiece.getPiece()) {
			case PAWN: c = 'p'; break;
			case KNIGHT: c = 'k'; break;
			case BISHOP: c = 'b'; break;
			case ROOK: c = 'r'; break;
			case QUEEN: c = 'q'; break;
			case KING: c = 'k'; break;
			default: throw new IllegalArgumentException("Unknown piece found: " + playerPiece.getPiece());
		}

		switch (playerPiece.getPlayer()) {
			case WHITE: return c;
			case BLACK: return Character.toUpperCase(c);
			default: throw new IllegalArgumentException("Unknown player found: " + playerPiece.getPlayer());
		}
	}
}
