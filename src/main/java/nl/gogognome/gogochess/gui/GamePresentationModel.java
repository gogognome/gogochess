package nl.gogognome.gogochess.gui;

import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.gui.GamePresentationModel.State.INITIALIZING;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Squares.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import javax.inject.*;
import javax.swing.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.piece.*;

public class GamePresentationModel {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public enum Event {
		STATE_CHANGED,
		DRAGGING_PIECE,
		PERCENTAGE_CHANGED,
		SETTING_CHANGED
	}

	public enum State {
		INITIALIZING,
		COMPUTER_THINKING,
		WAITING_FOR_DRAG,
		DRAGGING,
		GAME_OVER
	}

	private final Board board;

	private final List<Move> moves = new ArrayList<>();
	private State state = INITIALIZING;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ArtificialIntelligence ai;

	private boolean whitePlayerAi = false;
	private boolean blackPlayerAi = true;

	private List<Square> targets;
	private int percentage;
	private final List<Consumer<Event>> listeners = new ArrayList<>();

	@Inject
	public GamePresentationModel(ArtificialIntelligence ai, Board board) {
		this.ai = ai;
		this.board = board;
		board.process(Move.INITIAL_BOARD);
	}

	public void addListener(Consumer<Event> listener) {
		listeners.add(listener);
	}

	public Board getBoard() {
		return board;
	}

	public State getState() {
		return state;
	}

	public List<Square> getTargets() {
		return targets;
	}

	public int getPercentage() {
		return percentage;
	}

	public boolean isWhitePlayerAi() {
		return whitePlayerAi;
	}

	public boolean isBlackPlayerAi() {
		return blackPlayerAi;
	}

	public void onWhitePlayerAI(boolean whitePlayerAi) {
		this.whitePlayerAi = whitePlayerAi;
		if (state == State.COMPUTER_THINKING) {
			ai.cancel();
		}
		fireEvent(Event.SETTING_CHANGED);
		onStartThinking();
	}

	public void onBlackPlayerAI(boolean blackPlayerAi) {
		this.blackPlayerAi = blackPlayerAi;
		if (state == State.COMPUTER_THINKING) {
			ai.cancel();
		}
		fireEvent(Event.SETTING_CHANGED);
		onStartThinking();
	}

	public void playGame() {
		board.process(Move.INITIAL_BOARD);
		onStartThinking();
		fireEvent(Event.STATE_CHANGED);
	}

	private void computerThinking() {
		try {
			Move move = ai.nextMove(
					board,
					board.currentPlayer(),
					percentage -> setPercentage(percentage),
					bestMoves -> logger.debug(bestMoves.stream().map(m -> m.toString()).collect(joining(", "))));
			SwingUtilities.invokeLater(() -> onMove(move));
		} catch (ArtificalIntelligenceCanceledException e) {
			// ignored intentionally
		} catch (Exception e) {
			logger.error("Problem occurred: " + e.getMessage(), e);
		}
	}

	private void setPercentage(Integer percentage) {
		if (this.percentage != percentage) {
			this.percentage = percentage;
			SwingUtilities.invokeLater(() -> fireEvent(Event.PERCENTAGE_CHANGED));
		}
	}

	public void onPlayerMove(Square startSquare, Square targetSquare) {
		targets = null;
		Optional<Move> move = board.validMoves().stream()
				.filter(m -> startAndTargetSquareMatchMove(m, startSquare, targetSquare))
				.findFirst();
		if (move.isPresent()) {
			onMove(move.get());
		} else {
			onInvalidMove();
		}
	}

	private boolean startAndTargetSquareMatchMove(Move move, Square startSquare, Square targetSquare) {
		String description = move.toString();
		if (description.equals("O-O")) {
			description = move.getPlayer() == WHITE ?  E1 + "-" + B1 : E8 + "-" + B8;
		} else if (description.equals("O-O-O")) {
			description = move.getPlayer() == WHITE ?  E1 + "-" + G1 : E8 + "-" + G8;
		}
		return description.contains(startSquare.toString()) && description.contains(targetSquare.toString())
				&& description.indexOf(startSquare.toString()) < description.indexOf(targetSquare.toString());
	}

	private void onInvalidMove() {
		state = State.WAITING_FOR_DRAG;
		fireEvent(Event.STATE_CHANGED);
	}

	private void onMove(Move move) {
		board.process(move);
		moves.add(move);
		if (move.getStatus().isGameOver()) {
			state = State.GAME_OVER;
			fireEvent(Event.STATE_CHANGED);
		} else {
			onStartThinking();
		}
	}

	private void onStartThinking() {
		if (board.currentPlayer() == WHITE && whitePlayerAi || board.currentPlayer() == BLACK && blackPlayerAi) {
			state = State.COMPUTER_THINKING;
			executorService.submit(this::computerThinking);
		} else {
			state = State.WAITING_FOR_DRAG;
		}
		fireEvent(Event.STATE_CHANGED);
	}

	public void onStartDragPiece(Square square) {
		PlayerPiece playerPiece = board.pieceAt(square);
		if (playerPiece != null && playerPiece.getPlayer() == board.currentPlayer()) {
			state = State.DRAGGING;
			targets = determineTargetsForValidMoves(square);
			fireEvent(Event.DRAGGING_PIECE);
		}
	}

	private List<Square> determineTargetsForValidMoves(Square startSquare) {
		return board.validMoves().stream()
				// Move must remove piece from startSquare
				.filter(m -> m.getBoardMutations().stream()
						.anyMatch(mut -> mut.isRemoveFrom(startSquare)))
				// Find square where piece is added
				.map(m -> m.getBoardMutations().stream()
						.filter(mut -> mut.getMutation() == ADD)
						.map(BoardMutation::getSquare).findFirst().get())
				.collect(toList());
	}

	public void onClose() {
		ai.cancel();
		executorService.shutdownNow();
	}

	private void fireEvent(Event event) {
		listeners.forEach(l -> l.accept(event));
	}

}
