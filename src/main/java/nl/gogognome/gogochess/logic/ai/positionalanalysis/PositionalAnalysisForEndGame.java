package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import com.google.common.collect.ImmutableSet;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.EndOfGameBoardEvaluator;
import nl.gogognome.gogochess.logic.ai.PieceValueEvaluator;

import java.util.List;
import java.util.Set;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;

public class PositionalAnalysisForEndGame implements MovesEvaluator {

    private final PassedPawnFieldHeuristic passedPawnFieldHeuristic;
    private final CentralControlHeuristic centralControlHeuristic;
    private final KingFieldHeuristic kingFieldHeuristic;
    private final PawnHeuristicsEndgame pawnHeuristics;
    private final PieceValueEvaluator pieceValueEvaluator;
    private final EndOfGameBoardEvaluator endOfGameBoardEvaluator;

    PositionalAnalysisForEndGame(
            PassedPawnFieldHeuristic passedPawnFieldHeuristic,
            CentralControlHeuristic centralControlHeuristic,
            KingFieldHeuristic kingFieldHeuristic,
            PawnHeuristicsEndgame pawnHeuristics,
            PieceValueEvaluator pieceValueEvaluator,
            EndOfGameBoardEvaluator endOfGameBoardEvaluator) {
        this.passedPawnFieldHeuristic = passedPawnFieldHeuristic;
        this.centralControlHeuristic = centralControlHeuristic;
        this.kingFieldHeuristic = kingFieldHeuristic;
        this.pawnHeuristics = pawnHeuristics;
        this.pieceValueEvaluator = pieceValueEvaluator;
        this.endOfGameBoardEvaluator = endOfGameBoardEvaluator;
    }

    @Override
    public void evaluate(Board board, List<Move> moves) {
        Square opponentKingSquare = board.kingSquareOf(board.currentPlayerOpponent());

        boolean endgameWithPawns = board.countPiecesWhere(playerPiece -> playerPiece.getPiece() == PAWN) > 0;
        Set<Piece> pieces = ImmutableSet.of(BISHOP, ROOK, KNIGHT, QUEEN);
        boolean endgameWithPieces = board.countPiecesWhere(playerPiece -> pieces.contains(playerPiece.getPiece())) > 0;

        if (endgameWithPawns && endgameWithPieces) {
            pieceValueEvaluator.setWhitePawnValue(countNrPawnsFor(board, WHITE) <= 2 ? 190 : 120);
            pieceValueEvaluator.setBlackPawnValue(countNrPawnsFor(board, BLACK) <= 2 ? 190 : 120);
        }

        for (Move move : moves) {
            BoardMutation from = move.getMutationRemovingPieceFromStart();
            BoardMutation to = move.getMutationAddingPieceAtDestination();
            int value = 0;
            if (endgameWithPawns && !endgameWithPieces) {
                value += negateForBlack(passedPawnFieldHeuristic.getDeltaForPassedPawns(board, move), move);
                value += negateForBlack(centralControlHeuristic.getCenterControlDeltaForEndgameWithPawns(from, to), move);
                value += negateForBlack(kingFieldHeuristic.getKingFieldDeltaForEndgameWithPawns(from, to, opponentKingSquare), move);
                value += negateForBlack(pawnHeuristics.getPawnHeuristicsForOpeningAndEndgame(board, from, to), move);
            }

            if (endgameWithPawns && endgameWithPieces) {
                value += negateForBlack(getDeltaForRookPlacedBehindPassedPawn(board, from, to), move);
                value += negateForBlack(centralControlHeuristic.getCenterControlDeltaForGeneralEndgame(from, to), move);
                value += negateForBlack(kingFieldHeuristic.getKingFieldDeltaForGeneralEndgame(from, to, opponentKingSquare), move);
                value += negateForBlack(mobilityAfterMove(board, move), move);
            }

            if (!endgameWithPawns) {
                value += board.temporarilyMove(move, () -> {
                    Player opponent = move.getPlayer().opponent();
                    List<Move> opponentMoves = opponent.validMoves(board);
                    if (opponentMoves.isEmpty()) {
                        return endOfGameBoardEvaluator.value(board);
                    }
                    int bestOpponentValue = negateForBlack(-10000, opponent);
                    for (Move opponentMove : opponentMoves) {
                        int opponentValue = board.temporarilyMove(opponentMove, () -> evaluateForEndgameWithPieces(board, move.getPlayer()));
                        bestOpponentValue = opponent == WHITE ?
                                Math.max(bestOpponentValue, opponentValue) :
                                Math.min(bestOpponentValue, opponentValue);
                    }
                    return bestOpponentValue;
                });
            }
            move.setValue(value);
        }
    }

    private int evaluateForEndgameWithPieces(Board board, Player player) {
        int value;
        Square ownKingSquare = board.kingSquareOf(player);
        Player opponent = player.opponent();
        Square opponentKingSquare = board.kingSquareOf(opponent);

        value = negateForBlack(centralControlHeuristic.getCenterControlValueForOpponentKingInEndgameWithPieces(opponentKingSquare), player);
        value += negateForBlack(kingFieldHeuristic.getOpponentKingFieldValueForEndgameWithPieces(ownKingSquare, opponentKingSquare), player);
        return value;
    }

    private int countNrPawnsFor(Board board, Player player) {
        return board.countPiecesWhere(playerPiece -> playerPiece.getPlayer() == player && playerPiece.getPiece() == PAWN);
    }

    int getDeltaForRookPlacedBehindPassedPawn(Board board, BoardMutation from, BoardMutation to) {
        if (from.getPlayerPiece().getPiece() != ROOK) {
            return 0;
        }
        if (board.isBehindPassedPawn(to.getSquare()) && !board.isBehindPassedPawn(from.getSquare())) {
            return 15;
        }
        return 0;
    }

    private int mobilityAfterMove(Board board, Move move) {
        return board.temporarilyMove(move, () -> move.getPlayer().validMoves(board).size());
    }
}
