package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;
import static nl.gogognome.gogochess.logic.Player.*;

import java.util.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.PieceValueEvaluator;

class PositionalAnalysisForMiddleGame implements MovesEvaluator {

	private final CastlingHeuristics castlingHeuristics;
	private final CentralControlHeuristic centralControlHeuristic;
	private final KingFieldHeuristic kingFieldHeuristic;
	private final PawnHeuristics pawnHeuristics;
	private final PieceValueEvaluator pieceValueEvaluator;

	PositionalAnalysisForMiddleGame(
			CastlingHeuristics castlingHeuristics,
			CentralControlHeuristic centralControlHeuristic,
			KingFieldHeuristic kingFieldHeuristic,
			PawnHeuristics pawnHeuristics,
			PieceValueEvaluator pieceValueEvaluator) {
		this.castlingHeuristics = castlingHeuristics;
		this.centralControlHeuristic = centralControlHeuristic;
		this.kingFieldHeuristic = kingFieldHeuristic;
		this.pawnHeuristics = pawnHeuristics;
		this.pieceValueEvaluator = pieceValueEvaluator;
	}

	public void evaluate(Board board, List<Move> moves) {
		Square opponentKingSquare = board.kingSquareOf(board.currentPlayerOpponent());
		int captureBonus = getCaptureBonus(board);

		for (Move move : moves) {
			BoardMutation from = move.getMutationRemovingPieceFromStart();
			BoardMutation to = move.getMutationAddingPieceAtDestination();

			int value = negateForBlack(centralControlHeuristic.getCenterControlDeltaForMiddleGame(from, to), move);
			value += negateForBlack(castlingHeuristics.getCastlingValue(from.getPlayerPiece().getPiece(), from.getSquare().column(), to.getSquare().column()), move);
			value += negateForBlack(kingFieldHeuristic.getKingFieldDeltaForMiddleGame(from, to, opponentKingSquare), move);
			value += negateForBlack(mobilityAfterMove(board, move), move);
			value += pawnHeuristics.getPawnHeuristicsForOpening(board, move, from, to);
			value += move.isCapture() ? captureBonus : 0;

			move.setValue(value);
		}
	}

	private int mobilityAfterMove(Board board, Move move) {
		return board.temporarilyMove(move, () -> board.currentPlayerOpponent().validMoves(board).size());
	}

	private int getCaptureBonus(Board board) {
		int pieceValue = pieceValueEvaluator.value(board);
		int captureBonus = 0;
		if (board.currentPlayer() == WHITE && pieceValue > 0) {
			captureBonus = 10;
		} else if (board.currentPlayer() == BLACK && pieceValue < 0) {
			captureBonus = -10;
		}
		return captureBonus;
	}
}
