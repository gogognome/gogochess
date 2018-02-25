package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.piece.*;

public class BoardMutationTest {

	@Test
	public void testEquals() {
		assertEquals(
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD),
				new BoardMutation(new Pawn(BLACK), new Square("a2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD),
				new BoardMutation(new Knight(WHITE), new Square("a2"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("a3"), ADD));

		assertNotEquals(
				new BoardMutation(new Pawn(WHITE), new Square("a2"), ADD),
				new BoardMutation(new Pawn(WHITE), new Square("a2"), REMOVE));
	}
}
