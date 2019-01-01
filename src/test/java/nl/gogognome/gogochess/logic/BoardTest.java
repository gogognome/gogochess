package nl.gogognome.gogochess.logic;

import nl.gogognome.gogochess.logic.asserters.BoardAsserter;
import nl.gogognome.gogochess.logic.movenotation.MoveNotation;
import nl.gogognome.gogochess.logic.movenotation.ReverseAlgebraicNotation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.REMOVE;
import static nl.gogognome.gogochess.logic.Moves.assertMovesContain;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

	private Board board = new Board();

	@Test
	void processAddMutationToEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		assertThat(board.pieceAt(A2)).isEqualTo(WHITE_PAWN);
	}

	@Test
	void processAddMutationToNonEmptySquareFails() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		assertThatThrownBy(() -> board.process(new BoardMutation(WHITE_PAWN, A2, ADD)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The square a2 is not empty. It contains white pawn.");
	}

	@Test
	void processRemoveMutationToNonEmptySquareSucceeds() {
		board.process(new BoardMutation(WHITE_PAWN, A2, ADD));

		board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE));

		assertThat(board.pieceAt(A2)).isNull();
	}

	@Test
	void processRemoveMutationToEmptySquareFails() {
		assertThatThrownBy(() -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The square a2 is empty, instead of containing white pawn.");
	}

	@Test
	void processRemoveMutationToSquareContainingWrongPieceFails() {
		board.process(new BoardMutation(WHITE_KNIGHT, A2, ADD));

		assertThatThrownBy(() -> board.process(new BoardMutation(WHITE_PAWN, A2, REMOVE)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The square a2 does not contain white pawn. It contains white knight.");
	}

	@Test
	void toStringAfterInitialMove() {
		board.initBoard();
		String actualBoard = board.toString();

		assertThat(actualBoard).isEqualTo(
				"RNBQKBNR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* * * * \n" +
				" * * * *\n" +
				"pppppppp\n" +
				"rnbqkbnr\n");
	}

	@Test
	void validMovesForEmptyBoard() {
		assertThatThrownBy(() -> board.currentPlayer().validMoves(board))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void undoMove() {
		board.initBoard();
		List<Move> moves = board.currentPlayer().validMoves(board);
		board.process(find(moves, "e2-e4"));
		board.initBoard();

		String actualBoard = board.toString();
		assertThat(actualBoard).isEqualTo(
				"RNBQKBNR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* * * * \n" +
				" * * * *\n" +
				"pppppppp\n" +
				"rnbqkbnr\n");
	}

	@Test
	void automaticallyUndoMoveAndProcessNewMove() {
		board.initBoard();
		List<Move> moves = board.currentPlayer().validMoves(board);
		board.process(find(moves, "e2-e4"));
		board.process(find(moves, "d2-d4"));

		String actualBoard = board.toString();
		assertThat(actualBoard).isEqualTo(
				"RNBQKBNR\n" +
				"PPPPPPPP\n" +
				"* * * * \n" +
				" * * * *\n" +
				"* *p* * \n" +
				" * * * *\n" +
				"ppp pppp\n" +
				"rnbqkbnr\n");
	}

	@Test
	void moveCausingCheckIsMarkedAsCheck() {
		board.process(new Move(BLACK,
				WHITE_PAWN.addTo(E6),
				BLACK_KING.addTo(F8)));

		List<Move> moves = board.currentPlayer().validMoves(board);
		assertMovesContain(moves, "e6-e7+");
		assertThat(find(moves, "e6-e7+").getStatus()).isEqualTo(CHECK);
	}

	@Test
	void moveCausingCheckForOwnPlayerIsNotValid() {
		board.process(new Move(BLACK,
				WHITE_PAWN.addTo(E6),
				WHITE_KING.addTo(A6),
				BLACK_QUEEN.addTo(H6)));

		String moves = board.currentPlayer().validMoves(board).toString();
		assertThat(moves).doesNotContain("e6-e7");
	}

	@Test
	void moveCausingCheckMateIsMarkedAsCheckMate() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		List<Move> moves = board.currentPlayer().validMoves(board);
		assertMovesContain(moves, "Qg1-g7++");
		assertThat(find(moves, "Qg1-g7++").getStatus()).isEqualTo(CHECK_MATE);
	}

	@Test
	void moveCausingStaleMateIsMarkedAsStaleMate() {
		board.process(new Move(BLACK,
				WHITE_QUEEN.addTo(G1),
				WHITE_KING.addTo(F7),
				BLACK_KING.addTo(H8)));

		List<Move> moves = board.currentPlayer().validMoves(board);
		assertMovesContain(moves, "Qg1-g6");
		assertThat(find(moves, "Qg1-g6").getStatus()).isEqualTo(STALE_MATE);
	}

	@Test
	void threeFoldRepetitionLeadsToDraw() {
		board.initBoard(); // first occurrence
		BoardAsserter.assertThat(board).lastMoveStatusIsEqualTo(Status.NORMAL);

		board.process(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(C3));
		board.process(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));
		board.process(WHITE_KNIGHT.removeFrom(C3), WHITE_KNIGHT.addTo(B1));
		board.process(BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8)); // second occurrence
		BoardAsserter.assertThat(board).lastMoveStatusIsEqualTo(Status.NORMAL);

		board.process(WHITE_KNIGHT.removeFrom(B1), WHITE_KNIGHT.addTo(C3));
		board.process(BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));
		board.process(WHITE_KNIGHT.removeFrom(C3), WHITE_KNIGHT.addTo(B1));
		board.process(BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8)); // third occurrence
		BoardAsserter.assertThat(board).lastMoveStatusIsEqualTo(Status.DRAW_BECAUSE_OF_THREEFOLD_REPETITION);
	}

	@Test
	void singleWhitePawnIsPassedPawn() {
		board.process(new Move(BLACK, WHITE_PAWN.addTo(E4)));
		assertThat(board.isPassedPawn(WHITE_PAWN, E4)).isTrue();
	}

	@Test
	void singleBlackPawnInFileAIsPassedPawn() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(A4)));
		assertThat(board.isPassedPawn(BLACK_PAWN, A4)).isTrue();
	}

	@Test
	void singleWhitePawnInFileHIsPassedPawn() {
		board.process(new Move(BLACK, WHITE_PAWN.addTo(H4)));
		assertThat(board.isPassedPawn(WHITE_PAWN, H4)).isTrue();
	}

	@Test
	void blackPawnWithWhitePawnBehindItIsPassedPawn() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(A4), WHITE_PAWN.addTo(A5)));
		assertThat(board.isPassedPawn(BLACK_PAWN, A4)).isTrue();
	}

	@Test
	void whitePawnWithBlackPawnBehindItInAdjacentFileIsPassedPawn() {
		board.process(new Move(BLACK, WHITE_PAWN.addTo(A4), BLACK_PAWN.addTo(B3)));
		assertThat(board.isPassedPawn(WHITE_PAWN, A4)).isTrue();
	}

	@Test
	void whitePawnWithBlackPawnNextToItInAdjacentFileIsPassedPawn() {
		board.process(new Move(BLACK, WHITE_PAWN.addTo(E7), BLACK_PAWN.addTo(F7)));
		assertThat(board.isPassedPawn(WHITE_PAWN, E7)).isTrue();
	}

	@Test
	void blackPawnWithWhitePawnInFrontOfItIsNotPassedPawn() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(B4), WHITE_PAWN.addTo(B2)));
		assertThat(board.isPassedPawn(BLACK_PAWN, B4)).isFalse();
	}

	@Test
	void blackPawnWithWhitePawnInFrontOfItAdjacentFileIsNotPassedPawn() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(H4), WHITE_PAWN.addTo(G2)));
		assertThat(board.isPassedPawn(BLACK_PAWN, H4)).isFalse();
	}

	@Test
	void blackPawnWithWhitePawnInFrontOfItAdjacentAdjacentFileIsPassedPawn() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(H4), WHITE_PAWN.addTo(F2)));
		assertThat(board.isPassedPawn(BLACK_PAWN, H4)).isTrue();
	}

	@Test
	void fieldsBehindWhitePassedPawnWithoutPieceBehindItEndAtLastRank() {
		board.process(new Move(WHITE, WHITE_PAWN.addTo(E6)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).containsExactlyInAnyOrder(E1, E2, E3, E4, E5);
	}

	@Test
	void fieldsBehindBlackPassedPawnWithoutPieceBehindItEndAtLastRank() {
		board.process(new Move(BLACK, BLACK_PAWN.addTo(H3)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).containsExactlyInAnyOrder(H4, H5, H6, H7, H8);
	}

	@Test
	void fieldsBehindPassedPawnReturnsEmptyListForWhitePawnWhichIsNotPassed() {
		board.process(new Move(WHITE, WHITE_PAWN.addTo(E6), BLACK_PAWN.addTo(D7)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).isEmpty();
	}

	@Test
	void fieldsBehindWhitePassedPawnEndAtFirstNonEmptySquare() {
		board.process(new Move(WHITE, WHITE_PAWN.addTo(E6), WHITE_KING.addTo(E2)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).containsExactlyInAnyOrder(E2, E3, E4, E5);
	}

	@Test
	void fieldsBehindBlackPassedPawnEndAtFirstNonEmptySquare() {
		board.process(new Move(BLACK, BLACK_PAWN.addTo(H3), WHITE_KING.addTo(H7)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).containsExactlyInAnyOrder(H4, H5, H6, H7);
	}

	@Test
	void fieldsBehindPassedPawnReturnsEmptyListForBlackPawnWhichIsNotPassed() {
		board.process(new Move(WHITE, BLACK_PAWN.addTo(E4), WHITE_PAWN.addTo(D2)));

		List<Square> fieldsBehindPassedPawn = ALL.stream()
				.filter(square -> board.isBehindPassedPawn(square))
				.collect(toList());
		assertThat(fieldsBehindPassedPawn).isEmpty();
	}

	@Test
	void boardWithoutSetupIsNotStartedFromInitialSetup() {
		assertThat(board.gameStartedFromInitialSetup()).isFalse();
	}

	@Test
	void boardWithInitialSetupIsStartedFromInitialSetup() {
		board.initBoard();
		assertThat(board.gameStartedFromInitialSetup()).isTrue();
	}

	@Test
	void boardWithMovesFollowingInitialSetupIsStartedFromInitialSetup() {
		Move move1 = new Move(INITIAL_BOARD, WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		Move move2 = new Move(move1, BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		board.process(move2);

		assertThat(board.gameStartedFromInitialSetup()).isTrue();
	}

	@Test
	void boardWithMovesFollowingDeviatingSetupIsNotStartedFromInitialSetup() {
		Move deviatingSetup = new Move(BLACK, WHITE_PAWN.addTo(E2), BLACK_PAWN.addTo(E7));
		Move move1 = new Move(deviatingSetup, WHITE_PAWN.removeFrom(E2), WHITE_PAWN.addTo(E4));
		Move move2 = new Move(move1, BLACK_PAWN.removeFrom(E7), BLACK_PAWN.addTo(E5));
		board.process(move2);

		assertThat(board.gameStartedFromInitialSetup()).isFalse();
	}

	private Move find(List<Move> moves, String moveDescription) {
		MoveNotation moveNotation = new ReverseAlgebraicNotation();
		return moves.stream()
				.filter(m -> moveNotation.format(m).equals(moveDescription))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("could not find move " + moveDescription + " in moves " + moves));
	}

}