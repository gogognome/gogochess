package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.game.Player.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.piece.*;

public class BoardMutationTest {

	@Test
	public void testEquals() {
		assertEquals(
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD),
				new BoardMutation(new Pawn(BLACK), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD),
				new BoardMutation(new Knight(WHITE), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("A3"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("A2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("A2"), REMOVE));
	}
}
