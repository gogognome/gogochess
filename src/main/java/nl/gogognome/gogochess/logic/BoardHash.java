package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.logic.piece.*;

/**
 * This class implements a hash of a Board. Its implementation is based on x
 */
public class BoardHash {

	private final static long[] HASHES = new long[64*12]; // 12 = 6 white pieces + 6 black pieces

	static {
		Random random = new Random(0);
		for (int i = 0; i< HASHES.length; i++) {
			HASHES[i] = random.nextLong();
		}
	}

	private long hash;

	public long getHash(Player player) {
		if (player == WHITE) {
			return hash & 0x7fff_ffff_ffff_ffffL;
		} else {
			return hash | 0x8000_0000_0000_0000L;
		}
	}

	public void addPlayerPiece(PlayerPiece playerPieceToAdd, Square square) {
		togglePiece(playerPieceToAdd, square);
	}

	public void removePlayerPiece(PlayerPiece playerPieceToRemove, Square square) {
		togglePiece(playerPieceToRemove, square);
	}

	private void togglePiece(PlayerPiece playerPieceToRemove, Square square) {
		int index = square.boardIndex() * 12 + toIndex(playerPieceToRemove);
		hash ^= HASHES[index];
	}

	private int toIndex(PlayerPiece playerPiece) {
		int baseIndex = playerPiece.getPlayer() == WHITE ? 0 : 6;
		switch (playerPiece.getPiece()) {
			case PAWN: return baseIndex ;
			case KNIGHT: return baseIndex + 1;
			case BISHOP: return baseIndex + 2;
			case ROOK: return baseIndex + 3;
			case QUEEN: return baseIndex + 4;
			case KING: return baseIndex + 5;
			default: throw new IllegalArgumentException("Unknown piece found: " + playerPiece.getPiece());
		}
	}
}
