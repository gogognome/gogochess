package nl.gogognome.gogochess.game;

import static nl.gogognome.gogochess.game.Player.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import nl.gogognome.gogochess.game.piece.*;

public class Board {

	public static final PlayerPiece WHITE_PAWN = new Pawn(WHITE);
	public static final PlayerPiece WHITE_KNIGHT = new Knight(WHITE);
	public static final PlayerPiece WHITE_BISHOP = new Bishop(WHITE);
	public static final PlayerPiece WHITE_ROOK = new Rook(WHITE);
	public static final PlayerPiece WHITE_QUEEN = new Queen(WHITE);
	public static final PlayerPiece WHITE_KING = new King(WHITE);
	public static final PlayerPiece BLACK_PAWN = new Pawn(BLACK);
	public static final PlayerPiece BLACK_KNIGHT = new Knight(BLACK);
	public static final PlayerPiece BLACK_BISHOP = new Bishop(BLACK);
	public static final PlayerPiece BLACK_ROOK = new Rook(BLACK);
	public static final PlayerPiece BLACK_QUEEN = new Queen(BLACK);
	public static final PlayerPiece BLACK_KING = new King(BLACK);

	private Move lastMove;

	private PlayerPiece[] playerPiecesPerSquare = new PlayerPiece[8*8];

	public void process(Move move) {
		Move commonAncestor = Move.findCommonAncestor(lastMove, move);
		undoUntil(commonAncestor);
		processForwardUntil(move, commonAncestor);
		lastMove = move;
	}

	private void undoUntil(Move commonAncestor) {
		while (lastMove != commonAncestor) {
			undoSingleMove(lastMove);
			lastMove = lastMove.getPrecedingMove();
		}
	}

	private void processForwardUntil(Move move, Move commonAncestor) {
		Deque<Move> moves = new LinkedList<>();
		while (move != commonAncestor) {
			moves.push(move);
			move = move.getPrecedingMove();
		}
		while (!moves.isEmpty()) {
			processSingleMove(moves.pop());
		}
	}

	private void processSingleMove(Move move) {
		for (BoardMutation boardMutation : move.getBoardMutations()) {
			process(boardMutation);
		}
	}

	private void undoSingleMove(Move move) {
		List<BoardMutation> boardMutations = move.getBoardMutations();
		for (int i=boardMutations.size() - 1; i >=0; i--) {
			undo(boardMutations.get(i));
		}
	}

	void process(BoardMutation mutation) {
		switch (mutation.getMutation()) {
			case ADD:
				addPlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			case REMOVE:
				removePlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			default:
				throw new IllegalArgumentException("Unexpected mutation found: " + mutation.getMutation());
		}
	}

	private void undo(BoardMutation mutation) {
		switch (mutation.getMutation()) {
			case ADD:
				removePlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			case REMOVE:
				addPlayerPiece(mutation.getPlayerPiece(), mutation.getSquare());
				break;
			default:
				throw new IllegalArgumentException("Unexpected mutation found: " + mutation.getMutation());
		}
	}

	private void addPlayerPiece(PlayerPiece playerPieceToAdd, Square square) {
		int index = square.boardIndex();
		PlayerPiece playerPiece = playerPiecesPerSquare[index];
		if (playerPiece != null) {
			throw new IllegalArgumentException("The square " + square + " is not empty. It contains " + playerPiece + '.');
		}
		playerPiecesPerSquare[index] = playerPieceToAdd;
	}

	private void removePlayerPiece(PlayerPiece playerPieceToRemove, Square square) {
		int index = square.boardIndex();
		PlayerPiece playerPiece = playerPiecesPerSquare[index];
		if (playerPiece == null) {
			throw new IllegalArgumentException("The square " + square + " is empty, instead of containing " + playerPieceToRemove + '.');
		}
		if (!playerPiece.equals(playerPieceToRemove)) {
			throw new IllegalArgumentException("The square " + square + " does not contain " + playerPieceToRemove + ". It contains " + playerPiece + '.');
		}
		playerPiecesPerSquare[index] = null;
	}

	public PlayerPiece pieceAt(Square square) {
		return playerPiecesPerSquare[square.boardIndex()];
	}

	public boolean empty(Square square) {
		return pieceAt(square) == null;
	}

	public List<Move> validMoves(Player player) {
		if (lastMove == null) {
			throw new IllegalStateException("No moves can be determined when the board is empty");
		}
		if (!lastMove.hasFollowingMoves()) {
			List<Move> moves = new ArrayList<>(40);
			addMovesIgnoringCheck(player, moves);
			removeMovesCausingCheckForOwnPlayer(player, moves);
			determineCheck(player, moves);
			lastMove.setFollowingMoves(moves);
		}
		return lastMove.getFollowingMoves();
	}

	private void addMovesIgnoringCheck(Player player, List<Move> moves) {
		forEachPlayerPiece(player, (playerPiece, square) -> playerPiece.addPossibleMoves(moves, square, this));
	}

	private void removeMovesCausingCheckForOwnPlayer(Player player, List<Move> moves) {
		int index = 0;
		while (index < moves.size()) {
			processSingleMove(moves.get(index));

			AtomicBoolean attacksKing = new AtomicBoolean();
			Square kingSquare = squareOf(new King(player));
			if (kingSquare != null) {
				forEachPlayerPiece(player.other(), (playerPiece, square) -> attacksKing.set(attacksKing.get() || playerPiece.attacks(square, kingSquare, this)));
			}

			undoSingleMove(moves.get(index));

			if (attacksKing.get()) {
				moves.remove(index);
			} else {
				index++;
			}
		}
	}

	private void determineCheck(Player player, List<Move> moves) {
		for (Move move : moves) {
			processSingleMove(move);

			Square oppositeKingSquare = squareOf(new King(player.other()));
			if (oppositeKingSquare != null) {
				AtomicBoolean attacksKing = new AtomicBoolean();
				forEachPlayerPiece(player, (playerPiece, square) -> attacksKing.set(attacksKing.get() || playerPiece.attacks(square, oppositeKingSquare, this)));
				if (attacksKing.get()) {
					move.setCheck();
				}
			}

			undoSingleMove(move);
		}
	}

	private void forEachPlayerPiece(Player player, BiConsumer<PlayerPiece, Square> action) {
		for (int index = 0; index < playerPiecesPerSquare.length; index++) {
			PlayerPiece playerPiece = playerPiecesPerSquare[index];
			if (playerPiece != null && playerPiece.getPlayer() == player) {
				action.accept(playerPiece, new Square(index));
			}
		}
	}

	private Square squareOf(PlayerPiece playerPiece) {
		for (int index = 0; index < playerPiecesPerSquare.length; index++) {
			if (playerPiece.equals(playerPiecesPerSquare[index])) {
				return new Square(index);
			}
		}
		return null;
	}

	public Move lastMove() {
		return lastMove;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(9*8);
		for (int row = 7; row >= 0; row--) {
			for (int column = 0; column < 8; column++) {
				char c = ((row ^ column) & 1) == 1 ? '*' : ' ';
				PlayerPiece playerPiece = playerPiecesPerSquare[new Square(column, row).boardIndex()];
				if (playerPiece != null) {
					c = formatPiece(playerPiece);
				}
				sb.append(c);
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private char formatPiece(PlayerPiece playerPiece) {
		char c;
		switch (playerPiece.getPiece()) {
			case PAWN: c = 'p'; break;
			case KNIGHT: c = 'k'; break;
			case BISHOP: c = 'b'; break;
			case ROOK: c = 'r'; break;
			case QUEEN: c = 'q'; break;
			case KING: c = 'k'; break;
			default: throw new IllegalArgumentException("Unknown piece found: " + playerPiece.getPiece());
		}

		switch (playerPiece.getPlayer()) {
			case WHITE: return c;
			case BLACK: return Character.toUpperCase(c);
			default: throw new IllegalArgumentException("Unknown player found: " + playerPiece.getPlayer());
		}
	}
}
