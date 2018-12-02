package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;

import java.util.*;
import nl.gogognome.gogochess.logic.*;

class PositionalAnalysisForMiddleGame implements MovesEvaluator {

	private final CentralControlHeuristic centralControlHeuristic;
	private final KingFieldHeuristic kingFieldHeuristic;

	PositionalAnalysisForMiddleGame(
			CentralControlHeuristic centralControlHeuristic,
			KingFieldHeuristic kingFieldHeuristic) {
		this.centralControlHeuristic = centralControlHeuristic;
		this.kingFieldHeuristic = kingFieldHeuristic;
	}

	public void evaluate(Board board, List<Move> moves) {
		Square opponentKingSquare = board.kingSquareOf(board.currentPlayerOpponent());
		for (Move move : moves) {
			BoardMutation from = move.getMutationRemovingPieceFromStart();
			BoardMutation to = move.getMutationAddingPieceAtDestination();

			int value = negateForBlack(centralControlHeuristic.getCenterControlDeltaForMiddleGame(from, to), move);
			value += negateForBlack(kingFieldHeuristic.getKingFieldDeltaForMiddleGame(from, to, opponentKingSquare), move);
			value += negateForBlack(mobilityAfterMove(board, move), move);
			move.setValue(value);
		}
	}

	private int mobilityAfterMove(Board board, Move move) {
		Move lastMove = board.lastMove();
		board.process(move);

		int mobility = board.currentPlayerOpponent().validMoves(board).size();

		board.process(lastMove);
		return mobility;
	}
}
