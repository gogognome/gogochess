package nl.gogognome.gogochess.gui;

import com.google.common.collect.ImmutableList;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.PlayerPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static nl.gogognome.gogochess.gui.GamePresentationModel.State.INITIALIZING;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.ADD;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;

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

	private State state = INITIALIZING;
	private final AiController aiController;

	private boolean whitePlayerAi = false;
	private boolean blackPlayerAi = true;

	private List<Square> targets;
	private int percentage;
	private final List<Consumer<Event>> listeners = new ArrayList<>();
	private List<Move> promotionMoves;

	@Inject
	public GamePresentationModel(AiController aiController, Board board) {
		this.aiController = aiController;
		this.board = board;
		board.initBoard();

		this.aiController.setPercentageConsumer(this::setPercentage);
		this.aiController.setComputerMoveConsumer(this::onComputerMove);
	}

	void addListener(Consumer<Event> listener) {
		listeners.add(listener);
	}

	public Board getBoard() {
		return board;
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
		if (board.currentPlayer() == player) {
			if (state == State.COMPUTER_THINKING) {
				aiController.cancelThinking();
			}

			onStartThinking();
		}
	}

	void playGame() {
		onStartThinking();
		fireEvent(Event.STATE_CHANGED);
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
			description = move.getPlayer() == WHITE ?  E1 + "-" + G1 : E8 + "-" + G8;
		} else if (description.equals("O-O-O")) {
			description = move.getPlayer() == WHITE ?  E1 + "-" + C1 : E8 + "-" + C8;
		}
		return description.contains(startSquare.toString()) && description.contains(targetSquare.toString())
				&& description.indexOf(startSquare.toString()) < description.indexOf(targetSquare.toString());
	}

	private void onInvalidMove() {
		changeStateTo(State.WAITING_FOR_DRAG);
	}

	private void onComputerMove(Move move) {
		SwingUtilities.invokeLater(() -> {
            highlightMove(move);
            onMove(move);
		});
	}

    private void onMove(Move move) {
		board.process(move);
		if (move.getStatus().isGameOver()) {
			changeStateTo(State.GAME_OVER);
		} else {
			onStartThinking();
		}
	}

	void onUndoMove() {
		if (state == State.COMPUTER_THINKING) {
			aiController.cancelThinking();
		}
		if (board.lastMove() != null && board.lastMove().getPrecedingMove() != null) {
            highlightMove(board.lastMove());
            onMove(board.lastMove().getPrecedingMove());
		}
	}

	private void highlightMove(Move move) {
        targets = ImmutableList.of(
                move.getMutationRemovingPieceFromStart().getSquare(),
                move.getMutationAddingPieceAtDestination().getSquare());
    }


    private void onPromote(List<Move> promotionMoves) {
		this.promotionMoves = promotionMoves;
		changeStateTo(promotionMoves.get(0).getPlayer() == WHITE ? State.PROMOTING_WHITE_PAWN : State.PROMOTING_BLACK_PAWN);
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
			changeStateTo(State.COMPUTER_THINKING);
		} else {
			changeStateTo(State.WAITING_FOR_DRAG);
			if (board.currentPlayer() == WHITE && blackPlayerAi || board.currentPlayer() == BLACK && whitePlayerAi) {
				aiController.onStartThinkingDuringOpponentsTurn();
			}
		}
	}

	void onStartDragPiece(Square square) {
		PlayerPiece playerPiece = board.pieceAt(square);
		if (playerPiece != null && playerPiece.getPlayer() == board.currentPlayer()) {
			targets = determineTargetsForValidMoves(square);
			changeStateTo(State.DRAGGING);
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

	void onClose() {
		aiController.onClose();
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

		logger.debug("Fired event: " + event);

		if (event == Event.STATE_CHANGED) {
			logger.debug("New state: " + state);
		}
	}

}
