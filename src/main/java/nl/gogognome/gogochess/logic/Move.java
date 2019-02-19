package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Status.*;
import java.util.*;
import java.util.function.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.piece.*;

public class Move {

	private Status status = NORMAL;
	private int depthInTree;
	private final Move precedingMove;
	private final ImmutableList<BoardMutation> boardMutations;
	private final Player player;
	private MoveValue value;
	private int boardMutationsHashCode;

	public Move(Move precedingMove, BoardMutation... boardMutations) {
		this(precedingMove, precedingMove.player.opponent(), ImmutableList.copyOf(boardMutations));
	}

	public Move(Player player, BoardMutation... boardMutations) {
		this(null, player, ImmutableList.copyOf(boardMutations));
	}

	public Move(Move precedingMove, Player player, List<BoardMutation> boardMutations) {
		this.precedingMove = precedingMove;
		this.player = player;
		this.boardMutations = boardMutations instanceof ImmutableList ? (ImmutableList<BoardMutation>) boardMutations : ImmutableList.copyOf(boardMutations);
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

	static Move findCommonAncestor(Move left, Move right) {
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

	void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public MoveValue getValue() {
		return value;
	}

	public void setValue(MoveValue value) {
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
			if (move == null) {
				return asList(this); // can happen if lastMove comes from the cache
			}
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
				.filter(mutation -> mutation.getMutation() == REMOVE && (squareOfCapturedPiece.equals(mutation.getSquare()) || isEnPassentCapture()))
				.map(BoardMutation::getPlayerPiece)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("This move is not a capture"));
	}

	private boolean isEnPassentCapture() {
		if (boardMutations.size() != 3) {
			return false;
		}
		if (!boardMutations.stream().allMatch(mutation -> mutation.getPlayerPiece().getPiece() == PAWN)) {
			return false;
		}
		Square takenPawnSquare = getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() != getPlayer() && mutation.getMutation() == REMOVE)
				.map(BoardMutation::getSquare)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Expected one pawn to be removed in en passent capture."));
		return getMutationRemovingPieceFromStart().getSquare().file() != takenPawnSquare.file();
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

	public boolean boardMutationsEqual(Move that) {
		return this.getBoardMutationsHashcode() == that.getBoardMutationsHashcode()
				&& this.boardMutations.equals(that.boardMutations);
	}

	private int getBoardMutationsHashcode() {
		if (boardMutationsHashCode == 0) {
			boardMutationsHashCode = boardMutations.hashCode();
		}
		return boardMutationsHashCode;
	}

	@Override
	public String toString() {
		return boardMutations.toString();
	}
}
