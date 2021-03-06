package nl.gogognome.gogochess.logic.piece;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class Pawn extends PlayerPiece {

	private final int forwardRowDelta;
	private final int initialRow;
	private final int promotionRow;

	public Pawn(Player player) {
		super(player, PAWN);

		forwardRowDelta = player == WHITE ? 1 : -1;
		initialRow = player == WHITE ? 1 : 6;
		promotionRow = player == WHITE ? 7 : 0;
	}

	public void addPossibleMoves(List<Move> moves, Square from, Board board) {
		Square to1 = from.addRanks(forwardRowDelta);
		if (board.empty(to1)) {
			addMoveIncludingPromotions(moves, new Move(board.lastMove(), removeFrom(from), addTo(to1)));
		}

		Square to2 = from.addRanks(2 * forwardRowDelta);
		if (from.rank() == initialRow && board.empty(to1) && board.empty(to2)) {
			addMoveIncludingPromotions(moves, new Move(board.lastMove(), removeFrom(from), addTo(to2)));
		}

		if (from.file() > 0) {
			Square to = from.addFilesAndRanks(-1, forwardRowDelta);
			addCaptureMove(moves, from, board, to);
			addEnPassantCaptureMove(moves, from, board, to);
		}
		if (from.file() < 7) {
			Square to = from.addFilesAndRanks(1, forwardRowDelta);
			addCaptureMove(moves, from, board, to);
			addEnPassantCaptureMove(moves, from, board, to);
		}
	}

	private void addCaptureMove(List<Move> moves, Square square, Board board, Square to) {
		PlayerPiece capturedPiece = board.pieceAt(to);
		if (capturedPiece != null && capturedPiece.getPlayer() == getPlayer().opponent()) {
			addMoveIncludingPromotions(moves, new Move(board.lastMove(), removeFrom(square), capturedPiece.removeFrom(to), addTo(to)));
		}
	}

	private void addEnPassantCaptureMove(List<Move> moves, Square square, Board board, Square to) {
		Square capturedPawnSquare = to.addRanks(-forwardRowDelta);
		PlayerPiece capturedPiece = board.pieceAt(capturedPawnSquare);
		if (canCaptureEnPassant(board, capturedPawnSquare, capturedPiece)) {
			addMoveIncludingPromotions(moves, new Move(board.lastMove(), removeFrom(square), capturedPiece.removeFrom(capturedPawnSquare), addTo(to)));
		}
	}

	private boolean canCaptureEnPassant(Board board, Square capturedPawnSquare, PlayerPiece capturedPiece) {
		Square previousMoveStartSquare = capturedPawnSquare.addRanks(2 * forwardRowDelta);
		return capturedPiece != null && capturedPiece.getPlayer() == getPlayer().opponent() && capturedPiece.getPiece() == PAWN
				&& board.lastMove().getBoardMutations().contains(capturedPiece.addTo(capturedPawnSquare))
				&& previousMoveStartSquare != null && board.lastMove().getBoardMutations().contains(capturedPiece.removeFrom(previousMoveStartSquare));
	}

	private void addMoveIncludingPromotions(List<Move> moves, Move move) {
		BoardMutation lastMutation = move.getBoardMutations().get(move.getBoardMutations().size() - 1);
		if (lastMutation.getSquare().rank() == promotionRow) {
			Player player = lastMutation.getPlayerPiece().getPlayer();
			for (PlayerPiece promotedPlayerPiece : new PlayerPiece[] { new Knight(player), new Bishop(player), new Rook(player), new Queen(player) }) {
				List<BoardMutation> modifiedMutations = new ArrayList<>(move.getBoardMutations());
				modifiedMutations.set(modifiedMutations.size()-1, new BoardMutation(promotedPlayerPiece, lastMutation.getSquare(), lastMutation.getMutation()));
				Move promotionMove = new Move(move.getPrecedingMove(), player, modifiedMutations);
				moves.add(promotionMove);
			}
		} else {
			moves.add(move);
		}
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board, int deltaX, int deltaY) {
		return deltaY == forwardRowDelta && (deltaX == 1 || deltaX == -1);
	}
}
