package nl.gogognome.gogochess.logic;

import nl.gogognome.gogochess.logic.piece.*;

public class MoveNotation {

	public String move(PlayerPiece playerPiece, Square from, Square to) {
		return pieceName(playerPiece) + format(from) + '-' + format(to);
	}

	public String capture(PlayerPiece playerPiece, Square from, Square to, PlayerPiece capturedPiece) {
		return pieceName(playerPiece) + format(from) + 'x' + pieceName(capturedPiece) + format(to);
	}

	public String appendPromotionPiece(String description, PlayerPiece playerPiece) {
		return description + '(' + pieceName(playerPiece) + ')';
	}

	public String castlingShort() {
		return "O-O";
	}

	public String castlingLong() {
		return "O-O-O";
	}

	private String pieceName(PlayerPiece playerPiece) {
		switch (playerPiece.getPiece()) {
			case PAWN: return "";
			case KNIGHT: return "N";
			case BISHOP: return "B";
			case ROOK: return "R";
			case QUEEN: return "Q";
			case KING: return "K";
			default: throw new IllegalArgumentException("Unknown piece found: " + playerPiece.getPiece());
		}
	}

	private String format(Square square) {
		return Character.toString(((char) ('a' + square.column()))) + ((char)('1' + square.row()));
	}
}
