package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.Piece.KNIGHT;
import static nl.gogognome.gogochess.game.Piece.PAWN;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.piece.*;

class PlayerPieceTest {

	@Test
	void testEqualsAndHashCode() {
		PlayerPiece WHITE_PAWN_1 = new PlayerPiece(WHITE, PAWN);
		PlayerPiece WHITE_PAWN_2 = new PlayerPiece(WHITE, PAWN);
		PlayerPiece BLACK_KNIGHT = new PlayerPiece(BLACK, KNIGHT);

		assertEquals(WHITE_PAWN_1, WHITE_PAWN_1);
		assertEquals(WHITE_PAWN_1, WHITE_PAWN_2);
		assertEquals(WHITE_PAWN_2, WHITE_PAWN_1);
		assertNotEquals(null, WHITE_PAWN_1);
		assertNotEquals(WHITE_PAWN_1, null);
		assertNotEquals(WHITE_PAWN_1, BLACK_KNIGHT);
		assertNotEquals(BLACK_KNIGHT, WHITE_PAWN_1);
		assertNotEquals(WHITE_PAWN_1, "foobar");

		assertEquals(WHITE_PAWN_1.hashCode(), WHITE_PAWN_2.hashCode());
		assertNotEquals(WHITE_PAWN_1.hashCode(), BLACK_KNIGHT.hashCode());
	}

	@Test
	void testToString() {
		assertEquals("white pawn", new PlayerPiece(WHITE, PAWN).toString());
	}
}