package nl.gogognome.gogochess.logic.piece;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Moves.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class KnightTest {

	private Board board = new Board();

	@Test
	void validMovesForKnightAtMiddleOfBoard() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KNIGHT, E4, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContainsExactlyInAnyOrder(moves, "Ne4-f6", "Ne4-f2", "Ne4-d6", "Ne4-d2", "Ne4-g5", "Ne4-g3", "Ne4-c5", "Ne4-c3");
		assertThat(moves.stream().map(Move::getPrecedingMove).collect(toSet())).containsExactly(setup);
		assertThat(moves.stream().map(Move::getBoardMutations).collect(toList())).containsExactlyInAnyOrder(
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, F6, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, F2, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, D6, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, D2, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, G5, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, G3, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, C5, ADD)),
				asList(new BoardMutation(BLACK_KNIGHT, E4, REMOVE), new BoardMutation(BLACK_KNIGHT, C3, ADD)));
	}


	@Test
	void validMovesForKnightAtCornerOfBoard() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KNIGHT, A1, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContainsExactlyInAnyOrder(moves, "Na1-b3", "Na1-c2");
		assertThat(moves.stream().map(Move::getPrecedingMove).collect(toSet())).containsExactly(setup);
		assertThat(moves.stream().map(Move::getBoardMutations).collect(toList()))
				.containsExactlyInAnyOrder(
						asList(new BoardMutation(BLACK_KNIGHT, A1, REMOVE), new BoardMutation(BLACK_KNIGHT, B3, ADD)),
						asList(new BoardMutation(BLACK_KNIGHT, A1, REMOVE), new BoardMutation(BLACK_KNIGHT, C2, ADD)));
	}

	@Test
	void knightCannotCapturePieceOfOwnPlayer() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KNIGHT, E4, ADD),
				new BoardMutation(BLACK_BISHOP, D2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertFalse(moves.toString().contains("Ne4xBd2"), moves.toString());
	}

	@Test
	void knightCanCapturePieceOfOtherPlayer() {
		Move setup = new Move(WHITE,
				new BoardMutation(BLACK_KNIGHT, E4, ADD),
				new BoardMutation(WHITE_BISHOP, D2, ADD));
		board.process(setup);

		List<Move> moves = board.validMoves();

		assertMovesContain(moves, "Ne4xBd2");
		assertMovesContain(moves,
				BLACK_KNIGHT.removeFrom(E4),
				WHITE_BISHOP.removeFrom(D2),
				BLACK_KNIGHT.addTo(D2));
	}
}