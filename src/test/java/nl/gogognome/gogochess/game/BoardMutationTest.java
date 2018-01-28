package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.game.BoardMutation.Mutation.REMOVE;
import static nl.gogognome.gogochess.game.Piece.KNIGHT;
import static nl.gogognome.gogochess.game.Piece.PAWN;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.game.piece.*;

public class BoardMutationTest {

	@Test
	public void testEquals() {
		assertEquals(
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD),
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD),
				new BoardMutation(new PlayerPiece(BLACK, PAWN), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD),
				new BoardMutation(new PlayerPiece(WHITE, KNIGHT), new Square("A2"), ADD));

		assertNotEquals(
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD),
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A3"), ADD));

		assertNotEquals(
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), ADD),
				new BoardMutation(new PlayerPiece(WHITE, PAWN), new Square("A2"), REMOVE));
	}
}
