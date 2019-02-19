package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

class PieceValueEvaluatorTest {

	private final PieceValueEvaluator pieceValueEvaluator = new PieceValueEvaluator();
	private final Board board = new Board();

	@Test
	void testEvaluation() {
		assertThatValueAfterMoveEquals(new MoveValue(15000, 0), WHITE_KING);
		assertThatValueAfterMoveEquals(new MoveValue(0, 15000), BLACK_KING);
		assertThatValueAfterMoveEquals(new MoveValue(900, 0), WHITE_QUEEN);
		assertThatValueAfterMoveEquals(new MoveValue(0, 900), BLACK_QUEEN);
		assertThatValueAfterMoveEquals(new MoveValue(500, 0), WHITE_ROOK);
		assertThatValueAfterMoveEquals(new MoveValue(0, 500), BLACK_ROOK);
		assertThatValueAfterMoveEquals(new MoveValue(330, 0), WHITE_BISHOP);
		assertThatValueAfterMoveEquals(new MoveValue(0, 330), BLACK_BISHOP);
		assertThatValueAfterMoveEquals(new MoveValue(330, 0), WHITE_KNIGHT);
		assertThatValueAfterMoveEquals(new MoveValue(0, 330), BLACK_KNIGHT);
		assertThatValueAfterMoveEquals(new MoveValue(100, 0), WHITE_PAWN);
		assertThatValueAfterMoveEquals(new MoveValue(0, 100), BLACK_PAWN);

		assertThatValueAfterMoveEquals(new MoveValue(15000, 15000), WHITE_KING, BLACK_KING);
		assertThatValueAfterMoveEquals(new MoveValue(15900, 15000), WHITE_KING, WHITE_QUEEN, BLACK_KING);
		assertThatValueAfterMoveEquals(new MoveValue(3220, 0), WHITE_QUEEN, WHITE_ROOK, WHITE_ROOK, WHITE_BISHOP, WHITE_BISHOP, WHITE_KNIGHT, WHITE_KNIGHT);
	}

	private void assertThatValueAfterMoveEquals(MoveValue expectedValue, PlayerPiece... playerPieces) {
		Move move = buildMovesToAddPieces(playerPieces);
		board.process(move);
		MoveValue actualValue = pieceValueEvaluator.value(board);
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