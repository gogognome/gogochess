package nl.gogognome.gogochess.game;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import java.util.*;

public class Move {

	private final Move precedingMove;
	private List<BoardMutation> boardMutations;
	private String description;

	public Move(String description, Move precedingMove, BoardMutation... boardMutations) {
		this(description, precedingMove, asList(boardMutations));
	}

	public Move(String description, Move precedingMove, List<BoardMutation> boardMutations) {
		this.precedingMove = precedingMove;
		this.boardMutations = Collections.unmodifiableList(boardMutations);
		this.description = description;
	}

	public List<BoardMutation> getBoardMutations() {
		return boardMutations;
	}

	public Move getPrecedingMove() {
		return precedingMove;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}

	public final static Move INITIAL_BOARD = new Move("initial board", null,
			new BoardMutation(Board.WHITE_ROOK, new Square("A1"), ADD),
			new BoardMutation(Board.WHITE_KNIGHT, new Square("B1"), ADD),
			new BoardMutation(Board.WHITE_BISHOP, new Square("C1"), ADD),
			new BoardMutation(Board.WHITE_QUEEN, new Square("D1"), ADD),
			new BoardMutation(Board.WHITE_KING, new Square("E1"), ADD),
			new BoardMutation(Board.WHITE_BISHOP, new Square("F1"), ADD),
			new BoardMutation(Board.WHITE_KNIGHT, new Square("G1"), ADD),
			new BoardMutation(Board.WHITE_ROOK, new Square("H1"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("A2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("B2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("C2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("D2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("E2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("F2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("G2"), ADD),
			new BoardMutation(Board.WHITE_PAWN, new Square("H2"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("A7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("B7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("C7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("D7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("E7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("F7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("G7"), ADD),
			new BoardMutation(Board.BLACK_PAWN, new Square("H7"), ADD),
			new BoardMutation(Board.BLACK_ROOK, new Square("A8"), ADD),
			new BoardMutation(Board.BLACK_KNIGHT, new Square("B8"), ADD),
			new BoardMutation(Board.BLACK_BISHOP, new Square("C8"), ADD),
			new BoardMutation(Board.BLACK_QUEEN, new Square("D8"), ADD),
			new BoardMutation(Board.BLACK_KING, new Square("E8"), ADD),
			new BoardMutation(Board.BLACK_BISHOP, new Square("F8"), ADD),
			new BoardMutation(Board.BLACK_KNIGHT, new Square("G8"), ADD),
			new BoardMutation(Board.BLACK_ROOK, new Square("H8"), ADD));
}
