package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.util.*;
import nl.gogognome.gogochess.logic.*;

class PositionalAnalysisForMiddleGame {

	private final CentralControlHeuristic centralControlHeuristic;
	private final KingField kingField;

	PositionalAnalysisForMiddleGame(
			CentralControlHeuristic centralControlHeuristic,
			KingField kingField) {
		this.centralControlHeuristic = centralControlHeuristic;
		this.kingField = kingField;
	}

	public void evaluate(Board board, List<Move> moves) {
		Square opponentKingSquare = board.kingSquareOf(board.lastMove().getPlayer());
		for (Move move : moves) {
			BoardMutation from = move.getMutationRemovingPieceFromStart();
			BoardMutation to = move.getMutationAddingPieceAtDestination();

			int value = negateForBlack(centralControlHeuristic.getCenterControlDeltaForMiddleGame(from, to), move);
			value += negateForBlack(mobilityAfterMove(board, move), move);
			value += negateForBlack(kingField.getKingFieldDeltaForMiddleGame(from, to, opponentKingSquare), move);
			move.setValue(value);
		}
	}

	private int mobilityAfterMove(Board board, Move move) {
		board.process(move);
		return WHITE.validMoves(board).size() - BLACK.validMoves(board).size();
	}
}
