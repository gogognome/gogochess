package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.Board.*;
import static nl.gogognome.gogochess.game.Piece.*;
import static nl.gogognome.gogochess.game.Player.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.piece.*;

class PlayerPieceTest {

	@Test
	void testEqualsAndHashCode() {
		PlayerPiece whitePawn1 = WHITE_PAWN;
		PlayerPiece whitePawn2 = new PlayerPiece(WHITE, PAWN);
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
		assertEquals("white pawn", new PlayerPiece(WHITE, PAWN).toString());
	}
}