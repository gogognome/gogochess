package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Status.*;
import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.piece.*;

public class Move {

	private Status status = NORMAL;
	private int depthInTree;
	private final Move precedingMove;
	private final List<BoardMutation> boardMutations;
	private final Player player;
	private int value;

	public Move(Move precedingMove, BoardMutation... boardMutations) {
		this(precedingMove, precedingMove.player.opponent(), asList(boardMutations));
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

	public boolean isCapture() {
		if (boardMutations.size() == 2) {
			return false;
		}
		int adds = 0;
		int removes = 0;
		for (int index = 0; index<boardMutations.size(); index++) {
			if (boardMutations.get(index).getMutation() == ADD) {
				adds++;
			} else {
				removes++;
			}
		}
		return adds == 1 && removes == 2;
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

	public BoardMutation getMutationRemovingPieceFromStart() {
		return getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == getPlayer() && mutation.getMutation() == REMOVE)
				.filter(filterForKingDuringCastling())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No mutation found that removes a piece of the player"));
	}

	public BoardMutation getMutationAddingPieceAtDestination() {
		return getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == getPlayer() && mutation.getMutation() == ADD)
				.filter(filterForKingDuringCastling())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No mutation found that adds a piece of the player"));
	}

	public PlayerPiece capturedPlayerPiece() {
		Square squareOfCapturedPiece = getMutationAddingPieceAtDestination().getSquare();
		return getBoardMutations().stream()
				.filter(mutation -> mutation.getMutation() == REMOVE && squareOfCapturedPiece.equals(mutation.getSquare()))
				.map(mutation -> mutation.getPlayerPiece())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("This move is not a capture"));
	}

	private Predicate<BoardMutation> filterForKingDuringCastling() {
		Predicate<BoardMutation> extraFilter = mutation -> true;
		if (getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == getPlayer() && mutation.getMutation() == REMOVE)
				.count() == 2) {
			extraFilter = mutation -> mutation.getPlayerPiece().getPiece() == KING;
		}
		return extraFilter;
	}

	@Override
	public String toString() {
		return boardMutations.toString();
	}

}
