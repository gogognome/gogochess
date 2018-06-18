package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class MoveTest {

	@Test
	void initalBoard() {
		Board board = new Board();
		board.initBoard();
		String actualBoard = board.toString();

		String boardWithoutEmptySquares = actualBoard.replaceAll("[ *\n]", "");

		assertEquals(
				"RNBQKBNR" +
				"PPPPPPPP" +
				"pppppppp" +
				"rnbqkbnr",
				boardWithoutEmptySquares);
	}

	@Test
	void isCapture_whiteKnightCapturesBlackNight_returnsTrue() {
		Move move = new Move(WHITE,
				WHITE_KNIGHT.removeFrom(E4),
				BLACK_KNIGHT.removeFrom(F6),
				WHITE_KNIGHT.addTo(F6));

		assertThat(move.isCapture()).isTrue();
	}

	@Test
	void isCapture_whiteKnightMoves_returnsFalse() {
		Move move = new Move(WHITE,
				WHITE_KNIGHT.removeFrom(E4),
				WHITE_KNIGHT.addTo(F6));

		assertThat(move.isCapture()).isFalse();
	}

	@Test
	void isCapture_castling_returnsFalse() {
		Move move = new Move(BLACK,
				BLACK_KING.removeFrom(E8),
				BLACK_ROOK.removeFrom(A8),
				BLACK_KING.addTo(B8),
				BLACK_ROOK.addTo(C8));

		assertThat(move.isCapture()).isFalse();
	}
}