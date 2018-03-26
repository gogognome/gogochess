package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.assertMovesContain;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

class BoardTest {

	private Board board = new Board();

	@Test
	void processAddMutationToEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		assertEquals(WHITE_PAWN, board.pieceAt(A2));
	}

	@Test
	void processAddMutationToNonEmptySquareFails() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, ADD)));

		assertEquals("The square a2 is not empty. It contains white pawn.", exception.getMessage());
	}

	@Test
	void processRemoveMutationToNonEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE));

		assertNull(board.pieceAt(A2));
	}

	@Test
	void processRemoveMutationToEmptySquareFails() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)));

		assertEquals("The square a2 is empty, instead of containing white pawn.", exception.getMessage());
	}

	@Test
	void processRemoveMutationToSquareContainingWrongPieceFails() {
		board.process(new BoardMutation(WHITE_KNIGHT, A2, ADD));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)));

		assertEquals("The square a2 does not contain white pawn. It contains white knight.", exception.getMessage());
	}

	@Test
	void toStringAfterInitialMove() {
		board.process(Move.INITIAL_BOARD);
		String actualBoard = board.toString();

		assertEquals(
				"RKBQKBKR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* * * * \n" +
				" * * * *\n" +
				"pppppppp\n" +
				"rkbqkbkr\n",
				actualBoard);
	}

	@Test
	void validMovesForEmptyBoard() {
		assertThrows(IllegalStateException.class, () -> board.validMoves());
	}

	@Test
	void undoMove() {
		board.process(Move.INITIAL_BOARD);
		List<Move> moves = board.validMoves();
		board.process(find(moves, "e2-e4"));
		board.process(Move.INITIAL_BOARD);

		String actualBoard = board.toString();
		assertEquals(
				"RKBQKBKR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* * * * \n" +
				" * * * *\n" +
				"pppppppp\n" +
				"rkbqkbkr\n",
				actualBoard);
	}

	@Test
	void automaticallyUndoMoveAndProcessNewMove() {
		board.process(Move.INITIAL_BOARD);
		List<Move> moves = board.validMoves();
		board.process(find(moves, "e2-e4"));
		board.process(find(moves, "d2-d4"));

		String actualBoard = board.toString();
		assertEquals(
				"RKBQKBKR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* *p* * \n" +
				" * * * *\n" +
				"ppp pppp\n" +
				"rkbqkbkr\n",
				actualBoard);
	}

	@Test
	void moveCausingCheckIsMarkedAsCheck() {
		board.process(new Move(BLACK,
				WHITE_PAWN.addTo(E6),
				BLACK_KING.addTo(F8)));

		List<Move> moves = board.validMoves();
		assertMovesContain(moves, "e6-e7+");
		assertEquals(CHECK, find(moves, "e6-e7+").getStatus());
	}

	@Test
	void moveCausingCheckForOwnPlayerIsNotValid() {
		board.process(new Move(BLACK,
				WHITE_PAWN.addTo(E6),
				WHITE_KING.addTo(A6),
				BLACK_QUEEN.addTo(H6)));

		String moves = board.validMoves().toString();
		assertFalse(moves.contains("e6-e7"), moves);
	}

	@Test
	void moveCausingCheckMateIsMarkedAsCheckMate() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		List<Move> moves = board.validMoves();
		assertMovesContain(moves, "Qg1-g7++");
		assertEquals(CHECK_MATE, find(moves, "Qg1-g7++").getStatus());
	}

	@Test
	void moveCausingStaleMateIsMarkedAsStaleMate() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		List<Move> moves = board.validMoves();
		assertMovesContain(moves, "Qg1-g6");
		assertEquals(STALE_MATE, find(moves, "Qg1-g6").getStatus());
	}


	private Move find(List<Move> moves, String moveDescription) {
		MoveNotation moveNotation = new MoveNotation();
		return moves.stream()
				.filter(m -> moveNotation.format(m).equals(moveDescription))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("could not find move " + moveDescription + " in moves " + moves));
	}

}