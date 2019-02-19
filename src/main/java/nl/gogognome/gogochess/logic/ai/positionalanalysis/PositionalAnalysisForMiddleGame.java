package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Square.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.piece.*;

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
		MoveValue captureBonus = getCaptureBonus(board);

		for (Move move : moves) {
			BoardMutation from = move.getMutationRemovingPieceFromStart();
			BoardMutation to = move.getMutationAddingPieceAtDestination();

			MoveValue value = new MoveValue(centralControlHeuristic.getCenterControlDeltaForMiddleGame(from, to), move)
					.add(castlingHeuristics.getCastlingValue(from.getPlayerPiece().getPiece(), from.getSquare().file(), to.getSquare().file()), move)
					.add(kingFieldHeuristic.getKingFieldDeltaForMiddleGame(from, to, opponentKingSquare), move)
					.add(mobilityAfterMove(board, move), move)
					.add(pawnHeuristics.getPawnHeuristicsForOpeningAndMiddleGame(board, move, from, to))
					.add(move.isCapture() ? captureBonus : ZERO)
					.add(unblocksKingsOrQueensBishopPawn(from, move.getPlayer(), board), move);

			move.setValue(value);
		}
	}

	private int mobilityAfterMove(Board board, Move move) {
		return board.temporarilyMove(move, () -> move.getPlayer().validMoves(board).size());
	}

	private MoveValue getCaptureBonus(Board board) {
		MoveValue pieceValue = pieceValueEvaluator.value(board);
		MoveValue captureBonus = ZERO;
		if (board.currentPlayer() == WHITE && pieceValue.isGreaterThan(ZERO)) {
			captureBonus = forWhite(10);
		} else if (board.currentPlayer() == BLACK && pieceValue.isLessThan(ZERO)) {
			captureBonus = forBlack(10);
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
