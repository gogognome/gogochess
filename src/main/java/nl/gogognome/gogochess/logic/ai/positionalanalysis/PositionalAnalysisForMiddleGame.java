package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.PieceValueEvaluator;
import nl.gogognome.gogochess.logic.piece.Pawn;

import java.util.List;

import static nl.gogognome.gogochess.logic.MoveValues.negateForBlack;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Square.FILE_C;
import static nl.gogognome.gogochess.logic.Square.FILE_F;

class PositionalAnalysisForMiddleGame implements MovesEvaluator {

	private final CastlingHeuristics castlingHeuristics;
	private final CentralControlHeuristic centralControlHeuristic;
	private final KingFieldHeuristic kingFieldHeuristic;
	private final PawnHeuristicsOpeningAndMiddleGame pawnHeuristics;
	private final PieceValueEvaluator pieceValueEvaluator;

	PositionalAnalysisForMiddleGame(
			CastlingHeuristics castlingHeuristics,
			CentralControlHeuristic centralControlHeuristic,
			KingFieldHeuristic kingFieldHeuristic,
			PawnHeuristicsOpeningAndMiddleGame pawnHeuristics,
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
			value += negateForBlack(castlingHeuristics.getCastlingValue(from.getPlayerPiece().getPiece(), from.getSquare().file(), to.getSquare().file()), move);
			value += negateForBlack(kingFieldHeuristic.getKingFieldDeltaForMiddleGame(from, to, opponentKingSquare), move);
			value += negateForBlack(mobilityAfterMove(board, move), move);
			value += pawnHeuristics.getPawnHeuristicsForOpeningAndMiddleGame(board, move, from, to);
			value += move.isCapture() ? captureBonus : 0;
			value += negateForBlack(unblocksKingsOrQueensBishopPawn(from, move.getPlayer(), board), move);

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

	private int unblocksKingsOrQueensBishopPawn(BoardMutation from, Player player, Board board) {
		int file = from.getSquare().file();
		if (file != FILE_C && file != FILE_F) {
			return 0;
		}
		Square blockedSquare = from.getSquare().addRanks(player == WHITE ? -1 : 1);
		if (blockedSquare == null) {
			return 0;
		}

		Pawn pawn = new Pawn(player);
		return pawn.equals(board.pieceAt(blockedSquare)) ? 5 : 0;
	}
}
