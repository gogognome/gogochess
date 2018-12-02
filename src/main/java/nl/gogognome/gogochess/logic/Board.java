package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.piece.*;

public class Board {

	private Move lastMove;

	private final PlayerPiece[] playerPiecesPerSquare = new PlayerPiece[8*8];
	private final Square[] whitePieceSquares = new Square[2*8];
	private int nrWhitePieces;
	private final Square[] blackPieceSquares = new Square[2*8];
	private int nrBlackPieces;
	private final BoardHash boardHash = new BoardHash();
	private final Map<Long, Integer> hashToNumberOfRepetitions = new HashMap<>();

	public void process(BoardMutation... boardMutations) {
		List<Move> moves = lastMove.getPlayer().opponent().validMoves(this);
		List<BoardMutation> boardMutationsList = asList(boardMutations);
		Optional<Move> moveIncludingStatus = moves.stream()
				.filter(m -> m.getBoardMutations().containsAll(boardMutationsList))
				.findFirst();
		process(moveIncludingStatus.orElseThrow(() -> new IllegalArgumentException("Board mutations " + Arrays.toString(boardMutations) + " not found in moves: " + moves)));
	}

	public void process(Move move) {
		Move commonAncestor = Move.findCommonAncestor(lastMove, move);
		undoUntil(commonAncestor);
		processForwardUntil(move, commonAncestor);
	}

	private void undoUntil(Move commonAncestor) {
		while (lastMove != commonAncestor) {
			undoSingleMove(lastMove);
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

	void tryWithMove(Move move, Runnable runnable) {
		try {
			processSingleMove(move);
			runnable.run();
		} finally {
			undoSingleMove(move);
		}
	}

	private void processSingleMove(Move move) {
		for (BoardMutation boardMutation : move.getBoardMutations()) {
			process(boardMutation);
		}
		lastMove = move;
		updateRepetitionCount(count -> count + 1);
	}

	private void undoSingleMove(Move move) {
		updateRepetitionCount(count -> count-1);
		List<BoardMutation> boardMutations = move.getBoardMutations();
		for (int i=boardMutations.size() - 1; i >=0; i--) {
			undo(boardMutations.get(i));
		}
		lastMove = move.getPrecedingMove();
	}

	private void updateRepetitionCount(Function<Integer, Integer> countChanger) {
		long hash = getBoardHash();
		int count = hashToNumberOfRepetitions.getOrDefault(hash, 0);
		count = countChanger.apply(count);
		if (count > 0) {
			hashToNumberOfRepetitions.put(hash, count);
		} else {
			hashToNumberOfRepetitions.remove(hash);
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

		if (playerPieceToAdd.getPlayer() == WHITE) {
			whitePieceSquares[nrWhitePieces++] = square;
		} else {
			blackPieceSquares[nrBlackPieces++] = square;
		}

		boardHash.addPlayerPiece(playerPieceToAdd, square);
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

		if (playerPieceToRemove.getPlayer() == WHITE) {
			for (int i=0; i<nrWhitePieces; i++) {
				if (whitePieceSquares[i].equals(square)) {
					whitePieceSquares[i] = whitePieceSquares[--nrWhitePieces];
					break;
				}
			}
		} else {
			for (int i=0; i<nrBlackPieces; i++) {
				if (blackPieceSquares[i].equals(square)) {
					blackPieceSquares[i] = blackPieceSquares[--nrBlackPieces];
					break;
				}
			}
		}

		boardHash.removePlayerPiece(playerPieceToRemove, square);
	}

	/**
	 * Process a move, call the supplier and undo the move.
	 *
	 * @param move the move
	 * @param supplier the supplier
	 * @param <T> type of the return value
	 * @return the value provided by the supplier
	 */
	public <T> T temporarilyMove(Move move, Supplier<T> supplier) {
		Move lastMove = lastMove();
		process(move);

		T value = supplier.get();

		process(lastMove);
		return value;
	}

	public PlayerPiece pieceAt(Square square) {
		return playerPiecesPerSquare[square.boardIndex()];
	}

	/**
	 * Checks if the square is empty.
	 * @param square the square
	 * @return true if the square contains no piece; false if the square contains a piece
	 */
	public boolean empty(Square square) {
		return pieceAt(square) == null;
	}

	public boolean empty(int column, int row) {
		return playerPiecesPerSquare[column*8 + row] == null;
	}

	public Square kingSquareOf(Player player) {
		King king = new King(player);
		if (player == WHITE) {
			for (int i=0; i<nrWhitePieces; i++) {
				if (king.equals(playerPiecesPerSquare[whitePieceSquares[i].boardIndex()])) {
					return whitePieceSquares[i];
				}
			}
		} else {
			for (int i=0; i<nrBlackPieces; i++) {
				if (king.equals(playerPiecesPerSquare[blackPieceSquares[i].boardIndex()])) {
					return blackPieceSquares[i];
				}
			}
		}
		return null;
	}

	public void forEachPlayerPiece(Player player, BiConsumer<PlayerPiece, Square> action) {
		forEachPlayerPiece(player, action, () -> false);
	}

	void forEachPlayerPiece(Player player, BiConsumer<PlayerPiece, Square> action, Supplier<Boolean> terminateIf) {
		Square[] tempSquares;
		int nrTempSquares;
		if (player == WHITE) {
			tempSquares = new Square[nrWhitePieces];
			System.arraycopy(whitePieceSquares, 0, tempSquares, 0, nrWhitePieces);
			nrTempSquares = nrWhitePieces;
		} else {
			tempSquares = new Square[nrBlackPieces];
			System.arraycopy(blackPieceSquares, 0, tempSquares, 0, nrBlackPieces);
			nrTempSquares = nrBlackPieces;
		}
		for (int i=0; !terminateIf.get() && i<nrTempSquares; i++) {
			Square square = tempSquares[i];
			action.accept(playerPiecesPerSquare[square.boardIndex()], square);
		}
	}

	public Move lastMove() {
		return lastMove;
	}

	public Player currentPlayer() {
		return currentPlayerOpponent().opponent();
	}
	
	public Player currentPlayerOpponent() {
		if (lastMove == null) {
			throw new IllegalStateException("No moves can be determined when the board is empty");
		}
		return lastMove().getPlayer();
	}

	/**
	 * Checks if any piece of the specified player attacks the specified square
	 * @param player the player
	 * @param square the square
	 * @return true if one or more pieces of the player attack the square; false otherwise
	 */
	public boolean anyPieceAttacks(Player player, Square square) {
		AtomicBoolean attacked = new AtomicBoolean();
		forEachPlayerPiece(player, (playerPiece, pieceSquare) -> attacked.set(attacked.get() || playerPiece.attacks(pieceSquare, square, this)), attacked::get);
		return attacked.get();
	}

	public int numberNonPawnPieces() {
		int count = 0;
		for (int i=0; i<playerPiecesPerSquare.length; i++) {
			PlayerPiece playerPiece = playerPiecesPerSquare[i];
			if (playerPiece != null && playerPiece.getPiece() != PAWN) {
				count++;
			}
		}
		return count;
	}

	public int countNrOccurrencesInColumn(PlayerPiece playerPiece, int column) {
		int nrPawns = 0;
		for (int row=0; row<8; row++) {
			if (playerPiece.equals(pieceAt(new Square(column, row)))) {
				nrPawns++;
			}
		}
		return nrPawns;
	}

	/**
	 * Determines if a pawn in the specified column is isolated, i.e., a pawn that has no pawns of the same player
	 * in adjacent columns.
	 * @param player the player whose pawn must be checked
	 * @param column the column containing the pawn
	 * @return true if the pawn is isolated; false otherwise
	 */
	public boolean isIsolatedPawnInColumn(Player player, int column) {
		PlayerPiece pawn = new Pawn(player);
		int nrPawnsInAdjacentColumns = 0;
		if (column > 0) {
			nrPawnsInAdjacentColumns += countNrOccurrencesInColumn(pawn, column - 1);
		}
		if (column < 7) {
			nrPawnsInAdjacentColumns += countNrOccurrencesInColumn(pawn, column + 1);
		}
		return nrPawnsInAdjacentColumns == 0;
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
			case KNIGHT: c = 'n'; break;
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

	public long getBoardHash() {
		return boardHash.getHash(lastMove.getPlayer());
	}

	int getNumberOfRepetitionsOfCurrentPosition() {
		return hashToNumberOfRepetitions.get(getBoardHash());
	}

	@Override
	public int hashCode() {
		return (int) boardHash.getHash(lastMove.getPlayer());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != Board.class) {
			return false;
		}

		Board that = (Board) obj;
		return this.hashCode() == that.hashCode();
	}

	public void initBoard() {
		process(INITIAL_BOARD);
	}

	final static Move INITIAL_BOARD = new Move(BLACK,
			WHITE_ROOK.addTo(A1),
			WHITE_KNIGHT.addTo(B1),
			WHITE_BISHOP.addTo(C1),
			WHITE_QUEEN.addTo(D1),
			WHITE_KING.addTo(E1),
			WHITE_BISHOP.addTo(F1),
			WHITE_KNIGHT.addTo(G1),
			WHITE_ROOK.addTo(H1),
			WHITE_PAWN.addTo(A2),
			WHITE_PAWN.addTo(B2),
			WHITE_PAWN.addTo(C2),
			WHITE_PAWN.addTo(D2),
			WHITE_PAWN.addTo(E2),
			WHITE_PAWN.addTo(F2),
			WHITE_PAWN.addTo(G2),
			WHITE_PAWN.addTo(H2),
			BLACK_PAWN.addTo(A7),
			BLACK_PAWN.addTo(B7),
			BLACK_PAWN.addTo(C7),
			BLACK_PAWN.addTo(D7),
			BLACK_PAWN.addTo(E7),
			BLACK_PAWN.addTo(F7),
			BLACK_PAWN.addTo(G7),
			BLACK_PAWN.addTo(H7),
			BLACK_ROOK.addTo(A8),
			BLACK_KNIGHT.addTo(B8),
			BLACK_BISHOP.addTo(C8),
			BLACK_QUEEN.addTo(D8),
			BLACK_KING.addTo(E8),
			BLACK_BISHOP.addTo(F8),
			BLACK_KNIGHT.addTo(G8),
			BLACK_ROOK.addTo(H8));

}
