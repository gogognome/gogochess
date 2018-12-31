package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.Piece;
import nl.gogognome.gogochess.logic.ai.EndOfGameBoardEvaluator;
import nl.gogognome.gogochess.logic.ai.PieceValueEvaluator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.Piece.PAWN;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    SingleMoveEvaluator rookBehindPawnEvaluator = SingleMoveEvaluator.forFunction((board, move) ->
            positionalAnalysisForendgame.getDeltaForRookPlacedBehindPassedPawn(
                    board,
                    move.getMutationRemovingPieceFromStart(),
                    move.getMutationAddingPieceAtDestination()));

    static class EndgameWithPawnsTest extends PositionalAnalysisForEndGameTest {

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
        void unopposedBlackPawnOnLowerRankScoresBetterThanUnopposedBlackPawnOnHigherRank() {
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
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A1)),
                    WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(C1));
            assertThat(value).isEqualTo(15);
        }

        @Test
        void whiteRookMovesBehindBlackPawnAdds15Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            BLACK_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A8)),
                    WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(C8));
            assertThat(value).isEqualTo(15);
        }

        @Test
        void whiteRookThatWasAlreadyBehindWhitePawnMovesBehindWhitePawnAdds0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(C1)),
                    WHITE_ROOK.removeFrom(C1), WHITE_ROOK.addTo(C2));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void whiteRookThatWasAlreadyBehindWhitePawnMovesAwayFromBehindWhitePawnAdds0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(C1)),
                    WHITE_ROOK.removeFrom(C1), WHITE_ROOK.addTo(D1));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void whiteRookMovesInFrontOfWhitePawnAdds0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A8)),
                    WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(C8));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void whiteRookMovesInFrontOfBlackPawnAdds0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            BLACK_PAWN.addTo(C3),
                            WHITE_ROOK.addTo(A1)),
                    WHITE_ROOK.removeFrom(A1), WHITE_ROOK.addTo(C1));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void whiteQueenMovesBehindWhitePawnAdds0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(BLACK,
                            WHITE_PAWN.addTo(C3),
                            WHITE_QUEEN.addTo(A1)),
                    WHITE_QUEEN.removeFrom(A1), WHITE_QUEEN.addTo(C1));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void blackRookMovesBehindBlackPawnSubtracts15Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A8)),
                    BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(C8));
            assertThat(value).isEqualTo(15);
        }

        @Test
        void blackRookMovesBehindWhitePawnSubtracts15Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A1)),
                    BLACK_ROOK.removeFrom(A1), BLACK_ROOK.addTo(C1));
            assertThat(value).isEqualTo(15);
        }

        @Test
        void blackRookThatWasAlreadyBehindBlackPawnMovesBehindBlackPawnSubtracts0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(C8)),
                    BLACK_ROOK.removeFrom(C8), BLACK_ROOK.addTo(C7));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void blackRookThatWasAlreadyBehindBlackPawnMovesAwayFromBehindBlackPawnSubtracts0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(C8)),
                    BLACK_ROOK.removeFrom(C8), BLACK_ROOK.addTo(D8));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void blackRookMovesInFrontOfBlackPawnSubtracts0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A1)),
                    BLACK_ROOK.removeFrom(A1), BLACK_ROOK.addTo(C1));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void blackRookMovesInFrontOfWhitePawnSubtracts0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            WHITE_PAWN.addTo(C3),
                            BLACK_ROOK.addTo(A8)),
                    BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(C8));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void blackQueenMovesBehindBlackPawnSubtracts0Points() {
            int value = rookBehindPawnEvaluator.valueOfMove(new Move(WHITE,
                            BLACK_PAWN.addTo(C3),
                            BLACK_QUEEN.addTo(A8)),
                    BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(C8));
            assertThat(value).isEqualTo(0);
        }

        @Test
        void whiteKnightMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KNIGHT.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KNIGHT.removeFrom(B2), WHITE_KNIGHT.addTo(C4));

            int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KNIGHT.addTo(C4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KNIGHT.removeFrom(C4), WHITE_KNIGHT.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void whiteBishopMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(B2), WHITE_BISHOP.addTo(D4));

            int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(D4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(D4), WHITE_BISHOP.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void blackRookMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(B2), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(B2), WHITE_BISHOP.addTo(D4));

            int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_BISHOP.addTo(D4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_BISHOP.removeFrom(D4), WHITE_BISHOP.addTo(B2));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void blackQueenMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            int towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(A8), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(E4));

            int awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(E4), WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    BLACK_QUEEN.removeFrom(E4), BLACK_QUEEN.addTo(A8));

            assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
        }

        @Test
        void whiteKingMovingTowardsCenterScoresBetterThanMovingAwayFromCenter() {
            int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(A1), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(A1), WHITE_KING.addTo(B2));

            int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(B2), WHITE_PAWN.addTo(A2), BLACK_KING.addTo(H8)),
                    WHITE_KING.removeFrom(B2), WHITE_KING.addTo(A1));

            assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
        }

        @Test
        void whiteKingMovingTowardsBlackKingScoresBetterThanWhiteKingMovingAwayFromBlackKing() {
            int towardsOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(D4), WHITE_PAWN.addTo(H2), WHITE_KNIGHT.addTo(A8), BLACK_KING.addTo(G2)),
                    WHITE_KING.removeFrom(D4), WHITE_KING.addTo(E4));

            int awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(BLACK, WHITE_KING.addTo(E4), WHITE_PAWN.addTo(H2), WHITE_KNIGHT.addTo(A8), BLACK_KING.addTo(G2)),
                    WHITE_KING.removeFrom(E4), WHITE_KING.addTo(D4));

            assertThat(towardsOpponentsKingValue).isGreaterThan(awayFromOpponentsKingValue);
        }

        @Test
        void blackKingMovingTowardsWhiteKingScoresBetterThanBlackKingMovingAwayFromWhiteKing() {
            int towardsOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(D4), BLACK_PAWN.addTo(H2), BLACK_KNIGHT.addTo(A8), WHITE_KING.addTo(G2)),
                    BLACK_KING.removeFrom(D4), BLACK_KING.addTo(E4));

            int awayFromOpponentsKingValue = evaluator.valueOfMove(new Move(WHITE, BLACK_KING.addTo(E4), BLACK_PAWN.addTo(H2), BLACK_KNIGHT.addTo(A8), WHITE_KING.addTo(F4)),
                    BLACK_KING.removeFrom(E4), BLACK_KING.addTo(D4));

            assertThat(towardsOpponentsKingValue).isLessThan(awayFromOpponentsKingValue);
        }

        @Test
        void bigMobilityForWhiteRookScoresBetterThanSmallMobility() {
            int bigMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(B2), WHITE_PAWN.addTo(C7)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

            int smallMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(A2), WHITE_PAWN.addTo(C7)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

            assertThat(bigMobility).isGreaterThan(smallMobility);
        }

        @Test
        void bigMobilityForBlackQueenScoresBetterThanSmallMobility() {
            int bigMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_KING.addTo(A7), WHITE_PAWN.addTo(C7)),
                    BLACK_QUEEN.removeFrom(B1), BLACK_QUEEN.addTo(A1));

            int smallMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_KING.addTo(A2), WHITE_PAWN.addTo(C7)),
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
            int forcesOpponentKingToEdge = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), WHITE_ROOK.addTo(A5), BLACK_KING.addTo(E6), WHITE_KING.addTo(A1)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(B6));

            int doesNotForcesOpponentKingToEdge = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), WHITE_ROOK.addTo(A5), BLACK_KING.addTo(E6), WHITE_KING.addTo(A1)),
                    WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(B5));

            assertThat(forcesOpponentKingToEdge).isGreaterThan(doesNotForcesOpponentKingToEdge);
        }

        @Test
        void blackMoveThatForcesOpponentsKingToEdgeScoresBetterThanMoveThatDoesNotForceOpponentsKingToEdge() {
            int forcesOpponentKingToEdge = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), BLACK_ROOK.addTo(A5), WHITE_KING.addTo(E6), BLACK_KING.addTo(A1)),
                    BLACK_ROOK.removeFrom(B1), BLACK_ROOK.addTo(B6));

            int doesNotForcesOpponentKingToEdge = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), BLACK_ROOK.addTo(A5), WHITE_KING.addTo(E6), BLACK_KING.addTo(A1)),
                    BLACK_ROOK.removeFrom(B1), BLACK_ROOK.addTo(B5));

            assertThat(forcesOpponentKingToEdge).isLessThan(doesNotForcesOpponentKingToEdge);
        }

        @Test
        void whiteMoveThatForcesOpponentsKingNearOwnKingScoresBetterThanMoveThatDoesNotForceOpponentsKingNearOwnKing() {
            int forcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(E5)),
                    WHITE_KING.removeFrom(E5), WHITE_KING.addTo(F5));

            int doesNotForcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_KING.addTo(E5)),
                    WHITE_KING.removeFrom(E5), WHITE_KING.addTo(D4));

            assertThat(forcesOpponentKingNearOwnKing).isGreaterThan(doesNotForcesOpponentKingNearOwnKing);
        }

        @Test
        void blackMoveThatForcesOpponentsKingNearOwnKingScoresBetterThanMoveThatDoesNotForceOpponentsKingNearOwnKing() {
            int forcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8), BLACK_KING.addTo(E5)),
                    BLACK_KING.removeFrom(E5), BLACK_KING.addTo(F5));

            int doesNotForcesOpponentKingNearOwnKing = evaluator.valueOfMove(new Move(WHITE, BLACK_ROOK.addTo(B1), WHITE_KING.addTo(H8), BLACK_KING.addTo(E5)),
                    BLACK_KING.removeFrom(E5), BLACK_KING.addTo(D4));

            assertThat(forcesOpponentKingNearOwnKing).isLessThan(doesNotForcesOpponentKingNearOwnKing);
        }

    }
}