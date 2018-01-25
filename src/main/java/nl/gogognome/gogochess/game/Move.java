package nl.gogognome.gogochess.game;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.ADD;
import java.util.*;
import nl.gogognome.gogochess.game.piece.*;

public class Move {

	private final Move precedingMove;
	private List<BoardMutation> boardMutations;
	private String description;

	public Move(String description, Move precedingMove, BoardMutation... boardMutations) {
		this.precedingMove = precedingMove;
		this.boardMutations = Collections.unmodifiableList(asList(boardMutations));
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
			new BoardMutation(PlayerPiece.WHITE_ROOK, new Square("A1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_KNIGHT, new Square("B1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_BISHOP, new Square("C1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_QUEEN, new Square("D1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_KING, new Square("E1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_BISHOP, new Square("F1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_KNIGHT, new Square("G1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_ROOK, new Square("H1"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("A2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("B2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("C2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("D2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("E2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("F2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("G2"), ADD),
			new BoardMutation(PlayerPiece.WHITE_PAWN, new Square("H2"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("A7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("B7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("C7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("D7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("E7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("F7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("G7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_PAWN, new Square("H7"), ADD),
			new BoardMutation(PlayerPiece.BLACK_ROOK, new Square("A8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_KNIGHT, new Square("B8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_BISHOP, new Square("C8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_QUEEN, new Square("D8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_KING, new Square("E8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_BISHOP, new Square("F8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_KNIGHT, new Square("G8"), ADD),
			new BoardMutation(PlayerPiece.BLACK_ROOK, new Square("H8"), ADD));
}
