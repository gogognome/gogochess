package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.Status.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.movenotation.*;

class MoveNotationTest {

	private final MoveNotation moveNotation = new ReverseAlgebraicNotation();

	@Test
	void format_null_throwsException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> moveNotation.format(null))
				.withMessage("Move must not be null");
	}

	@Test
	void formatPawnMove() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_PAWN.removeFrom(Squares.E2), WHITE_PAWN.addTo(E4))))
				.isEqualTo("e2-e4");
	}

	@Test
	void formatKnightMove() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_KNIGHT.removeFrom(Squares.B1), WHITE_KNIGHT.addTo(C3))))
				.isEqualTo("Nb1-c3");
	}

	@Test
	void formatBishopMove() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_BISHOP.removeFrom(Squares.C8), BLACK_BISHOP.addTo(E6))))
				.isEqualTo("Bc8-e6");
	}

	@Test
	void formatRookMove() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_ROOK.removeFrom(Squares.A8), BLACK_BISHOP.addTo(E8))))
				.isEqualTo("Ra8-e8");
	}

	@Test
	void formatQueenMove() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_QUEEN.removeFrom(Squares.D1), WHITE_QUEEN.addTo(A4))))
				.isEqualTo("Qd1-a4");
	}

	@Test
	void formatKingMove() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_KING.removeFrom(Squares.E1), WHITE_KING.addTo(E2))))
				.isEqualTo("Ke1-e2");
	}

	@Test
	void formatPawnCapturingAnotherPawn() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_PAWN.removeFrom(Squares.D5), WHITE_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E4))))
				.isEqualTo("d5xe4");
	}

	@Test
	void formatPawnCapturingAnotherPawnEnPassent() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_PAWN.removeFrom(Squares.D5), WHITE_PAWN.removeFrom(E5), BLACK_PAWN.addTo(E4))))
				.isEqualTo("d5xe4");
	}

	@Test
	void formatRookCapturingQueen() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_ROOK.removeFrom(Squares.F3), BLACK_QUEEN.removeFrom(F7), WHITE_ROOK.addTo(F7))))
				.isEqualTo("Rf3xQf7");
	}

	@Test
	void formatWhitePawnPromotingToQueen() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_PAWN.removeFrom(Squares.A7), WHITE_QUEEN.addTo(A8))))
				.isEqualTo("a7-a8(Q)");
	}

	@Test
	void formatBlakPawnPromotingToKnight() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_PAWN.removeFrom(Squares.G2), BLACK_KNIGHT.addTo(G1))))
				.isEqualTo("g2-g1(N)");
	}

	@Test
	void formatShortCastlingMove() {
		assertThat(moveNotation.format(new Move(WHITE, WHITE_KING.removeFrom(Squares.E1), WHITE_ROOK.removeFrom(H1), WHITE_KING.addTo(G1), WHITE_ROOK.addTo(F1))))
				.isEqualTo("O-O");
	}

	@Test
	void formatLongCastlingMove() {
		assertThat(moveNotation.format(new Move(BLACK, BLACK_ROOK.removeFrom(A8), BLACK_KING.removeFrom(Squares.E8), BLACK_ROOK.addTo(C8), BLACK_KING.addTo(B8))))
				.isEqualTo("O-O-O");
	}

	@Test
	void formatMoveResultingInCheck() {
		Move move = new Move(WHITE, WHITE_BISHOP.removeFrom(Squares.F4), WHITE_BISHOP.addTo(H6));
		move.setStatus(CHECK);
		assertThat(moveNotation.format(move))
				.isEqualTo("Bf4-h6+");
	}

	@Test
	void formatMoveResultingInCheckMate() {
		Move move = new Move(WHITE, WHITE_BISHOP.removeFrom(Squares.F4), WHITE_BISHOP.addTo(H6));
		move.setStatus(CHECK_MATE);
		assertThat(moveNotation.format(move))
				.isEqualTo("Bf4-h6++");
	}

	@Test
	void formatMoveResultingInStaleMate() {
		Move move = new Move(WHITE, WHITE_BISHOP.removeFrom(Squares.F4), WHITE_BISHOP.addTo(H6));
		move.setStatus(STALE_MATE);
		assertThat(moveNotation.format(move))
				.isEqualTo("Bf4-h6");
	}

	@Test
	void formatShortCastlingMoveResultingIntoCheck() {
		Move move = new Move(WHITE, WHITE_KING.removeFrom(Squares.E1), WHITE_ROOK.removeFrom(H1), WHITE_KING.addTo(G1), WHITE_ROOK.addTo(F1));
		move.setStatus(CHECK);
		assertThat(moveNotation.format(move))
				.isEqualTo("O-O+");
	}


	@Test
	void formatLongCastlingMoveResultingIntoCheckMate() {
		Move move = new Move(BLACK, BLACK_ROOK.removeFrom(A8), BLACK_KING.removeFrom(Squares.E8), BLACK_ROOK.addTo(C8), BLACK_KING.addTo(B8));
		move.setStatus(CHECK_MATE);
		assertThat(moveNotation.format(move))
				.isEqualTo("O-O-O++");
	}

}