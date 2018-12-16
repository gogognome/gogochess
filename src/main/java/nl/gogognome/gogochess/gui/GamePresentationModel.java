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

import com.google.common.collect.ImmutableList;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
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

	private State state = INITIALIZING;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ArtificialIntelligence ai;

	private boolean whitePlayerAi = false;
	private boolean blackPlayerAi = true;

	private List<Square> targets;
	private int percentage;
	private final List<Consumer<Event>> listeners = new ArrayList<>();
	private List<Move> promotionMoves;

	private final ProgressListener progressListener = new ProgressListener()
			.withProgressUpdateConsumer(this::setPercentage)
			.withBestMovesConsumer(bestMoves -> logger.debug(bestMoves.stream().map(Move::toString).collect(joining(", "))));

	private long aiStartTime;
	private long aiMaxEndTime;
	private int maxDurationSeconds = 15;

	@Inject
	public GamePresentationModel(ArtificialIntelligence ai, Board board) {
		this.ai = ai;
		this.board = board;
		board.initBoard();
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
				ai.cancel();
			}

			onStartThinking();
		}
	}

	void playGame() {
		onStartThinking();
		fireEvent(Event.STATE_CHANGED);
	}

	private void computerThinking() {
		logger.debug("Start thinking...");
		try {
			aiStartTime = System.currentTimeMillis();
			aiMaxEndTime = aiStartTime + maxDurationSeconds * 1000;

			Board boardForArtificialIntelligence = new Board();
			boardForArtificialIntelligence.process(board.lastMove());
			Move move = ai.nextMove(
					boardForArtificialIntelligence,
					boardForArtificialIntelligence.currentPlayer(),
					progressListener);
			SwingUtilities.invokeLater(() -> onComputerMove(move));
		} catch (ArtificalIntelligenceCanceledException e) {
			logger.debug("Canceled thinking");
		} catch (Exception e) {
			logger.error("Problem occurred: " + e.getMessage(), e);
		}
	}

	private void setPercentage(Integer percentage) {
		if (this.percentage != percentage) {
			this.percentage = percentage;
			SwingUtilities.invokeLater(() -> fireEvent(Event.PERCENTAGE_CHANGED));
		}

		if (percentage > 10) {
			updateMaxDepthDelta(percentage);
		}
	}

	private void updateMaxDepthDelta(Integer percentage) {
		int maxDepthDelta = 0;
		int durationPercentage = (int) (100 * ((System.currentTimeMillis() - aiStartTime)) / (aiMaxEndTime - aiStartTime));
		if (durationPercentage > percentage) {
			maxDepthDelta--;
		}
		if (durationPercentage > 2 * percentage) {
			maxDepthDelta--;
		}
		if (durationPercentage > 4 * percentage) {
			maxDepthDelta--;
		}
		if (maxDepthDelta != progressListener.getMaxDepthDelta().get()) {
			progressListener.getMaxDepthDelta().set(maxDepthDelta);
			logger.info("Set max depth delta to " + maxDepthDelta + " because AI percentage is "
					+ percentage + "% and duration percentage is " + durationPercentage + "%.");
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
		long aiEndTime = System.currentTimeMillis();
		logger.debug("Computer has thought for " + (aiEndTime - aiStartTime) / 1000  + " seconds");
		targets = ImmutableList.of(
				move.getMutationRemovingPieceFromStart().getSquare(),
				move.getMutationAddingPieceAtDestination().getSquare());
		onMove(move);
	}

	private void onMove(Move move) {
		board.process(move);
		if (move.getStatus().isGameOver()) {
			changeStateTo(State.GAME_OVER);
		} else {
			onStartThinking();
		}
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
			executorService.submit(this::computerThinking);
			changeStateTo(State.COMPUTER_THINKING);
		} else {
			changeStateTo(State.WAITING_FOR_DRAG);
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
		ai.cancel();
		executorService.shutdownNow();
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
