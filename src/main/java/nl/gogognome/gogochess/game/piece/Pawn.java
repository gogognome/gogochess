package nl.gogognome.gogochess.game.piece;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
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
			moves.add(new Move(square + "-" + destination1, board.lastMove(),
					new BoardMutation(this, square, REMOVE),
					new BoardMutation(this, destination1, ADD)));
		}

		Square destination2 = square.addRow(2 * forwardRowDelta);
		if (square.row() == initialRow && board.empty(destination1) && board.empty(destination2)) {
			moves.add(new Move(square + "-" + destination2, board.lastMove(),
					new BoardMutation(this, square, REMOVE),
					new BoardMutation(this, destination2, ADD)));
		}

		if (square.column() > 0) {
			Square captureDestination = square.addColumnAndRow(-1, forwardRowDelta);
			addCaptureMove(moves, square, board, captureDestination);
		}
		if (square.column() < 7) {
			Square captureDestination = square.addColumnAndRow(1, forwardRowDelta);
			addCaptureMove(moves, square, board, captureDestination);
		}
	}

	private void addCaptureMove(List<Move> moves, Square square, Board board, Square captureDestination) {
		PlayerPiece capturedPiece = board.pieceAt(captureDestination);
		if (capturedPiece != null && capturedPiece.getPlayer() == getPlayer().other()) {
			moves.add(new Move(square + "-" + captureDestination, board.lastMove(),
					new BoardMutation(this, square, REMOVE),
					new BoardMutation(capturedPiece, captureDestination, REMOVE),
					new BoardMutation(this, captureDestination, ADD)));
		}
	}
}
