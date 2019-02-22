package nl.gogognome.gogochess.gui;

import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.gui.GamePresentationModel.State.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import java.util.*;
import java.util.function.*;
import javax.inject.*;
import javax.swing.*;
import org.slf4j.*;
import com.google.common.collect.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

public class GamePresentationModel {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public enum Event {
		STATE_CHANGED,
		PERCENTAGE_CHANGED,
		SETTING_CHANGED
	}

	public enum State {
		INITIALIZING,
		COMPUTER_THINKING,
		WAITING_FOR_DRAG,
		DRAGGING,
		PROMOTING_WHITE_PAWN,
		PROMOTING_BLACK_PAWN,
		GAME_OVER
	}

	private final Board board;

	/** All moves made so far, including the board setup move. */
	private List<Move> moves = new ArrayList<>();
	private int lastMoveIndex;

	private State state = INITIALIZING;
	private final AiController aiController;

	private boolean whitePlayerAi = false;
	private boolean blackPlayerAi = false;

	private List<Square> targets;
	private int percentage;
	private final List<Consumer<Event>> listeners = new ArrayList<>();
	private List<Move> promotionMoves;

	@Inject
	public GamePresentationModel(AiController aiController, Board board) {
		this.aiController = aiController;
		this.board = board;

		this.aiController.setPercentageConsumer(this::setPercentage);
		this.aiController.setComputerMoveConsumer(this::onComputerMove);
	}

	void addListener(Consumer<Event> listener) {
		listeners.add(listener);
	}

	public Board getBoard() {
		return board;
	}

	/**
	 * @return all moves made so far, including the board setup move.
	 */
	public List<Move> getMoves() {
		return moves;
	}

	/**
	 * @return the index of the last move made. This index is at least zero and less than
	 * the sie of getMoves().
	 */
	int getLastMoveIndex() {
		return lastMoveIndex;
	}

	State getState() {
		return state;
	}

	List<Square> getTargets() {
		return targets;
	}

	int getPercentage() {
		return percentage;
	}

	boolean isWhitePlayerAi() {
		return whitePlayerAi;
	}

	boolean isBlackPlayerAi() {
		return blackPlayerAi;
	}

	boolean whiteHasWon() {
		return checkIfPlayerHasWon(WHITE);
	}

	boolean blackHasWon() {
		return checkIfPlayerHasWon(BLACK);
	}

	private boolean checkIfPlayerHasWon(Player winningPlayer) {
		return board.lastMove() != null
				&& board.lastMove().getStatus() == Status.CHECK_MATE
				&& board.lastMove().getPlayer() == winningPlayer;
	}

	AIThinkingLimit getThinkingLimit() {
		return aiController.getThinkingLimit();
	}

	void setThinkingLimit(AIThinkingLimit thinkingLimit) {
		aiController.setThinkingLimit(thinkingLimit);
		fireEvent(Event.SETTING_CHANGED);
	}

	void onWhitePlayerAI(boolean whitePlayerAi) {
		this.whitePlayerAi = whitePlayerAi;
		fireEvent(Event.SETTING_CHANGED);

		cancelOrStartThinkingFor(WHITE);
	}

	void onBlackPlayerAI(boolean blackPlayerAi) {
		this.blackPlayerAi = blackPlayerAi;
		fireEvent(Event.SETTING_CHANGED);

		cancelOrStartThinkingFor(BLACK);
	}

	private void cancelOrStartThinkingFor(Player player) {
		if (state == GAME_OVER) {
			return;
		}

		if (board.currentPlayer() == player) {
			cancelComputerThinking();

			onStartThinking();
		}
	}

	private void setPercentage(Integer percentage) {
		if (this.percentage != percentage) {
			this.percentage = percentage;
			SwingUtilities.invokeLater(() -> fireEvent(Event.PERCENTAGE_CHANGED));
		}
	}

	void onPlayerMove(Square startSquare, Square targetSquare) {
		targets = null;
		List<Move> moves = board.currentPlayer().validMoves(board).stream()
				.filter(m -> startAndTargetSquareMatchMove(m, startSquare, targetSquare))
				.collect(toList());
		switch (moves.size()) {
			case 0: onInvalidMove(); break;
			case 1: onMove(moves.get(0)); break;
			default: onPromote(moves); break;
		}
	}

	private boolean startAndTargetSquareMatchMove(Move move, Square startSquare, Square targetSquare) {
		String description = move.toString();
		if (description.equals("O-O")) {
			description = move.getPlayer() == WHITE ? E1 + "-" + G1 : E8 + "-" + G8;
		} else if (description.equals("O-O-O")) {
			description = move.getPlayer() == WHITE ? E1 + "-" + C1 : E8 + "-" + C8;
		}
		return description.contains(startSquare.toString()) && description.contains(targetSquare.toString())
				&& description.indexOf(startSquare.toString()) < description.indexOf(targetSquare.toString());
	}

	private void onInvalidMove() {
		changeStateTo(WAITING_FOR_DRAG);
	}

	private void onComputerMove(Move move) {
		SwingUtilities.invokeLater(() -> {
            highlightMove(move);
            onMove(move);
		});
	}

    void onMove(Move move) {
		lastMoveIndex += 1;
		while (moves.size() > lastMoveIndex) {
			moves.remove(moves.size()-1);
		}
		moves.add(move);
		board.process(move);
		if (move.getStatus().isGameOver()) {
			changeStateTo(GAME_OVER);
		} else {
			onStartThinking();
		}
	}

	void onUndoMove() {
		if (lastMoveIndex > 0) {
			showMove(lastMoveIndex - 1, lastMoveIndex);
		}
	}

	void onRedoMove() {
		if (lastMoveIndex + 1 < moves.size()) {
			showMove(lastMoveIndex + 1, lastMoveIndex + 1);
		}
	}

	private void showMove(int moveToShow, int indexOfHighlightedMove) {
		lastMoveIndex = moveToShow;
		cancelComputerThinking();
		setOnlyToHumanPlayers();
		highlightMove(moves.get(indexOfHighlightedMove));
		board.process(moves.get(lastMoveIndex));
		changeStateTo(moves.get(lastMoveIndex).getStatus().isGameOver() ? GAME_OVER : WAITING_FOR_DRAG);
		fireEvent(Event.SETTING_CHANGED); // because of possible change of computer thinking
	}

	/**
	 * Call this method to initialise the board and put the game in paused state.
	 */
	void init() {
		setOnlyToHumanPlayers();
		board.initBoard();
		moves.clear();
		moves.add(board.lastMove()); // add board setup move to the moves
		lastMoveIndex = 0;
		targets = null; // prevents showing the last move of previous game in new game
		onStartThinking();
		fireEvent(Event.SETTING_CHANGED);
		fireEvent(Event.STATE_CHANGED);
		fireEvent(Event.PERCENTAGE_CHANGED);
	}

	private void setOnlyToHumanPlayers() {
		whitePlayerAi = false;
		blackPlayerAi = false;
	}

	private void highlightMove(Move move) {
        targets = ImmutableList.of(
                move.getMutationRemovingPieceFromStart().getSquare(),
                move.getMutationAddingPieceAtDestination().getSquare());
    }

    private void onPromote(List<Move> promotionMoves) {
		this.promotionMoves = promotionMoves;
		changeStateTo(promotionMoves.get(0).getPlayer() == WHITE ? PROMOTING_WHITE_PAWN : PROMOTING_BLACK_PAWN);
	}

	void onPromoteTo(PlayerPiece selectedPiece) {
		Move selectedMove = promotionMoves.stream()
				.filter(move -> move.getMutationAddingPieceAtDestination().getPlayerPiece().equals(selectedPiece))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Could not find move that promotes to " + selectedPiece));
		promotionMoves = null;
		onMove(selectedMove);
	}
	
	private void onStartThinking() {
		if (board.currentPlayer() == WHITE && whitePlayerAi || board.currentPlayer() == BLACK && blackPlayerAi) {
			aiController.onStartThinking(board.lastMove());
			changeStateTo(COMPUTER_THINKING);
		} else {
			changeStateTo(WAITING_FOR_DRAG);
			if (board.currentPlayer() == WHITE && blackPlayerAi || board.currentPlayer() == BLACK && whitePlayerAi) {
				aiController.onStartThinkingDuringOpponentsTurn();
			}
		}
	}

	void onStartDragPiece(Square square) {
		PlayerPiece playerPiece = board.pieceAt(square);
		if (playerPiece != null && playerPiece.getPlayer() == board.currentPlayer()) {
			targets = determineTargetsForValidMoves(square);
			changeStateTo(DRAGGING);
		}
	}

	private List<Square> determineTargetsForValidMoves(Square startSquare) {
		return board.currentPlayer().validMoves(board).stream()
				// Move must remove piece from startSquare
				.filter(m -> m.getBoardMutations().stream()
						.anyMatch(mut -> mut.isRemoveFrom(startSquare)))
				// Find square where piece is added
				.map(m -> m.getBoardMutations().stream()
						.filter(mut -> mut.getMutation() == ADD)
						.map(BoardMutation::getSquare)
						.findFirst()
						.orElseThrow(() -> new IllegalStateException("Could not find destination of move " + m)))
				.collect(toList());
	}

	void onRestart() {
		init();
	}

	void onClose() {
		aiController.onClose();
	}

	private void cancelComputerThinking() {
		if (state == COMPUTER_THINKING) {
			aiController.cancelThinking();
		}
	}

	private void changeStateTo(State newState) {
		state = newState;
		fireEvent(Event.STATE_CHANGED);
	}

	private void fireEvent(Event event) {
		logEvent(event);
		listeners.forEach(l -> l.accept(event));
	}

	private void logEvent(Event event) {
		if (event == Event.PERCENTAGE_CHANGED) {
			return;
		}

		logger.debug("Fired event: " + event + (event == Event.STATE_CHANGED ? "New state: " + state : ""));
	}

}
