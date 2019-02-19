package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

class PositionalAnalysisForMiddleGameTest {

    private PositionalAnalysisForMiddleGame positionalAnalysisForMiddleGame = new PositionalAnalysisForMiddleGame(
            new CastlingHeuristics(), 
            new CentralControlHeuristic(), 
            new KingFieldHeuristic(), 
            new PawnHeuristicsOpeningAndMiddleGame(-5),
            new PieceValueEvaluator());
    
    private SingleMoveEvaluator evaluator = SingleMoveEvaluator.forConsumer((board, move) -> positionalAnalysisForMiddleGame.evaluate(board, asList(move)));

    @Test
    void blackKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
        MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(C6)),
                BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8));

        assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
    }

    @Test
    void whiteBishopMovingToCenterScoresBetterThanBishopMovingAwayFromCenter() {
        MoveValue towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A1), WHITE_BISHOP.addTo(B8)),
                WHITE_BISHOP.removeFrom(B8), WHITE_BISHOP.addTo(E5));

        MoveValue awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A1), WHITE_BISHOP.addTo(E5)),
                WHITE_BISHOP.removeFrom(E5), WHITE_BISHOP.addTo(B8));

        assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
    }

    @Test
    void blackKnightMovingToWhiteKingScoresBetterThanKnightMovingAwayFromWhiteKing() {
        MoveValue towardsOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(C5), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        MoveValue awayFromOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A8), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        assertThat(towardsOpponentsKing).isLessThan(awayFromOpponentsKing);
    }

    @Test
    void whiteQueenMovingToBlackKingScoresBetterThanQueenMovingAwayFromBlackKing() {
        MoveValue towardsOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(C5), WHITE_QUEEN.addTo(B8)),
                WHITE_QUEEN.removeFrom(B8), WHITE_QUEEN.addTo(B5));

        MoveValue awayFromOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(C5), WHITE_QUEEN.addTo(B5)),
                WHITE_QUEEN.removeFrom(B5), WHITE_QUEEN.addTo(B8));

        assertThat(towardsOpponentsKing).isGreaterThan(awayFromOpponentsKing);
    }

    @Test
    void bigMobilityForWhiteRookScoresBetterThanSmallMobility() {
        MoveValue bigMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_PAWN.addTo(B2)),
                WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

        MoveValue smallMobility = evaluator.valueOfMove(new Move(BLACK, WHITE_ROOK.addTo(B1), BLACK_KING.addTo(H8), WHITE_PAWN.addTo(A2)),
                WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

        assertThat(bigMobility).isGreaterThan(smallMobility);
    }

    @Test
    void bigMobilityForBlackQueenScoresBetterThanSmallMobility() {
        MoveValue bigMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_PAWN.addTo(A7)),
                BLACK_QUEEN.removeFrom(B1), BLACK_QUEEN.addTo(A1));

        MoveValue smallMobility = evaluator.valueOfMove(new Move(WHITE, BLACK_QUEEN.addTo(B1), WHITE_KING.addTo(H8), WHITE_PAWN.addTo(A2)),
                BLACK_QUEEN.removeFrom(B1), BLACK_QUEEN.addTo(A1));
        assertThat(bigMobility).isLessThan(smallMobility);
    }

    @Test
    void higherPriorityWhitePieceCloserToOpponentQueenScoresBetterThanLowerPriorityNearQueen() {
        MoveValue highPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_ROOK.addTo(A8), BLACK_PAWN.addTo(H7), BLACK_PAWN.addTo(G7), BLACK_PAWN.addTo(F7)),
                WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(G8));

        MoveValue lowPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_QUEEN.addTo(A8), BLACK_PAWN.addTo(H7), BLACK_PAWN.addTo(G7), BLACK_PAWN.addTo(F7)),
                WHITE_QUEEN.removeFrom(A8), WHITE_QUEEN.addTo(G8));

        assertThat(highPriorityPieceNearOpponentsKing).isGreaterThan(lowPriorityPieceNearOpponentsKing);
    }

    @Test
    void higherPriorityBlackPieceCloserToOpponentQueenScoresBetterThanLowerPriorityNearQueen() {
        MoveValue highPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H8), BLACK_ROOK.addTo(A8), WHITE_PAWN.addTo(H7), WHITE_PAWN.addTo(G7), WHITE_PAWN.addTo(F7)),
                BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(G8));

        MoveValue lowPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H8), BLACK_QUEEN.addTo(A8), WHITE_PAWN.addTo(H7), WHITE_PAWN.addTo(G7), WHITE_PAWN.addTo(F7)),
                BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(G8));

        assertThat(highPriorityPieceNearOpponentsKing).isLessThan(lowPriorityPieceNearOpponentsKing);
    }

    @Test
    void moveWhiteBishopWhichBlocksKingsBishopPawnScoresBetterThanBishopThatDoesNotBlockKingsBishopPawn() {
        MoveValue bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_BISHOP.addTo(C3), WHITE_PAWN.addTo(C2)),
                WHITE_BISHOP.removeFrom(C3), WHITE_BISHOP.addTo(B2));

        MoveValue bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_BISHOP.addTo(C3)),
                WHITE_BISHOP.removeFrom(C3), WHITE_BISHOP.addTo(B2));

        assertThat(bishopWhichBlocksKingsBishopPawn).isGreaterThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveWhiteBishopWhichBlocksQueensBishopPawnScoresBetterThanBishopThatDoesNotBlockQueensBishopPawn() {
        MoveValue bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A8), WHITE_BISHOP.addTo(F3), WHITE_PAWN.addTo(F2)),
                WHITE_BISHOP.removeFrom(F3), WHITE_BISHOP.addTo(G2));

        MoveValue bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A8), WHITE_BISHOP.addTo(F3)),
                WHITE_BISHOP.removeFrom(F3), WHITE_BISHOP.addTo(G2));

        assertThat(bishopWhichBlocksKingsBishopPawn).isGreaterThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveBlackBishopWhichBlocksKingsBishopPawnScoresBetterThanBishopThatDoesNotBlockKingsBishopPawn() {
        MoveValue bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(C6), BLACK_PAWN.addTo(C7)),
                BLACK_BISHOP.removeFrom(C6), BLACK_BISHOP.addTo(B7));

        MoveValue bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(C6)),
                BLACK_BISHOP.removeFrom(C6), BLACK_BISHOP.addTo(B7));

        assertThat(bishopWhichBlocksKingsBishopPawn).isLessThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveBlackBishopWhichBlocksQueensBishopPawnScoresBetterThanBishopThatDoesNotBlockQueensBishopPawn() {
        MoveValue bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_BISHOP.addTo(F6), BLACK_PAWN.addTo(F7)),
                BLACK_BISHOP.removeFrom(F6), BLACK_BISHOP.addTo(G7));

        MoveValue bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(F6)),
                BLACK_BISHOP.removeFrom(F6), BLACK_BISHOP.addTo(G7));

        assertThat(bishopWhichBlocksKingsBishopPawn).isLessThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

}