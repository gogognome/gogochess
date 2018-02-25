package nl.gogognome.gogochess.gui;

import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
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

	private final Board board = new Board();
	private final BoardPanel boardPanel;

	private final Player computerPlayer;
	private State state;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private MiniMaxAlphaBetaPruningArtificialIntelligence ai = new MiniMaxAlphaBetaPruningArtificialIntelligence(5, 2, 0);

	private DragData dragData;

	public BoardController(Player computerPlayer) {
		this.computerPlayer = computerPlayer;
		boardPanel = new BoardPanel(board, 100);
		MouseListener mouseListener = new MouseListener();
		boardPanel.addMouseListener(mouseListener);
		boardPanel.addMouseMotionListener(mouseListener);
		state = computerPlayer == Player.WHITE ? State.COMPUTER_THINKING : State.WAITING_FOR_DRAG;
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public void playGame() {
		onMove(Move.INITIAL_BOARD);
	}

	private void computerThinking() {
		Move move = ai.nextMove(board, board.currentPlayer());
		SwingUtilities.invokeLater(() -> onMove(move));
	}

	private void onPlayerMove(Square startSquare, Square targetSquare) {
		Optional<Move> move = board.validMoves().stream()
				.filter(m -> m.toString().contains(startSquare.toString()) && m.toString().contains(targetSquare.toString()))
				.findFirst();
		if (move.isPresent()) {
			onMove(move.get());
		} else {
			onInvalidMove();
		}
	}

	private void onInvalidMove() {
		state = State.WAITING_FOR_DRAG;
		boardPanel.updateBoard(board);
	}

	private void onMove(Move move) {
		board.process(move);
		if (move.getStatus().isGameOver()) {
			state = State.GAME_OVER;
		} else {
			if (board.currentPlayer() == computerPlayer) {
				state = State.COMPUTER_THINKING;
				executorService.submit(() -> computerThinking());
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
			boardPanel.updateBoard(board, dragData.dragTo(x, y));
		}
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

}
