package nl.gogognome.gogochess.logic.ai;

import static java.util.Arrays.*;
import static java.util.Collections.emptyList;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class KillerHeuristicTest {

	private KillerHeuristic killerHeuristic = new KillerHeuristic();

	@Test
	void zeroMoves_putKillerMoveFirstDoesNotModifyList() {
		List<Move> moves = emptyList();
		killerHeuristic.putKillerMoveFirst(moves);
		assertThat(moves).isEmpty();
	}

	@Test
	void addToLevelZero_movesContainKillerHeuristicAsFirstMove_putKillerMoveFirstReturnsMovesUnmodified() {
		killerHeuristic.markAsKiller(new Move(WHITE, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));

		List<Move> moves = asList(
				new Move(WHITE, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)),
				new Move(WHITE, WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)));
		killerHeuristic.putKillerMoveFirst(moves);

		assertThat(moves).extracting(move -> move.getBoardMutations())
				.containsExactly(
						asList(WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)),
						asList(WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)));
	}

	@Test
	void addToLevelZero_movesContainKillerHeuristicAsSecondMove_putKillerMoveFirstPutsKillerMoveFirst() {
		killerHeuristic.markAsKiller(new Move(WHITE, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));

		List<Move> moves = asList(
				new Move(WHITE, WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)),
				new Move(WHITE, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));
		killerHeuristic.putKillerMoveFirst(moves);

		assertThat(moves).extracting(move -> move.getBoardMutations())
				.containsExactly(
						asList(WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)),
						asList(WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)));
	}

	@Test
	void noKillerMoveSpecified_putKillerMoveDoesNotChangeMoves() {
		List<Move> moves = asList(
				new Move(WHITE, WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)),
				new Move(WHITE, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));
		killerHeuristic.putKillerMoveFirst(moves);

		assertThat(moves).extracting(move -> move.getBoardMutations())
				.containsExactly(
						asList(WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)),
						asList(WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));
	}

	@Test
	void addToLevelTwo_movesContainKillerHeuristicAsSecondMove_putKillerMoveFirstPutsKillerMoveFirst() {
		Move moveLevelZero = new Move(WHITE, WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(B1));
		Move moveLevelOne = new Move(moveLevelZero, BLACK_BISHOP.removeFrom(B3), BLACK_BISHOP.addTo(D5));
		killerHeuristic.markAsKiller(new Move(moveLevelOne, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));

		List<Move> moves = asList(
				new Move(moveLevelOne, WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)),
				new Move(moveLevelOne, WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)));
		killerHeuristic.putKillerMoveFirst(moves);

		assertThat(moves).extracting(move -> move.getBoardMutations())
				.containsExactly(
						asList(WHITE_KING.removeFrom(D1), WHITE_KING.addTo(E1)),
						asList(WHITE_PAWN.removeFrom(D2), WHITE_KING.addTo(D4)));
	}
}