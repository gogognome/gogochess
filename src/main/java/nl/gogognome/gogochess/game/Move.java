package nl.gogognome.gogochess.game;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.Player.*;
import static nl.gogognome.gogochess.game.Squares.*;
import static nl.gogognome.gogochess.game.Status.*;
import java.util.ArrayList;
import java.util.*;

public class Move {

	private Status status = NORMAL;
	private int depthInTree;
	private final Move precedingMove;
	private final List<BoardMutation> boardMutations;
	private final String description;
	private final Player player;
	private List<Move> followingMoves;
	private int value;

	public Move(String description, Move precedingMove, BoardMutation... boardMutations) {
		this(description, precedingMove, precedingMove.player.other(), asList(boardMutations));
	}

	public Move(String description, Player player, BoardMutation... boardMutations) {
		this(description, null, player, asList(boardMutations));
	}

	public Move(String description, Move precedingMove, Player player, List<BoardMutation> boardMutations) {
		this.precedingMove = precedingMove;
		this.player = player;
		this.boardMutations = Collections.unmodifiableList(boardMutations);
		this.description = description;
		this.depthInTree = precedingMove == null ? 0 : precedingMove.depthInTree + 1;
	}

	public List<BoardMutation> getBoardMutations() {
		return boardMutations;
	}

	public Move getPrecedingMove() {
		return precedingMove;
	}

	public String getDescription() {
		String postfix = "";
		if (status == CHECK_MATE) {
			postfix = "++";
		} else if (status == CHECK) {
			postfix = "+";
		}
		return description + postfix;
	}

	public Player getPlayer() {
		return player;
	}

	public int depthInTree() {
		return depthInTree;
	}

	public boolean hasFollowingMoves() {
		return followingMoves != null;
	}

	public List<Move> getFollowingMoves() {
		return followingMoves;
	}

	public void setFollowingMoves(List<Move> followingMoves) {
		this.followingMoves = followingMoves;
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

	public boolean isMateChecked() {
		return followingMoves != null;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Move> pathFromBegin() {
		LinkedList<Move> moves = new LinkedList<>();
		Move move = this;
		do {
			moves.addFirst(move);
			move = move.getPrecedingMove();
		} while (move != null);
		return moves;
	}

	public static List<Move> bestMovesForward(Move lastMove) {
		List<Move> moves = new ArrayList<>();
		List<Move> followingMoves = lastMove.getFollowingMoves();
		Player player = lastMove.player.other();
		int bestValue = MoveValues.minValue(player);
		Move bestFollowingMove = null;
		if (followingMoves != null) {
			for (Move followingMove : followingMoves) {
				if (MoveValues.compareTo(followingMove.getValue(), bestValue, player) > 0) {
					bestValue = followingMove.getValue();
					bestFollowingMove = followingMove;
				}
			}
		}
		if (bestFollowingMove != null) {
			moves.add(bestFollowingMove);
			moves.addAll(bestMovesForward(bestFollowingMove));
		}

		return moves;
	}

	public static Move find(List<Move> moves, String moveDescription) {
		return moves.stream()
				.filter(m -> m.getDescription().equals(moveDescription))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("could not find move " + moveDescription + " in moves " + moves));
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public final static Move INITIAL_BOARD = new Move("initial board", BLACK,
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
