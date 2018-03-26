package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.piece.*;

public class MoveNotation {

	public String format(Move move) {
		if (move == null) {
			throw new IllegalArgumentException("Move must not be null");
		}

		BoardMutation removeMutation = getMutationRemovingPieceFromStart(move);
		BoardMutation addMutation = getMutationAddingPieceAtDestination(move);
		PlayerPiece capturedPiece = findCapturedPiece(move);

		StringBuilder result = new StringBuilder();
		if (isShortCastling(removeMutation, addMutation)) {
			appendShortCastling(result);
		} else if (isLongCastling(removeMutation, addMutation)) {
			appendLongCastling(result);
		} else {
			appendRegularMove(result, removeMutation, addMutation, capturedPiece);
		}

		appendCheckOrCheckMate(move, result);
		return result.toString();
	}

	private BoardMutation getMutationRemovingPieceFromStart(Move move) {
		return move.getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == move.getPlayer() && mutation.getMutation() == REMOVE)
				.filter(filterForKingDuringCastling(move))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No mutation found that removes a piece of the player"));
	}

	private BoardMutation getMutationAddingPieceAtDestination(Move move) {
		return move.getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == move.getPlayer() && mutation.getMutation() == ADD)
				.filter(filterForKingDuringCastling(move))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No mutation found that adds a piece of the player"));
	}

	private Predicate<BoardMutation> filterForKingDuringCastling(Move move) {
		Predicate<BoardMutation> extraFilter = mutation -> true;
		if (move.getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() == move.getPlayer() && mutation.getMutation() == REMOVE)
				.count() == 2) {
			extraFilter = mutation -> mutation.getPlayerPiece().getPiece() == KING;
		}
		return extraFilter;
	}

	private PlayerPiece findCapturedPiece(Move move) {
		return move.getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() != move.getPlayer())
				.map(BoardMutation::getPlayerPiece)
				.findFirst()
				.orElse(null);
	}

	private boolean isShortCastling(BoardMutation removeMutation, BoardMutation addMutation) {
		return removeMutation.getPlayerPiece().getPiece() == KING && removeMutation.getSquare().column() == 4 && addMutation.getSquare().column() == 6;
	}

	private void appendShortCastling(StringBuilder result) {
		result.append("O-O");
	}

	private boolean isLongCastling(BoardMutation removeMutation, BoardMutation addMutation) {
		return removeMutation.getPlayerPiece().getPiece() == KING && removeMutation.getSquare().column() == 4 && addMutation.getSquare().column() == 1;
	}

	private void appendLongCastling(StringBuilder result) {
		result.append("O-O-O");
	}

	private void appendRegularMove(
			StringBuilder result, BoardMutation removeMutation, BoardMutation addMutation,
			PlayerPiece capturedPiece) {
		result.append(pieceName(removeMutation.getPlayerPiece()));
		appendSquare(result, removeMutation.getSquare());
		result.append(capturedPiece != null ? 'x' : '-');
		if (capturedPiece != null) {
			result.append(pieceName(capturedPiece));
		}
		appendSquare(result, addMutation.getSquare());
		appendPromotionPiece(result, removeMutation, addMutation);
	}

	private void appendCheckOrCheckMate(Move move, StringBuilder result) {
		switch (move.getStatus()) {
			case CHECK:
				result.append('+');
				break;
			case CHECK_MATE:
				result.append("++");
				break;
		}
	}

	private String pieceName(PlayerPiece playerPiece) {
		switch (playerPiece.getPiece()) {
			case PAWN: return "";
			case KNIGHT: return "N";
			case BISHOP: return "B";
			case ROOK: return "R";
			case QUEEN: return "Q";
			case KING: return "K";
			default: throw new IllegalArgumentException("Unknown piece found: " + playerPiece.getPiece());
		}
	}

	private void appendSquare(StringBuilder stringBuilder, Square square) {
		stringBuilder.append((char) ('a' + square.column())).append(((char)('1' + square.row())));
	}

	private void appendPromotionPiece(StringBuilder result, BoardMutation removeMutation, BoardMutation addMutation) {
		if (removeMutation.getPlayerPiece().getPiece() == PAWN && addMutation.getPlayerPiece().getPiece() != PAWN) {
			result.append('(').append(pieceName(addMutation.getPlayerPiece())).append(')');
		}
	}
}
