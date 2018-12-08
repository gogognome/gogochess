package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.ai.PieceValueEvaluator;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import static org.assertj.core.api.Assertions.assertThat;

class PositionalAnalysisForMiddleGameTest {

    private PositionalAnalysisForMiddleGame positionalAnalysisForMiddleGame = new PositionalAnalysisForMiddleGame(
            new CastlingHeuristics(), 
            new CentralControlHeuristic(), 
            new KingFieldHeuristic(), 
            new PawnHeuristics(-5), 
            new PieceValueEvaluator());
    
    private SingleMoveEvaluator evaluator = new SingleMoveEvaluator((board, move) -> positionalAnalysisForMiddleGame.evaluate(board, asList(move)));

    @Test
    void blackKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
        int towardsCenterValue = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        int awayFromCenterValue = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(C6)),
                BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8));

        assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
    }

    @Test
    void whiteBishopMovingToCenterScoresBetterThanBishopMovingAwayFromCenter() {
        int towardsCenterValue = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A1), WHITE_BISHOP.addTo(B8)),
                WHITE_BISHOP.removeFrom(B8), WHITE_BISHOP.addTo(E5));

        int awayFromCenterValue = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A1), WHITE_BISHOP.addTo(E5)),
                WHITE_BISHOP.removeFrom(E5), WHITE_BISHOP.addTo(B8));

        assertThat(towardsCenterValue).isGreaterThan(awayFromCenterValue);
    }

    @Test
    void blackKnightMovingToWhiteKingScoresBetterThanKnightMovingAwayFromWhiteKing() {
        int towardsOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(C5), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        int awayFromOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A8), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        assertThat(towardsOpponentsKing).isLessThan(awayFromOpponentsKing);
    }

    @Test
    void whiteQueenMovingToBlackKingScoresBetterThanQueenMovingAwayFromBlackKing() {
        int towardsOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(C5), WHITE_QUEEN.addTo(B8)),
                WHITE_QUEEN.removeFrom(B8), WHITE_QUEEN.addTo(B5));

        int awayFromOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(C5), WHITE_QUEEN.addTo(B5)),
                WHITE_QUEEN.removeFrom(B5), WHITE_QUEEN.addTo(B8));

        assertThat(towardsOpponentsKing).isGreaterThan(awayFromOpponentsKing);
    }

    @Test
    void bigMobilityForWhiteRookScoresBetterThanSmallMobility() {
        int bigMobility = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_PAWN.addTo(A2), WHITE_ROOK.addTo(B1)),
                WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(B5));

        int smallMobility = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_PAWN.addTo(A2), WHITE_ROOK.addTo(B1)),
                WHITE_ROOK.removeFrom(B1), WHITE_ROOK.addTo(A1));

        assertThat(bigMobility).isGreaterThan(smallMobility);
    }

    @Test
    void bigMobilityForBlackQueenScoresBetterThanSmallMobility() {
        int bigMobility = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_QUEEN.addTo(B7), WHITE_ROOK.addTo(A5), WHITE_ROOK.addTo(C5)),
                BLACK_QUEEN.removeFrom(B7), BLACK_QUEEN.addTo(B3));

        int smallMobility = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_QUEEN.addTo(B7), WHITE_ROOK.addTo(A5), WHITE_ROOK.addTo(C5)),
                BLACK_QUEEN.removeFrom(B7), BLACK_QUEEN.addTo(B5));

        assertThat(bigMobility).isLessThan(smallMobility);
    }

    @Test
    void higherPriorityWhitePieceCloserToOpponentQueenScoresBetterThanLowerPriorityNearQueen() {
        int highPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_ROOK.addTo(A8), BLACK_PAWN.addTo(H7), BLACK_PAWN.addTo(G7), BLACK_PAWN.addTo(F7)),
                WHITE_ROOK.removeFrom(A8), WHITE_ROOK.addTo(G8));

        int lowPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_QUEEN.addTo(A8), BLACK_PAWN.addTo(H7), BLACK_PAWN.addTo(G7), BLACK_PAWN.addTo(F7)),
                WHITE_QUEEN.removeFrom(A8), WHITE_QUEEN.addTo(G8));

        assertThat(highPriorityPieceNearOpponentsKing).isGreaterThan(lowPriorityPieceNearOpponentsKing);
    }

    @Test
    void higherPriorityBlackPieceCloserToOpponentQueenScoresBetterThanLowerPriorityNearQueen() {
        int highPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H8), BLACK_ROOK.addTo(A8), WHITE_PAWN.addTo(H7), WHITE_PAWN.addTo(G7), WHITE_PAWN.addTo(F7)),
                BLACK_ROOK.removeFrom(A8), BLACK_ROOK.addTo(G8));

        int lowPriorityPieceNearOpponentsKing = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H8), BLACK_QUEEN.addTo(A8), WHITE_PAWN.addTo(H7), WHITE_PAWN.addTo(G7), WHITE_PAWN.addTo(F7)),
                BLACK_QUEEN.removeFrom(A8), BLACK_QUEEN.addTo(G8));

        assertThat(highPriorityPieceNearOpponentsKing).isLessThan(lowPriorityPieceNearOpponentsKing);
    }

    @Test
    void moveWhiteBishopWhichBlocksKingsBishopPawnScoresBetterThanBishopThatDoesNotBlockKingsBishopPawn() {
        int bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_BISHOP.addTo(C3), WHITE_PAWN.addTo(C2)),
                WHITE_BISHOP.removeFrom(C3), WHITE_BISHOP.addTo(B2));

        int bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(H8), WHITE_BISHOP.addTo(C3)),
                WHITE_BISHOP.removeFrom(C3), WHITE_BISHOP.addTo(B2));

        assertThat(bishopWhichBlocksKingsBishopPawn).isGreaterThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveWhiteBishopWhichBlocksQueensBishopPawnScoresBetterThanBishopThatDoesNotBlockQueebsBishopPawn() {
        int bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A8), WHITE_BISHOP.addTo(F3), WHITE_PAWN.addTo(F2)),
                WHITE_BISHOP.removeFrom(F3), WHITE_BISHOP.addTo(G2));

        int bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(BLACK, BLACK_KING.addTo(A8), WHITE_BISHOP.addTo(F3)),
                WHITE_BISHOP.removeFrom(F3), WHITE_BISHOP.addTo(G2));

        assertThat(bishopWhichBlocksKingsBishopPawn).isGreaterThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveBlackBishopWhichBlocksKingsBishopPawnScoresBetterThanBishopThatDoesNotBlockKingsBishopPawn() {
        int bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(C6), BLACK_PAWN.addTo(C7)),
                BLACK_BISHOP.removeFrom(C6), BLACK_BISHOP.addTo(B7));

        int bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(C6)),
                BLACK_BISHOP.removeFrom(C6), BLACK_BISHOP.addTo(B7));

        assertThat(bishopWhichBlocksKingsBishopPawn).isLessThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

    @Test
    void moveBlackBishopWhichBlocksQueensBishopPawnScoresBetterThanBishopThatDoesNotBlockQueebsBishopPawn() {
        int bishopWhichBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_BISHOP.addTo(F6), BLACK_PAWN.addTo(F7)),
                BLACK_BISHOP.removeFrom(F6), BLACK_BISHOP.addTo(G7));

        int bishopWhichDoesNotBlocksKingsBishopPawn = evaluator.valueOfMove(new Move(WHITE, WHITE_KING.addTo(H1), BLACK_BISHOP.addTo(F6)),
                BLACK_BISHOP.removeFrom(F6), BLACK_BISHOP.addTo(G7));

        assertThat(bishopWhichBlocksKingsBishopPawn).isLessThan(bishopWhichDoesNotBlocksKingsBishopPawn);
    }

}