package nl.gogognome.gogochess.logic.piece;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.REMOVE;
import static nl.gogognome.gogochess.logic.Piece.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

public class King extends PlayerPiece {

	private final int[] deltaX = new int[] { 1, 1, -1, -1, 1, -1, 0, 0 };
	private final int[] deltaY = new int[] { 1, -1, 1, -1, 0, 0, 1, -1 };

	public King(Player player) {
		super(player, KING);
	}

	@Override
	public void addPossibleMoves(List<Move> moves, Square square, Board board) {
		addRegularMoves(moves, square, board);
		addCastlingMoves(moves, square, board);
	}

	private void addRegularMoves(List<Move> moves, Square square, Board board) {
		for (int i=0; i<deltaX.length; i++) {
			Square to = square.addColumnAndRow(deltaX[i], deltaY[i]);
			addMoveToEmptyFieldOrCapture(moves, board, square, to);
		}
	}

	private void addCastlingMoves(List<Move> moves, Square square, Board board) {
		int row = getPlayer() == Player.WHITE ? 0 : 7;
		Square kingStartSquare = new Square(4, row);
		if (!square.equals(kingStartSquare) || movedBeforeFromSquare(board.lastMove(), square)) {
			return;
		}

		Rook rook = new Rook(getPlayer());
		Square leftTowerSquare = new Square(0, row);
		if (rook.equals(board.pieceAt(leftTowerSquare)) && !movedBeforeFromSquare(board.lastMove(), leftTowerSquare)) {
			boolean squaresInBetweenEmpty = areAllSquaresInBetweenEmpty(board, leftTowerSquare, kingStartSquare);
			Square kingTo = new Square(1, row);
			boolean blockedByAttack = isAnySquareAttacked(board, row, kingTo, kingStartSquare);
			if (squaresInBetweenEmpty && !blockedByAttack) {
				moves.add(new Move(moveNotation.castlingShort(), board.lastMove(),
						removeFrom(kingStartSquare), rook.removeFrom(leftTowerSquare), addTo(kingTo), rook.addTo(new Square(2, row))));
			}
		}

		Square rightTowerSquare = new Square(7, row);
		if (rook.equals(board.pieceAt(rightTowerSquare)) && !movedBeforeFromSquare(board.lastMove(), rightTowerSquare)) {
			boolean squaresInBetweenEmpty = areAllSquaresInBetweenEmpty(board, kingStartSquare, rightTowerSquare);
			Square kingTo = new Square(6, row);
			boolean blockedByAttack = isAnySquareAttacked(board, row, kingStartSquare, kingTo);
			if (squaresInBetweenEmpty && !blockedByAttack) {
				moves.add(new Move(moveNotation.castlingLong(), board.lastMove(),
						removeFrom(kingStartSquare), rook.removeFrom(rightTowerSquare), addTo(kingTo), rook.addTo(new Square(5, row))));
			}
		}
	}

	private boolean areAllSquaresInBetweenEmpty(Board board, Square leftMostSquare, Square rightMostSquare) {
		boolean squaresInBetweenEmpty = true;
		for (int col = leftMostSquare.column() + 1; col<= rightMostSquare.column() - 1; col++) {
			squaresInBetweenEmpty = squaresInBetweenEmpty && board.empty(new Square(col, leftMostSquare.row()));
		}
		return squaresInBetweenEmpty;
	}

	private boolean isAnySquareAttacked(Board board, int row, Square leftMostSquare, Square rightMostSquare) {
		boolean blockedByAttack = false;
		for (int col = leftMostSquare.column(); col<= rightMostSquare.column(); col++) {
			Square squareThatMustNotBeAttacked = new Square(col, row);
			blockedByAttack = blockedByAttack || board.anyPieceAttacks(getPlayer().other(), squareThatMustNotBeAttacked);
		}
		return blockedByAttack;
	}

	private boolean movedBeforeFromSquare(Move move, Square square) {
		while (move != null) {
			if (moveContainsSquare(move, square)) {
				return true;
			}
			move = move.getPrecedingMove();
		}
		return false;
	}

	private boolean moveContainsSquare(Move move, Square square) {
		return move.getBoardMutations().stream().anyMatch(mutation -> mutation.getMutation() == REMOVE && mutation.getSquare().equals(square));
	}

	@Override
	public boolean attacks(Square pieceSquare, Square attackedSquare, Board board) {
		for (int i=0; i<deltaX.length; i++) {
			Square to = pieceSquare.addColumnAndRow(deltaX[i], deltaY[i]);
			if (attackedSquare.equals(to)) {
				return true;
			}
		}
		return false;
	}
}
