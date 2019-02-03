package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Status.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public enum Player {
	WHITE,
	BLACK;

	public Player opponent() {
		switch (this) {
			case WHITE: return BLACK;
			case BLACK: return WHITE;
			default: throw new IllegalStateException("Unknown player found: " + this);
		}
	}

	public List<Move> validMoves(Board board) {
		List<Move> moves = new ArrayList<>(40);
		addMovesIgnoringCheck(board, moves);
		removeMovesCausingCheckForOwnPlayer(board, moves);
		updateStatusForMoves(board, moves);
		return moves;
	}

	public List<Move> validCaptures(Board board) {
		List<Move> moves = new ArrayList<>(40);
		addMovesIgnoringCheck(board, moves);
		removeMovesWhere(moves, move -> !move.isCapture());
		removeMovesCausingCheckForOwnPlayer(board, moves);
		updateStatusForMoves(board, moves);
		return moves;
	}

	private void addMovesIgnoringCheck(Board board, List<Move> moves) {
		board.forEachPlayerPiece(this, (playerPiece, square) -> playerPiece.addPossibleMoves(moves, square, board));
	}

	private void updateStatusForMoves(Board board, List<Move> moves) {
		for (Move move : moves) {
			updateStatusForMove(board, move);
		}
	}

	private void updateStatusForMove(Board board, Move move) {
		Square oppositeKingSquare = board.kingSquareOf(opponent());
		if (oppositeKingSquare == null) {
			return; // can happen in tests where board contains just a few pieces but not the opponent's king
		}
		board.tryWithMove(move, () -> {
			if (board.getNumberOfRepetitionsOfCurrentPosition() >= 3) {
				move.setStatus(DRAW_BECAUSE_OF_THREEFOLD_REPETITION);
			} else {
				determineCheck(board, move, oppositeKingSquare);
				determineCheckMateAndStaleMate(board, move);
			}
		});
	}

	public void removeMovesCausingCheckForOwnPlayer(Board board, List<Move> moves) {
		removeMovesWhere(moves, move -> {
			AtomicBoolean attacksKing = new AtomicBoolean();
			board.tryWithMove(move, () -> {
				Square kingSquare = board.kingSquareOf(this);
				if (kingSquare != null) {
					board.forEachPlayerPiece(
							opponent(),
							(playerPiece, square) -> attacksKing.set(attacksKing.get() || playerPiece.attacks(square, kingSquare, board)),
							attacksKing::get);
				}
			});
			return attacksKing.get();
		});
	}

	public void removeMovesWhere(List<Move> moves, Predicate<Move> mustRemove) {
		int index = 0;
		while (index < moves.size()) {
			Move move = moves.get(index);
			if (mustRemove.test(move)) {
				moves.remove(index);
			} else {
				index++;
			}
		}
	}

	private void determineCheck(Board board, Move move, Square oppositeKingSquare) {
		AtomicBoolean attacksKing = new AtomicBoolean();
		board.forEachPlayerPiece(
				this,
				(playerPiece, square) -> attacksKing.set(attacksKing.get() || playerPiece.attacks(square, oppositeKingSquare, board)),
				attacksKing::get);

		if (attacksKing.get()) {
			move.setStatus(CHECK);
		}
	}

	private void determineCheckMateAndStaleMate(Board board, Move move) {
		List<Move> otherPlayerMoves = new ArrayList<>();
		board.forEachPlayerPiece(
				opponent(),
				(opponentPiece, square) -> {
					opponentPiece.addPossibleMoves(otherPlayerMoves, square, board);
					opponent().removeMovesCausingCheckForOwnPlayer(board, otherPlayerMoves);
				},
				() -> !otherPlayerMoves.isEmpty());

		if (otherPlayerMoves.isEmpty()) {
			move.setStatus(move.getStatus() == CHECK ? CHECK_MATE : STALE_MATE);
		}
	}

}
