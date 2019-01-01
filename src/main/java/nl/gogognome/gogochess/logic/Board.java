package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Square.*;
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
			for (int i=nrWhitePieces-1; i>=0; i--) {
				if (whitePieceSquares[i].equals(square)) {
					whitePieceSquares[i] = whitePieceSquares[--nrWhitePieces];
					break;
				}
			}
		} else {
			for (int i=nrBlackPieces-1; i>=0; i--) {
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

	/**
	 * Checks if the square is empty.
	 * @param file the file in the range [0..7]
	 * @param rank the rank in the range [0..7]
	 * @return true if the square contains no piece; false if the square contains a piece
	 */
	public boolean empty(int file, int rank) {
		return playerPiecesPerSquare[file*8 + rank] == null;
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

	public void forEachPlayerPieceWhere(Player player, BiPredicate<PlayerPiece, Square> where, BiConsumer<PlayerPiece, Square> action) {
		BiConsumer<PlayerPiece, Square> actionForMatchingPlayerPieceAndSquare = (playerPiece, square) -> {
			if (where.test(playerPiece, square)) action.accept(playerPiece, square);
		};
		forEachPlayerPiece(player, actionForMatchingPlayerPieceAndSquare, () -> false);
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
		return (int) Arrays.stream(playerPiecesPerSquare)
				.filter(playerPiece -> playerPiece != null && playerPiece.getPiece() != PAWN)
				.count();
	}

	public int countPiecesWhere(Predicate<PlayerPiece> mustCountPlayerPiece) {
		return (int) Arrays.stream(playerPiecesPerSquare)
				.filter(playerPiece -> playerPiece != null && mustCountPlayerPiece.test(playerPiece))
				.count();
	}

	public int countNrOccurrencesInFile(PlayerPiece playerPiece, int file) {
		int nrPawns = 0;
		for (int rank=0; rank<8; rank++) {
			if (playerPiece.equals(pieceAt(new Square(file, rank)))) {
				nrPawns++;
			}
		}
		return nrPawns;
	}

	/**
	 * Determines if a pawn in the specified file is isolated, i.e., a pawn that has no pawns of the same player
	 * in adjacent files.
	 * @param player the player whose pawn must be checked
	 * @param file the file containing the pawn
	 * @return true if the pawn is isolated; false otherwise
	 */
	public boolean isIsolatedPawnInFile(Player player, int file) {
		PlayerPiece pawn = new Pawn(player);
		int nrPawnsInAdjacentColumns = 0;
		if (file > 0) {
			nrPawnsInAdjacentColumns += countNrOccurrencesInFile(pawn, file - 1);
		}
		if (file < 7) {
			nrPawnsInAdjacentColumns += countNrOccurrencesInFile(pawn, file + 1);
		}
		return nrPawnsInAdjacentColumns == 0;
	}

	/**
	 * Determines if the pawn at the specified square is a passed pawn. A passed pawn is a pawn with no opposing pawns
	 * to prevent it from advancing to the eighth rank; i.e. there are no opposing pawns in front of it on either
	 * the same file or adjacent files.
	 * @param pawn the pawn
	 * @param square the square of the pawn
 	 * @return true if the pawn is a passed pawn; false otherwise.
	 */
	public boolean isPassedPawn(PlayerPiece pawn, Square square) {
		if (pawn.getPiece() != PAWN) {
			throw new IllegalArgumentException("Expected a pawn as piece, but got a " + pawn.getPiece());
		}
		int delta = pawn.getPlayer() == WHITE ? 1 : -1;
		int finalRank = pawn.getPlayer() == WHITE ? RANK_8 : RANK_1;
		for (int file = Math.max(FILE_A, square.file() - 1); file <= Math.min(FILE_H, square.file() + 1); file++) {
			for (int rank = square.rank() + delta; rank != finalRank; rank += delta) {
				PlayerPiece otherPiece = pieceAt(new Square(file, rank));
				if (otherPiece != null && otherPiece.getPiece() == PAWN && otherPiece.getPlayer() != pawn.getPlayer()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param square a square
	 * @return true if the square is behind a passed pawn and all fields between square and the passed pawn are empty.
	 *         The specified square does not have to be empty.
	 */
	public boolean isBehindPassedPawn(Square square) {
		return isBehindPassedPawn(square, WHITE_PAWN) || isBehindPassedPawn(square, BLACK_PAWN);
	}

	private boolean isBehindPassedPawn(Square square, PlayerPiece pawn) {
		int deltaRanks = pawn.getPlayer() == WHITE ? 1 : -1;
		for (Square currentSquare = square.addRanks(deltaRanks); currentSquare != null; currentSquare = currentSquare.addRanks(deltaRanks)) {
			PlayerPiece playerPiece = pieceAt(currentSquare);
			if (pawn.equals(playerPiece) && isPassedPawn(playerPiece, currentSquare)) {
				return true;
			}
			if (playerPiece != null) {
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(9*8);
		for (int rank = 7; rank >= 0; rank--) {
			for (int file = 0; file < 8; file++) {
				char c = ((rank ^ file) & 1) == 1 ? '*' : ' ';
				PlayerPiece playerPiece = playerPiecesPerSquare[new Square(file, rank).boardIndex()];
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

	public void initBoard() {
		process(INITIAL_BOARD);
	}

	public boolean gameStartedFromInitialSetup() {
		if (lastMove == null) {
			return false;
		}
		Move move = lastMove;
		while (move.getPrecedingMove() != null) {
			move = move.getPrecedingMove();
		}
		return move.boardMutationsEqual(INITIAL_BOARD);
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

	public final static Move INITIAL_BOARD = new Move(BLACK,
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
