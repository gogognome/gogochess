package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;

class PassedPawnFieldHeuristicTest {

    private PassedPawnFieldHeuristic passedPawnFieldHeuristic = new PassedPawnFieldHeuristic();

    private SingleMoveEvaluator evaluator = SingleMoveEvaluator.forConsumer((board, move) ->
            move.setValue(new MoveValue(passedPawnFieldHeuristic.getDeltaForPassedPawns(board, move), move)));

    @Test
    void blackBishopMovesOutsideOfPassedPawnField_deltaIsZero() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(A1), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(A1), BLACK_BISHOP.addTo(B2));
        assertThat(delta).isEqualTo(ZERO);
    }

    @Test
    void blackBishopMovesFromOutsideToInsideOfPassedPawnField_deltaIsPositive() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(C8), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(C8), BLACK_BISHOP.addTo(G4));
        assertThat(delta).isEqualTo(forBlack(5));
    }

    @Test
    void blackBishopMovesFromInsideToOutsideOfPassedPawnField_deltaIsPositive() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(G4), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(G4), BLACK_BISHOP.addTo(C8));
        assertThat(delta).isEqualTo(forBlack(-5));
    }

    @Test
    void whitePassedPawnMovesSuchThatBlackRookEntersField_deltaIsNegative() {
        MoveValue delta = evaluator.valueOfMove(new Move(BLACK, BLACK_ROOK.addTo(D8), WHITE_PAWN.addTo(F4)),
                WHITE_PAWN.removeFrom(F4), WHITE_PAWN.addTo(F5));
        assertThat(delta).isEqualTo(forWhite(-1));
    }

    @Test
    void blackPassedPawnMovesSuchThatWhiteBishopEntersField_deltaIsNegative() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, WHITE_ROOK.addTo(D1), BLACK_PAWN.addTo(F5)),
                BLACK_PAWN.removeFrom(F5), BLACK_PAWN.addTo(F4));
        assertThat(delta).isEqualTo(forBlack(-1));
    }

    @Test
    void blackPassedPawnMovesSuchThatWhiteQueenEntersField_deltaIsNegative() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, WHITE_QUEEN.addTo(A1), BLACK_PAWN.addTo(B5)),
                BLACK_PAWN.removeFrom(B5), BLACK_PAWN.addTo(B4));
        assertThat(delta).isEqualTo(forBlack(-2));
    }

    @Test
    void blackKnightMovesIntoFieldOfOwnWhitePassedPawn_deltaTakesBothFieldsIntoAccount() {
        MoveValue delta = evaluator.valueOfMove(new Move(WHITE, BLACK_KNIGHT.addTo(C6), BLACK_PAWN.addTo(C5), WHITE_PAWN.addTo(E3)),
                BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(D4));
        assertThat(delta).isEqualTo(forBlack(5 + 5 -1));
    }

    @Test
    void whitePassedPawnMovesAndWhiteKingIsOutsideOfField() {
        MoveValue delta = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(A2), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(A2), WHITE_PAWN.addTo(A3));
        assertThat(delta).isEqualTo(ZERO);
    }

}