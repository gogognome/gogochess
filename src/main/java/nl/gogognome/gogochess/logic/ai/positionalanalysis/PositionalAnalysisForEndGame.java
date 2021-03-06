package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

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
            MoveValue value = MoveValue.ZERO;
            if (endgameWithPawns && !endgameWithPieces) {
                value = value
                        .add(passedPawnFieldHeuristic.getDeltaForPassedPawns(board, move), move, "delta for passed pawns")
                        .add(centralControlHeuristic.getCenterControlDeltaForEndgameWithPawns(from, to), move, "center control delta for endgame with pawns")
                        .add(kingFieldHeuristic.getKingFieldDeltaForEndgameWithPawns(from, to, opponentKingSquare), move, "king field delta for endgame with pawns")
                        .add(pawnHeuristics.getPawnHeuristicsForEndgame(board, from, to), move, "pawn heuristics for endgame");
            }

            if (endgameWithPawns && endgameWithPieces) {
                value = value
                        .add(getDeltaForRookPlacedBehindPassedPawn(board, from, to), move, "dela for rook placed behind passed pawn")
                        .add(centralControlHeuristic.getCenterControlDeltaForGeneralEndgame(from, to), move, "center control delta for general endgame")
                        .add(kingFieldHeuristic.getKingFieldDeltaForGeneralEndgame(from, to, opponentKingSquare), move, "king field dleta for general endgame")
                        .add(mobilityAfterMove(board, move), move, "mobility after move");
            }

            if (!endgameWithPawns) {
                value = value.add(board.temporarilyMove(move, () -> {
                    Player opponent = move.getPlayer().opponent();
                    List<Move> opponentMoves = opponent.validMoves(board);
                    if (opponentMoves.isEmpty()) {
                        return endOfGameBoardEvaluator.value(board);
                    }
                    MoveValue bestOpponentValue = new MoveValue(-10000, opponent, "min value approximation");
                    for (Move opponentMove : opponentMoves) {
                        MoveValue opponentValue = board.temporarilyMove(opponentMove, () -> evaluateForEndgameWithPieces(board, move.getPlayer()));
                        bestOpponentValue = opponent == WHITE ?
                                MoveValue.max(bestOpponentValue, opponentValue) :
                                MoveValue.min(bestOpponentValue, opponentValue);
                    }
                    return bestOpponentValue;
                }), "endgame with pieces");
            }
            move.setValue(value);
        }
    }

    private MoveValue evaluateForEndgameWithPieces(Board board, Player player) {
        Square ownKingSquare = board.kingSquareOf(player);
        Player opponent = player.opponent();
        Square opponentKingSquare = board.kingSquareOf(opponent);

        MoveValue value = new MoveValue(
                centralControlHeuristic.getCenterControlValueForOpponentKingInEndgameWithPieces(opponentKingSquare),
                player,
                "center control value for opponent king in endgame with pieces")
                .add(kingFieldHeuristic.getOpponentKingFieldValueForEndgameWithPieces(ownKingSquare, opponentKingSquare), player, "opponent king field value for endgame with pieces")
                .add(centralControlHeuristic.getCenterControlValueForOwnKingInEndgameWithPieces(ownKingSquare), player, "center control value for own king in endgame with pieces");
        List<Square> ownPiecesSquares = getOwnPiecesSquaresExceptForKing(board, player);
        value = value.add(kingFieldHeuristic.getCenterControlValueForPiecesAt(ownKingSquare, ownPiecesSquares), player, "center control value");
        return value;
    }

    private List<Square> getOwnPiecesSquaresExceptForKing(Board board, Player player) {
        List<Square> ownPiecesSquares = new ArrayList<>();
        board.forEachPlayerPieceWhere(player,
                (playerPiece, square) -> playerPiece.getPiece() != KING,
                (playerPiece, square) ->  ownPiecesSquares.add(square));
        return ownPiecesSquares;
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
