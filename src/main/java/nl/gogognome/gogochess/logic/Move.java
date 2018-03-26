package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import java.util.*;

public class Move {

	private Status status = NORMAL;
	private int depthInTree;
	private final Move precedingMove;
	private final List<BoardMutation> boardMutations;
	private final Player player;
	private int value;

	public Move(Move precedingMove, BoardMutation... boardMutations) {
		this(precedingMove, precedingMove.player.other(), asList(boardMutations));
	}

	public Move(Player player, BoardMutation... boardMutations) {
		this(null, player, asList(boardMutations));
	}

	public Move(Move precedingMove, Player player, List<BoardMutation> boardMutations) {
		this.precedingMove = precedingMove;
		this.player = player;
		this.boardMutations = Collections.unmodifiableList(boardMutations);
		this.depthInTree = precedingMove == null ? 0 : precedingMove.depthInTree + 1;
	}

	public List<BoardMutation> getBoardMutations() {
		return boardMutations;
	}

	public Move getPrecedingMove() {
		return precedingMove;
	}

	public Player getPlayer() {
		return player;
	}

	public int depthInTree() {
		return depthInTree;
	}

	public static Move findCommonAncestor(Move left, Move right) {
		if (left == null || right == null) {
			return null;
		}
		while (left.depthInTree > right.depthInTree) {
			left = left.precedingMove;
		}
		while (right.depthInTree > left.depthInTree) {
			right = right.precedingMove;
		}
		while (left != right) {
			left = left.precedingMove;
			right = right.precedingMove;
		}
		return left;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isCastling() {
		return boardMutations.stream().filter(m -> m.getPlayerPiece().getPiece().equals(KING)).count() == 2
				&& boardMutations.stream().filter(m -> m.getPlayerPiece().getPiece().equals(ROOK)).count() == 2;
	}

	/**
	 * Returns a list of moves from the current move to lastMove, provided that the current move is an ancestor
	 * of lastMove.
	 * @param lastMove the last move
	 * @return the list of moves
	 */
	public List<Move> pathTo(Move lastMove) {
		LinkedList<Move> moves = new LinkedList<>();
		Move move = lastMove;
		while (!this.equals(move)) {
			moves.addFirst(move);
			move = move.getPrecedingMove();
		}
		moves.addFirst(this);
		return moves;
	}

	@Override
	public String toString() {
		return boardMutations.toString();
	}

	public final static Move INITIAL_BOARD = new Move(BLACK,
			WHITE_ROOK.addTo(A1),
			WHITE_KNIGHT.addTo(B1),
			WHITE_BISHOP.addTo(C1),
			WHITE_QUEEN.addTo(D1),
			WHITE_KING.addTo(E1),
			WHITE_BISHOP.addTo(F1),
			WHITE_KNIGHT.addTo(G1),
			WHITE_ROOK.addTo(H1),
			WHITE_PAWN.addTo(A2),
			WHITE_PAWN.addTo(B2),
			WHITE_PAWN.addTo(C2),
			WHITE_PAWN.addTo(D2),
			WHITE_PAWN.addTo(E2),
			WHITE_PAWN.addTo(F2),
			WHITE_PAWN.addTo(G2),
			WHITE_PAWN.addTo(H2),
			BLACK_PAWN.addTo(A7),
			BLACK_PAWN.addTo(B7),
			BLACK_PAWN.addTo(C7),
			BLACK_PAWN.addTo(D7),
			BLACK_PAWN.addTo(E7),
			BLACK_PAWN.addTo(F7),
			BLACK_PAWN.addTo(G7),
			BLACK_PAWN.addTo(H7),
			BLACK_ROOK.addTo(A8),
			BLACK_KNIGHT.addTo(B8),
			BLACK_BISHOP.addTo(C8),
			BLACK_QUEEN.addTo(D8),
			BLACK_KING.addTo(E8),
			BLACK_BISHOP.addTo(F8),
			BLACK_KNIGHT.addTo(G8),
			BLACK_ROOK.addTo(H8));

}
