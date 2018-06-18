package nl.gogognome.gogochess.logic.piece;

import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class PlayerPieceTest {

	@Test
	void testEqualsAndHashCode() {
		PlayerPiece whitePawn1 = WHITE_PAWN;
		PlayerPiece whitePawn2 = new Pawn(WHITE);
		PlayerPiece blackKnight = BLACK_KNIGHT;

		assertEquals(whitePawn1, whitePawn1);
		assertEquals(whitePawn1, whitePawn2);
		assertEquals(whitePawn2, whitePawn1);
		assertNotEquals(null, whitePawn1);
		assertNotEquals(whitePawn1, null);
		assertNotEquals(whitePawn1, blackKnight);
		assertNotEquals(blackKnight, whitePawn1);
		assertNotEquals(whitePawn1, "foobar");

		assertEquals(whitePawn1.hashCode(), whitePawn2.hashCode());
		assertNotEquals(whitePawn1.hashCode(), blackKnight.hashCode());
	}

	@Test
	void testToString() {
		assertEquals("white pawn", new Pawn(WHITE).toString());
	}
}