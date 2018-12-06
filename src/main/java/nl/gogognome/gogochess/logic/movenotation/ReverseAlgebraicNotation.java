package nl.gogognome.gogochess.logic.movenotation;

import static nl.gogognome.gogochess.logic.Piece.*;
import java.util.stream.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

public class ReverseAlgebraicNotation implements MoveNotation {

	@Override
	public String format(Move move) {
		if (move == null) {
			throw new IllegalArgumentException("Move must not be null");
		}

		BoardMutation removeMutation = move.getMutationRemovingPieceFromStart();
		BoardMutation addMutation = move.getMutationAddingPieceAtDestination();
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

	@Override
	public String format(Move from, Move to) {
		return from.pathTo(to).stream()
				.map(this::format)
				.collect(Collectors.joining(", "));
	}

	private PlayerPiece findCapturedPiece(Move move) {
		return move.getBoardMutations().stream()
				.filter(mutation -> mutation.getPlayerPiece().getPlayer() != move.getPlayer())
				.map(BoardMutation::getPlayerPiece)
				.findFirst()
				.orElse(null);
	}

	private boolean isShortCastling(BoardMutation removeMutation, BoardMutation addMutation) {
		return removeMutation.getPlayerPiece().getPiece() == KING && removeMutation.getSquare().file() == 4 && addMutation.getSquare().file() == 6;
	}

	private void appendShortCastling(StringBuilder result) {
		result.append("O-O");
	}

	private boolean isLongCastling(BoardMutation removeMutation, BoardMutation addMutation) {
		return removeMutation.getPlayerPiece().getPiece() == KING && removeMutation.getSquare().file() == 4 && addMutation.getSquare().file() == 2;
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
		stringBuilder.append((char) ('a' + square.file())).append(((char)('1' + square.rank())));
	}

	private void appendPromotionPiece(StringBuilder result, BoardMutation removeMutation, BoardMutation addMutation) {
		if (removeMutation.getPlayerPiece().getPiece() == PAWN && addMutation.getPlayerPiece().getPiece() != PAWN) {
			result.append('(').append(pieceName(addMutation.getPlayerPiece())).append(')');
		}
	}


}
