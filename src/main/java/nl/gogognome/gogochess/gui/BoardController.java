package nl.gogognome.gogochess.gui;

import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.BoardMutation.Mutation.*;
import static nl.gogognome.gogochess.logic.Player.BLACK;
import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.inject.*;
import javax.swing.*;
import nl.gogognome.gogochess.gui.BoardPanel.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.piece.*;

public class BoardController {

	private enum State {
		COMPUTER_THINKING,
		WAITING_FOR_DRAG,
		DRAGGING,
		GAME_OVER
	}

	private final Board board;
	private final BoardPanel boardPanel;

	private final List<Move> moves = new ArrayList<>();
	private final Player computerPlayer;
	private State state;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ArtificialIntelligence ai;

	private DragData dragData;

	@Inject
	public BoardController(OpeningsDatabaseArtificialIntelligenceWrapper ai, Board board, BoardPanel boardPanel) {
		this.ai = ai;
		this.board = board;
		this.computerPlayer = BLACK;
		this.boardPanel = boardPanel;
		MouseListener mouseListener = new MouseListener();
		boardPanel.addMouseListener(mouseListener);
		boardPanel.addMouseMotionListener(mouseListener);

		state = computerPlayer == WHITE ? State.COMPUTER_THINKING : State.WAITING_FOR_DRAG;
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public void playGame() {
		onMove(Move.INITIAL_BOARD);
	}

	private void computerThinking() {
		try {
			Move move = ai.nextMove(
					board,
					board.currentPlayer(),
					percentage -> boardPanel.updatePercentage(percentage),
					bestMoves -> System.out.println(bestMoves));
			SwingUtilities.invokeLater(() -> onMove(move));
		} catch (ArtificalIntelligenceCanceledException e) {
			// ignored intentionally
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void onPlayerMove(Square startSquare, Square targetSquare) {
		boardPanel.setTargets(null);
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
		boardPanel.updateBoard(board);
	}

	private void onMove(Move move) {
		board.process(move);
		moves.add(move);
		if (move.getStatus().isGameOver()) {
			state = State.GAME_OVER;
		} else {
			if (board.currentPlayer() == computerPlayer) {
				state = State.COMPUTER_THINKING;
				executorService.submit(this::computerThinking);
			} else {
				state = State.WAITING_FOR_DRAG;
			}
		}
		boardPanel.updateBoard(board);
	}

	private void onStartDragPiece(int x, int y) {
		Square square = boardPanel.getSquare(x, y);
		PlayerPiece playerPiece = board.pieceAt(square);
		if (playerPiece != null && playerPiece.getPlayer() == board.currentPlayer()) {
			dragData = new DragData(square, x, y);
			state = State.DRAGGING;
			boardPanel.setTargets(determineTargetsForValidMoves(square));
			boardPanel.updateBoard(board, dragData.dragTo(x, y));
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

	private class MouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (state == State.WAITING_FOR_DRAG) {
				onStartDragPiece(e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (state == State.DRAGGING) {
				boardPanel.updateBoard(board, dragData.dragTo(e.getX(), e.getY()));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (state == State.DRAGGING) {
				Square targetSquare = boardPanel.getSquare(e.getX(), e.getY());
				onPlayerMove(dragData.getStartSquare(), targetSquare);
			}
		}
	}

	public void onClose() {
		ai.cancel();
		executorService.shutdownNow();
	}

}
