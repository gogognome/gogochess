package nl.gogognome.gogochess.game;

import java.util.*;

public class Move {

	private final Move precedingMove;
	private List<BoardMutation> boardMutations;
	private String description;

	public Move(Move precedingMove, List<BoardMutation> boardMutations, String description) {
		this.precedingMove = precedingMove;
		this.boardMutations = boardMutations;
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
