package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import java.lang.reflect.*;
import java.util.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

abstract class PositionalAnalysisForEndGameTest {

    final PieceValueEvaluator pieceValueEvaluator = new PieceValueEvaluator();

    private PositionalAnalysisForEndGame positionalAnalysisForendgame = new PositionalAnalysisForEndGame(
            new PassedPawnFieldHeuristic(),
            new CentralControlHeuristic(),
            new KingFieldHeuristic(),
            new PawnHeuristicsEndgame(),
            pieceValueEvaluator,
            new EndOfGameBoardEvaluator());

    SingleMoveEvaluator evaluator = SingleMoveEvaluator.forConsumer((board, move) -> positionalAnalysisForendgame.evaluate(board, asList(move)));
    SingleMoveEvaluator rookBehindPawnEvaluator = SingleMoveEvaluator.forFunction((board, move) -> {
        int score = positionalAnalysisForendgame.getDeltaForRookPlacedBehindPassedPawn(
                board,
                move.getMutationRemovingPieceFromStart(),
                move.getMutationAddingPieceAtDestination());
        return new MoveValue(score, move);
    });

    static class EndgameWithPawnsTest extends PositionalAnalysisForEndGameTest {

        @Test
        void whiteKingMovingToCenterScoresBetterThanWhiteKingMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(A1), WHITE_KING.addTo(B2));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(B2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(B2), WHITE_KING.addTo(A1));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void blackKingMovingToCenterScoresBetterThanBlackKingMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_PAWN.addTo(H7), WHITE_KING.addTo(H8)),
                    BLACK_KING.removeFrom(A1), BLACK_KING.addTo(B2));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_PAWN.addTo(H7), WHITE_KING.addTo(H8)),
                    BLACK_KING.removeFrom(B2), BLACK_KING.addTo(A1));

            assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
        }

        @Test
        void whiteKingMovingTowardsBlackKingScoresBetterThanWhiteKingMovingAwayFromBlackKing() {
            MoveValue towardsOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(F4)),
                    WHITE_KING.removeFrom(E2), WHITE_KING.addTo(E3));

            MoveValue awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E2), WHITE_PAWN.addTo(H2), BLACK_KING.addTo(F4)),
                    WHITE_KING.removeFrom(E3), WHITE_KING.addTo(E2));

            assertThat(towardsOpponentsKingValue).isGreaterThan(awayFromOpponentsKingValue);
        }

        @Test
        void blackKingMovingTowardsWhiteKingScoresBetterThanBlackKingMovingAwayFromWhiteKing() {
            MoveValue towardsOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E2), BLACK_PAWN.addTo(H2), WHITE_KING.addTo(F4)),
                    BLACK_KING.removeFrom(E2), BLACK_KING.addTo(E3));

            MoveValue awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E2), BLACK_PAWN.addTo(H2), WHITE_KING.addTo(F4)),
                    BLACK_KING.removeFrom(E3), BLACK_KING.addTo(E2));

            assertThat(towardsOpponentsKingValue).isLessThan(awayFromOpponentsKingValue);
        }

        @Test
        void unopposedWhitePawnOnHigherRankScoresBetterThanUnopposedWhitePawnOnLowerRank() {
            MoveValue pawnOnHigherRank = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E7), WHITE_QUEEN.addTo(E8));

            MoveValue pawnOnLowerRank = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E6), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E6), WHITE_QUEEN.addTo(E7));

            assertThat(pawnOnHigherRank).isGreaterThan(pawnOnLowerRank);
        }

        @Test
        void unopposedBlackPawnOnLowerRankScoresBetterThanUnopposedBlackPawnOnHigherRank() {
            MoveValue pawnOnLowerRank = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E2), BLACK_KNIGHT.addTo(E1));

            MoveValue pawnOnHigherRank = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E3), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E3), BLACK_PAWN.addTo(E2));

            assertThat(pawnOnLowerRank).isLessThan(pawnOnHigherRank);
        }

        @Test
        void unopposedWhitePawnScoresBetterThanOpposedWhitePawnOnSameRank() {
            MoveValue unopposedPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

            MoveValue opposedPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

            assertThat(unopposedPawn).isGreaterThan(opposedPawn);
        }

        @Test
        void unopposedBlackPawnScoresBetterThanOpposedBlackPawnOnSameRank() {
            MoveValue unopposedPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E4), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

            MoveValue opposedPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E5), WHITE_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

            assertThat(unopposedPawn).isLessThan(opposedPawn);
        }

        @Test
        void movingSecondWhitePawnOnFileGetsPenalty() {
            MoveValue singlePawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

            MoveValue secondPawn = evaluator.valueOfMove(new Move(BLACK, WHITE_PAWN.addTo(E5), WHITE_PAWN.addTo(E7), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    WHITE_PAWN.removeFrom(E5), WHITE_PAWN.addTo(E6));

            assertThat(singlePawn).isGreaterThan(secondPawn);
        }

        @Test
        void movingSecondBlackPawnOnFileGetsPenalty() {
            MoveValue singlePawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E4), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

            MoveValue secondPawn = evaluator.valueOfMove(new Move(WHITE, BLACK_PAWN.addTo(E5), BLACK_PAWN.addTo(E2), BLACK_KING.addTo(A1), WHITE_KING.addTo(H8)),
                    BLACK_PAWN.removeFrom(E4), BLACK_PAWN.addTo(E3));

            assertThat(singlePawn).isLessThan(secondPawn);
        }

    }

    static class GeneralEndgameTest extends PositionalAnalysisForEndGameTest {

        @Test
        void endgameWithThreeWhiteAndThreeBlackPawnsAndPiecesRaisesValueOfAllPawnsTo120() throws Exception {
            evaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(A2), WHITE_PAWN.addTo(B2), WHITE_PAWN.addTo(B3),
                            BLACK_PAWN.addTo(A7), BLACK_PAWN.addTo(B7), BLACK_PAWN.addTo(C7),
                            BLACK_KING.addTo(H8), WHITE_KING.addTo(H1),
                            WHITE_ROOK.addTo(A1)),
                    BLACK_PAWN.removeFrom(A7), BLACK_PAWN.addTo(A5));

            assertThat(getWhitePieceToValue()).containsEntry(PAWN, 120);
            assertThat(getBlackPieceToValue()).containsEntry(PAWN, 120);
        }

        @Test
        void endgameWithThreeWhitePawnsAndTwoBlackPawnsAndPiecesRaisesValueOfWhitePawnsTo120AndBlackPawnsTo190() throws Exception {
            evaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(A2), WHITE_PAWN.addTo(B2), WHITE_PAWN.addTo(B3),
                            BLACK_PAWN.addTo(A7), BLACK_PAWN.addTo(B7),
                            BLACK_KING.addTo(H8), WHITE_KING.addTo(H1),
                            WHITE_ROOK.addTo(A1)),
                    BLACK_PAWN.removeFrom(A7), BLACK_PAWN.addTo(A5));

            assertThat(getWhitePieceToValue()).containsEntry(PAWN, 120);
            assertThat(getBlackPieceToValue()).containsEntry(PAWN, 190);
        }

        @Test
        void endgameWithOneWhitePawnsAndThreeBlackPawnsAndPiecesRaisesValueOfWhitePawnsTo190AndBlackPawnsTo120() throws Exception {
            evaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(A2),
                            BLACK_PAWN.addTo(A7), BLACK_PAWN.addTo(B7), BLACK_PAWN.addTo(C7),
                            BLACK_KING.addTo(H8), WHITE_KING.addTo(H1),
                            WHITE_ROOK.addTo(A1)),
                    BLACK_PAWN.removeFrom(A7), BLACK_PAWN.addTo(A5));

            assertThat(getWhitePieceToValue()).containsEntry(PAWN, 190);
            assertThat(getBlackPieceToValue()).containsEntry(PAWN, 120);
        }

        @Test
        void endgameWithOneWhitePawnsAndThreeBlackPawnsAndWithoutPiecesDoesNotRaiseValueOfPawns() throws Exception {
            evaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(A2),
                            BLACK_PAWN.addTo(A7), BLACK_PAWN.addTo(B7), BLACK_PAWN.addTo(C7),
                            BLACK_KING.addTo(H8), WHITE_KING.addTo(H1)),
                    BLACK_PAWN.removeFrom(A7), BLACK_PAWN.addTo(A5));

            assertThat(getWhitePieceToValue()).containsEntry(PAWN, 100);
            assertThat(getBlackPieceToValue()).containsEntry(PAWN, 100);
        }

        @Test
        void whiteRookMovesBehindWhitePawnAdds15Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A1)),
                    WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(C1));
            assertThat(value).isEqualTo(forWhite(15));
        }

        @Test
        void whiteRookMovesBehindBlackPawnAdds15Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            BLACK_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A8)),
                    WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(C8));
            assertThat(value).isEqualTo(forWhite(15));
        }

        @Test
        void whiteRookThatWasAlreadyBehindWhitePawnMovesBehindWhitePawnAdds0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(C1)),
                    WHITE_ROOK.removeFrom(C1), WHITE_ROOK.addTo(C2));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void whiteRookThatWasAlreadyBehindWhitePawnMovesAwayFromBehindWhitePawnAdds0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(C1)),
                    WHITE_ROOK.removeFrom(C1), WHITE_ROOK.addTo(D1));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void whiteRookMovesInFrontOfWhitePawnAdds0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A8)),
                    WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(C8));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void whiteRookMovesInFrontOfBlackPawnAdds0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            BLACK_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A1)),
                    WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(C1));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void whiteQueenMovesBehindWhitePawnAdds0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_QUEEN.addTo(A1)),
                    WHITE_QUEEN.removeFrom(A1), WHITE_QUEEN.addTo(C1));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void blackRookMovesBehindBlackPawnSubtracts15Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A8)),
                    BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(C8));
            assertThat(value).isEqualTo(forBlack(15));
        }

        @Test
        void blackRookMovesBehindWhitePawnSubtracts15Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A1)),
                    BLACK_ROOK.removeFrom(A1), BLACK_ROOK.addTo(C1));
            assertThat(value).isEqualTo(forBlack(15));
        }

        @Test
        void blackRookThatWasAlreadyBehindBlackPawnMovesBehindBlackPawnSubtracts0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(C8)),
                    BLACK_ROOK.removeFrom(C8), BLACK_ROOK.addTo(C7));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void blackRookThatWasAlreadyBehindBlackPawnMovesAwayFromBehindBlackPawnSubtracts0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(C8)),
                    BLACK_ROOK.removeFrom(C8), BLACK_ROOK.addTo(D8));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void blackRookMovesInFrontOfBlackPawnSubtracts0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A1)),
                    BLACK_ROOK.removeFrom(A1), BLACK_ROOK.addTo(C1));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void blackRookMovesInFrontOfWhitePawnSubtracts0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A8)),
                    BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(C8));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void blackQueenMovesBehindBlackPawnSubtracts0Points() {
            MoveValue value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_QUEEN.addTo(A8)),
                    BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(C8));
            assertThat(value).isEqualTo(ZERO);
        }

        @Test
        void whiteKnightMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KNIGHT.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KNIGHT.removeFrom(B2), WHITE_KNIGHT.addTo(C4));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KNIGHT.addTo(C4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KNIGHT.removeFrom(C4), WHITE_KNIGHT.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void whiteBishopMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(B2), WHITE_BISHOP.addTo(D4));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(D4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(D4), WHITE_BISHOP.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void blackRookMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(B2), WHITE_BISHOP.addTo(D4));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(D4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(D4), WHITE_BISHOP.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void blackQueenMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(A8), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(E4));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(E4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    BLACK_QUEEN.removeFrom(E4), BLACK_QUEEN.addTo(A8));

            assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
        }

        @Test
        void whiteKingMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(A1), WHITE_KING.addTo(B2));

            MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(B2), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(B2), WHITE_KING.addTo(A1));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void whiteKingMovingTowardsBlackKingScoresBetterThanWhiteKingMovingAwayFromBlackKing() {
            MoveValue towardsOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(D4), WHITE_PAWN.addTo(H2), WHITE_KNIGHT.addTo(A8), BLACK_KING.addTo(G2)),
                    WHITE_KING.removeFrom(D4), WHITE_KING.addTo(E4));

            MoveValue awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E4), WHITE_PAWN.addTo(H2), WHITE_KNIGHT.addTo(A8), BLACK_KING.addTo(G2)),
                    WHITE_KING.removeFrom(E4), WHITE_KING.addTo(D4));

            assertThat(towardsOpponentsKingValue).isGreaterThan(awayFromOpponentsKingValue);
        }

        @Test
        void blackKingMovingTowardsWhiteKingScoresBetterThanBlackKingMovingAwayFromWhiteKing() {
            MoveValue towardsOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(D4), BLACK_PAWN.addTo(H2), BLACK_KNIGHT.addTo(A8), WHITE_KING.addTo(G2)),
                    BLACK_KING.removeFrom(D4), BLACK_KING.addTo(E4));

            MoveValue awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E4), BLACK_PAWN.addTo(H2), BLACK_KNIGHT.addTo(A8), WHITE_KING.addTo(F4)),
                    BLACK_KING.removeFrom(E4), BLACK_KING.addTo(D4));

            assertThat(towardsOpponentsKingValue).isLessThan(awayFromOpponentsKingValue);
        }

        @Test
        void bigMobilityForWhiteRookScoresBetterThanSmallMobility() {
            MoveValue bigMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(B2), WHITE_PAWN.addTo(C7)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

            MoveValue smallMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(A2), WHITE_PAWN.addTo(C7)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

            assertThat(bigMobility).isGreaterThan(smallMobility);
        }

        @Test
        void bigMobilityForBlackQueenScoresBetterThanSmallMobility() {
            MoveValue bigMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_KING.addTo(A7), WHITE_PAWN.addTo(C7)),
                    BLACK_QUEEN.removeFrom(B1), BLACK_QUEEN.addTo(A1));

            MoveValue smallMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_KING.addTo(A2), WHITE_PAWN.addTo(C7)),
                    BLACK_QUEEN.removeFrom(B1), BLACK_QUEEN.addTo(A1));
            assertThat(bigMobility).isLessThan(smallMobility);
        }

        private Map<Piece, Integer> getWhitePieceToValue() throws IllegalAccessException, NoSuchFieldException {
            String whitePieceToValue = "whitePieceToValue";
            return getPieceToValue(whitePieceToValue);
        }

        private Map<Piece, Integer> getBlackPieceToValue() throws IllegalAccessException, NoSuchFieldException {
            String whitePieceToValue = "blackPieceToValue";
            return getPieceToValue(whitePieceToValue);
        }

        private Map<Piece, Integer> getPieceToValue(String nameOfField) throws IllegalAccessException, NoSuchFieldException {
            Field field = PieceValueEvaluator.class.getDeclaredField(nameOfField);
            field.setAccessible(true);
            //noinspection unchecked
            return (Map<Piece, Integer>) field.get(pieceValueEvaluator);
        }
    }

    static class EndgameWithPiecesTest extends PositionalAnalysisForEndGameTest {

        @Test
        void whiteMoveThatForcesOpponentsKingToEdgeScoresBetterThanMoveThatDoesNotForceOpponentsKingToEdge() {
            MoveValue forcesOpponentKingToEdge = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), WHITE_ROOK.addTo(A5), BLACK_KING.addTo(E6), WHITE_KING.addTo(A1)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(B6));

            MoveValue doesNotForcesOpponentKingToEdge = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), WHITE_ROOK.addTo(A5), BLACK_KING.addTo(E6), WHITE_KING.addTo(A1)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(B5));

            assertThat(forcesOpponentKingToEdge).isGreaterThan(doesNotForcesOpponentKingToEdge);
        }

        @Test
        void blackMoveThatForcesOpponentsKingToEdgeScoresBetterThanMoveThatDoesNotForceOpponentsKingToEdge() {
            MoveValue forcesOpponentKingToEdge = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), BLACK_ROOK.addTo(A5), WHITE_KING.addTo(E6), BLACK_KING.addTo(A1)),
                    BLACK_ROOK.removeFrom(B1), BLACK_ROOK.addTo(B6));

            MoveValue doesNotForcesOpponentKingToEdge = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), BLACK_ROOK.addTo(A5), WHITE_KING.addTo(E6), BLACK_KING.addTo(A1)),
                    BLACK_ROOK.removeFrom(B1), BLACK_ROOK.addTo(B5));

            assertThat(forcesOpponentKingToEdge).isLessThan(doesNotForcesOpponentKingToEdge);
        }

        @Test
        void whiteMoveThatForcesOpponentsKingNearOwnKingScoresBetterThanMoveThatDoesNotForceOpponentsKingNearOwnKing() {
            MoveValue forcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(E5)),
                    WHITE_KING.removeFrom(E5), WHITE_KING.addTo(F5));

            MoveValue doesNotForcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(E5)),
                    WHITE_KING.removeFrom(E5), WHITE_KING.addTo(D4));

            assertThat(forcesOpponentKingNearOwnKing).isGreaterThan(doesNotForcesOpponentKingNearOwnKing);
        }

        @Test
        void blackMoveThatForcesOpponentsKingNearOwnKingScoresBetterThanMoveThatDoesNotForceOpponentsKingNearOwnKing() {
            MoveValue forcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8), BLACK_KING.addTo(E5)),
                    BLACK_KING.removeFrom(E5), BLACK_KING.addTo(F5));

            MoveValue doesNotForcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8), BLACK_KING.addTo(E5)),
                    BLACK_KING.removeFrom(E5), BLACK_KING.addTo(D4));

            assertThat(forcesOpponentKingNearOwnKing).isLessThan(doesNotForcesOpponentKingNearOwnKing);
        }

        @Test
        void whiteKingMovingTowardsCenterScoresBetterThanKingMovingAwayFromCenter() {
            MoveValue kingMovingToCenter = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(B2), WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(B2), WHITE_KING.addTo(C3));

            MoveValue kingMovingAwayFromCenter = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(C3), WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(C3), WHITE_KING.addTo(B2));

            assertThat(kingMovingToCenter).isGreaterThan(kingMovingAwayFromCenter);
        }

        @Test
        void blackKingMovingTowardsCenterScoresBetterThanKingMovingAwayFromCenter() {
            MoveValue kingMovingToCenter = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(B2), BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8)),
                    BLACK_KING.removeFrom(B2), BLACK_KING.addTo(C3));

            MoveValue kingMovingAwayFromCenter = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(C3), BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8)),
                    BLACK_KING.removeFrom(C3), BLACK_KING.addTo(B2));

            assertThat(kingMovingToCenter).isLessThan(kingMovingAwayFromCenter);
        }

        @Test
        void whiteRookMovingTowardsOwnKingScoresBetterThanMovingAwayFromOwnKing() {
            MoveValue kingMovingToCenter = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_ROOK.addTo(H1), BLACK_KING.addTo(H8)),
                    WHITE_ROOK.removeFrom(H1), WHITE_ROOK.addTo(B1));

            MoveValue kingMovingAwayFromCenter = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(H1));

            assertThat(kingMovingToCenter).isGreaterThan(kingMovingAwayFromCenter);
        }

        @Test
        void blackRookMovingTowardsOwnKingScoresBetterThanMovingAwayFromOwnKing() {
            MoveValue kingMovingToCenter = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_ROOK.addTo(H1), WHITE_KING.addTo(H8)),
                    BLACK_ROOK.removeFrom(H1), BLACK_ROOK.addTo(B1));

            MoveValue kingMovingAwayFromCenter = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(A1), BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8)),
                    BLACK_ROOK.removeFrom(B1), BLACK_ROOK.addTo(H1));

            assertThat(kingMovingToCenter).isLessThan(kingMovingAwayFromCenter);
        }
    }
}