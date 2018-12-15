package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Move;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.assertThat;

class PositionalAnalysisForEndGameTest {

    private PositionalAnalysisForEndGame positionalAnalysisForendgame = new PositionalAnalysisForEndGame(
            new PassedPawnFieldHeuristic(),
            new CentralControlHeuristic(),
            new KingFieldHeuristic(),
            new PawnHeuristicsEndgame());

    private SingleMoveEvaluator evaluator = new SingleMoveEvaluator((board, move) -> positionalAnalysisForendgame.evaluate(board, asList(move)));

    @Test
    void whiteKingMovingToCenterScoresBetterThanWhiteKingMovingAwayFromCenter() {
        int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(H8)),
                WHITE_KING.removeFrom(A1), WHITE_KING.addTo(B2));

        int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(B2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(H8)),
                WHITE_KING.removeFrom(B2), WHITE_KING.addTo(A1));

        assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
    }

    @Test
    void blackKingMovingToCenterScoresBetterThanBlackKingMovingAwayFromCenter() {
        int towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_PAWN.addTo(H7), WHITE_KING.addTo(H8)),
                BLACK_KING.removeFrom(A1), BLACK_KING.addTo(B2));

        int awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_PAWN.addTo(H7), WHITE_KING.addTo(H8)),
                BLACK_KING.removeFrom(B2), BLACK_KING.addTo(A1));

        assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
    }

    @Test
    void whiteKingMovingTowardsBlackKingScoresBetterThanWhiteKingMovingAwayFromBlackKing() {
        int towardsOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(F4)),
                WHITE_KING.removeFrom(E2), WHITE_KING.addTo(E3));

        int awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(F4)),
                WHITE_KING.removeFrom(E3), WHITE_KING.addTo(E2));

        assertThat(towardsOpponentsKingValue).isGreaterThan(awayFromOpponentsKingValue);
    }

    @Test
    void blackKingMovingTowardsWhiteKingScoresBetterThanBlackKingMovingAwayFromWhiteKing() {
        int towardsOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E2), BLACK_PAWN.addTo(H2), WHITE_KING.addTo(F4)),
                BLACK_KING.removeFrom(E2), BLACK_KING.addTo(E3));

        int awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E2), BLACK_PAWN.addTo(H2), WHITE_KING.addTo(F4)),
                BLACK_KING.removeFrom(E3), BLACK_KING.addTo(E2));

        assertThat(towardsOpponentsKingValue).isLessThan(awayFromOpponentsKingValue);
    }

    @Test
    void unopposedWhitePawnOnHigherRankScoresBetterThanUnopposedWhitePawnOnLowerRank() {
        int pawnOnHigherRank = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E7), WHITE_QUEEN.addTo(E8));

        int pawnOnLowerRank = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E6), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E6), WHITE_QUEEN.addTo(E7));

        assertThat(pawnOnHigherRank).isGreaterThan(pawnOnLowerRank);
    }

    @Test
    void unopposedBlackPawnOnLowerRankScoresBetterThanuUopposedBlackPawnOnHigherRank() {
        int pawnOnLowerRank = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E2), BLACK_KNIGHT.addTo(E1));

        int pawnOnHigherRank = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E3), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E3), BLACK_PAWN.addTo(E2));

        assertThat(pawnOnLowerRank).isLessThan(pawnOnHigherRank);
    }

    @Test
    void unopposedWhitePawnScoresBetterThanOpposedWhitePawnOnSameRank() {
        int unopposedPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

        int opposedPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

        assertThat(unopposedPawn).isGreaterThan(opposedPawn);
    }

    @Test
    void unopposedBlackPawnScoresBetterThanOpposedBlackPawnOnSameRank() {
        int unopposedPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E4), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

        int opposedPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E5), WHITE_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

        assertThat(unopposedPawn).isLessThan(opposedPawn);
    }

    @Test
    void movingSecondWhitePawnOnFileGetsPenalty() {
        int singlePawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

        int secondPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), WHITE_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

        assertThat(singlePawn).isGreaterThan(secondPawn);
    }

    @Test
    void movingSecondBlackPawnOnFileGetsPenalty() {
        int singlePawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E4), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

        int secondPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E5), BLACK_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

        assertThat(singlePawn).isLessThan(secondPawn);
    }

}