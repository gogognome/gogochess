package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Board.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PieceValueEvaluatorTest {

	private final PieceValueEvaluator pieceValueEvaluator = new PieceValueEvaluator();
	private final Board board = new Board();

	@Test
	void testEvaluation() {
		assertThatValueAfterMoveEquals(15000, WHITE_KING);
		assertThatValueAfterMoveEquals(-15000, BLACK_KING);
		assertThatValueAfterMoveEquals(900, WHITE_QUEEN);
		assertThatValueAfterMoveEquals(-900, BLACK_QUEEN);
		assertThatValueAfterMoveEquals(500, WHITE_ROOK);
		assertThatValueAfterMoveEquals(-500, BLACK_ROOK);
		assertThatValueAfterMoveEquals(330, WHITE_BISHOP);
		assertThatValueAfterMoveEquals(-330, BLACK_BISHOP);
		assertThatValueAfterMoveEquals(330, WHITE_KNIGHT);
		assertThatValueAfterMoveEquals(-330, BLACK_KNIGHT);
		assertThatValueAfterMoveEquals(100, WHITE_PAWN);
		assertThatValueAfterMoveEquals(-100, BLACK_PAWN);

		assertThatValueAfterMoveEquals(0, WHITE_KING, BLACK_KING);
		assertThatValueAfterMoveEquals(900, WHITE_KING, WHITE_QUEEN, BLACK_KING);
		assertThatValueAfterMoveEquals(3220, WHITE_QUEEN, WHITE_ROOK, WHITE_ROOK, WHITE_BISHOP, WHITE_BISHOP, WHITE_KNIGHT, WHITE_KNIGHT);
	}

	private void assertThatValueAfterMoveEquals(int expectedValue, PlayerPiece... playerPieces) {
		Move move = buildMovesToAddPieces(playerPieces);
		board.process(move);
		int actualValue = pieceValueEvaluator.value(board);
		assertThat(actualValue).isEqualTo(expectedValue);
	}

	private Move buildMovesToAddPieces(PlayerPiece[] playerPieces) {
		BoardMutation[] mutations = new BoardMutation[playerPieces.length];
		for (int i=0; i<playerPieces.length; i++) {
			mutations[i] = new BoardMutation(playerPieces[i], new Square(i % 8, i / 8), ADD);
		}
		return new Move(WHITE, mutations);
	}
}