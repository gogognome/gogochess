package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Move;
import org.junit.jupiter.api.Test;

import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.assertThat;

class PassedPawnFieldHeuristicTest {

    private PassedPawnFieldHeuristic passedPawnFieldHeuristic = new PassedPawnFieldHeuristic();

    private SingleMoveEvaluator evaluator = new SingleMoveEvaluator((board, move) ->
            move.setValue(passedPawnFieldHeuristic.getDeltaForPassedPawns(board, move)));

    @Test
    void blackBishopMovesOutsideOfPassedPawnField_deltaIsZero() {
        int delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(A1), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(A1), BLACK_BISHOP.addTo(B2));
        assertThat(delta).isZero();
    }

    @Test
    void blackBishopMovesFromOutsideToInsideOfPassedPawnField_deltaIsPositive() {
        int delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(C8), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(C8), BLACK_BISHOP.addTo(G4));
        assertThat(delta).isEqualTo(5);
    }

    @Test
    void blackBishopMovesFromInsideToOutsideOfPassedPawnField_deltaIsPositive() {
        int delta = evaluator.valueOfMove(new Move(WHITE, BLACK_BISHOP.addTo(G4), BLACK_PAWN.addTo(H5)),
                BLACK_BISHOP.removeFrom(G4), BLACK_BISHOP.addTo(C8));
        assertThat(delta).isEqualTo(-5);
    }

    @Test
    void whitePassedPawnMovesSuchThatBlackRookEntersField_deltaIsNegative() {
        int delta = evaluator.valueOfMove(new Move(BLACK, BLACK_ROOK.addTo(D8), WHITE_PAWN.addTo(F4)),
                WHITE_PAWN.removeFrom(F4), WHITE_PAWN.addTo(F5));
        assertThat(delta).isEqualTo(-1);
    }

    @Test
    void blackPassedPawnMovesSuchThatWhiteBishopEntersField_deltaIsNegative() {
        int delta = evaluator.valueOfMove(new Move(WHITE, WHITE_ROOK.addTo(D1), BLACK_PAWN.addTo(F5)),
                BLACK_PAWN.removeFrom(F5), BLACK_PAWN.addTo(F4));
        assertThat(delta).isEqualTo(-1);
    }

    @Test
    void blackPassedPawnMovesSuchThatWhiteQueenEntersField_deltaIsNegative() {
        int delta = evaluator.valueOfMove(new Move(WHITE, WHITE_QUEEN.addTo(A1), BLACK_PAWN.addTo(B5)),
                BLACK_PAWN.removeFrom(B5), BLACK_PAWN.addTo(B4));
        assertThat(delta).isEqualTo(-2);
    }

    @Test
    void blackKnightMovesIntoFieldOfOwnWhitePassedPawn_deltaTakesBothFieldsIntoAccount() {
        int delta = evaluator.valueOfMove(new Move(WHITE, BLACK_KNIGHT.addTo(C6), BLACK_PAWN.addTo(C5), WHITE_PAWN.addTo(E3)),
                BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(D4));
        assertThat(delta).isEqualTo(5 + 5 -1);
    }

    @Test
    void whitePassedPawnMovesAndWhiteKingIsOutsideOfField() {
        int delta = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(A2), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(A2), WHITE_PAWN.addTo(A3));
        assertThat(delta).isZero();
    }

}