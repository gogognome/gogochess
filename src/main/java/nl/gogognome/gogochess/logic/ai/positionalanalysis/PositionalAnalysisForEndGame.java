package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import com.google.common.collect.ImmutableSet;
import nl.gogognome.gogochess.logic.*;

import java.util.List;
import java.util.Set;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;
import static nl.gogognome.gogochess.logic.Piece.*;

public class PositionalAnalysisForEndGame implements MovesEvaluator {

    private final PassedPawnFieldHeuristic passedPawnFieldHeuristic;
    private final CentralControlHeuristic centralControlHeuristic;
    private final KingFieldHeuristic kingFieldHeuristic;
    private final PawnHeuristicsEndgame pawnHeuristics;

    PositionalAnalysisForEndGame(
            PassedPawnFieldHeuristic passedPawnFieldHeuristic,
            CentralControlHeuristic centralControlHeuristic,
            KingFieldHeuristic kingFieldHeuristic, PawnHeuristicsEndgame pawnHeuristics) {
        this.passedPawnFieldHeuristic = passedPawnFieldHeuristic;
        this.centralControlHeuristic = centralControlHeuristic;
        this.kingFieldHeuristic = kingFieldHeuristic;
        this.pawnHeuristics = pawnHeuristics;
    }

    @Override
    public void evaluate(Board board, List<Move> moves) {
        Square opponentKingSquare = board.kingSquareOf(board.currentPlayerOpponent());

        boolean endgameWithPawns = board.countPiecesWhere(playerPiece -> playerPiece.getPiece() == PAWN) > 0;
        Set<Piece> pieces = ImmutableSet.of(BISHOP, ROOK, KNIGHT, QUEEN);
        boolean endgameWithPieces = board.countPiecesWhere(playerPiece -> pieces.contains(playerPiece.getPiece())) > 0;

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
            move.setValue(value);
        }
    }
}
