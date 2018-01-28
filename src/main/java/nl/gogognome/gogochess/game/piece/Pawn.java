package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.Piece.*;
import static nl.gogognome.gogochess.game.Player.*;
import java.util.*;
import nl.gogognome.gogochess.game.*;

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

	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		Square destination1 = square.addRow(forwardRowDelta);
		if (board.empty(destination1)) {
			addMoveIncludingPromotions(moves, new Move(moveNotation(square, destination1), board.lastMove(),
					removeFrom(square), addTo(destination1)));
		}

		Square destination2 = square.addRow(2 * forwardRowDelta);
		if (square.row() == initialRow && board.empty(destination1) && board.empty(destination2)) {
			addMoveIncludingPromotions(moves, new Move(moveNotation(square, destination2), board.lastMove(),
					removeFrom(square), addTo(destination2)));
		}

		if (square.column() > 0) {
			Square captureDestination = square.addColumnAndRow(-1, forwardRowDelta);
			addCaptureMove(moves, square, board, captureDestination);
			addEnPassantCaptureMove(moves, square, board, captureDestination);
		}
		if (square.column() < 7) {
			Square captureDestination = square.addColumnAndRow(1, forwardRowDelta);
			addCaptureMove(moves, square, board, captureDestination);
			addEnPassantCaptureMove(moves, square, board, captureDestination);
		}
	}

	private void addCaptureMove(List<Move> moves, Square square, Board board, Square captureDestination) {
		PlayerPiece capturedPiece = board.pieceAt(captureDestination);
		if (capturedPiece != null && capturedPiece.getPlayer() == getPlayer().other()) {
			addMoveIncludingPromotions(moves, new Move(captureNotation(square, captureDestination, capturedPiece), board.lastMove(),
					removeFrom(square), capturedPiece.removeFrom(captureDestination), addTo(captureDestination)));
		}
	}

	private void addEnPassantCaptureMove(List<Move> moves, Square square, Board board, Square captureDestination) {
		Square capturedPawnSquare = captureDestination.addRow(-forwardRowDelta);
		PlayerPiece capturedPiece = board.pieceAt(capturedPawnSquare);
		if (canCaptureEnPassant(board, capturedPawnSquare, capturedPiece)) {
			addMoveIncludingPromotions(moves, new Move(captureNotation(square, captureDestination, capturedPiece), board.lastMove(),
					removeFrom(square), capturedPiece.removeFrom(capturedPawnSquare), addTo(captureDestination)));
		}
	}

	private boolean canCaptureEnPassant(Board board, Square capturedPawnSquare, PlayerPiece capturedPiece) {
		return capturedPiece != null && capturedPiece.getPlayer() == getPlayer().other() && capturedPiece.getPiece() == PAWN
				&& board.lastMove().getBoardMutations().contains(capturedPiece.addTo(capturedPawnSquare))
				&& board.lastMove().getBoardMutations().contains(capturedPiece.removeFrom(capturedPawnSquare.addRow(2*forwardRowDelta)));
	}

	private void addMoveIncludingPromotions(List<Move> moves, Move move) {
		BoardMutation lastMutation = move.getBoardMutations().get(move.getBoardMutations().size() - 1);
		if (lastMutation.getSquare().row() == promotionRow) {
			Player player = lastMutation.getPlayerPiece().getPlayer();
			for (Piece piece : new Piece[] { KNIGHT, BISHOP, ROOK, QUEEN }) {
				PlayerPiece promotedPlayerPiece = new PlayerPiece(player, piece);
				List<BoardMutation> modifiedMutations = new ArrayList<>(move.getBoardMutations());
				modifiedMutations.set(modifiedMutations.size()-1, new BoardMutation(promotedPlayerPiece, lastMutation.getSquare(), lastMutation.getMutation()));
				Move promotionMove = new Move(moveNotation.appendPromotionPiece(move.getDescription(), promotedPlayerPiece), move.getPrecedingMove(), modifiedMutations);
				moves.add(promotionMove);
			}
		} else {
			moves.add(move);
		}
	}
}
