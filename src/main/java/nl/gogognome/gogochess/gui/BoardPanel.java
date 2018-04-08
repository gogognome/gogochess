package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.*;
import javax.inject.*;
import javax.swing.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;
import nl.gogognome.gogochess.logic.piece.*;

public class BoardPanel extends JPanel {

	private List<Square> targets;

	public static class DragData {
		private final Square startSquare;
		private final int deltaX;
		private final int deltaY;

		public DragData(Square startSquare, int deltaX, int deltaY) {
			this.startSquare = startSquare;
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}

		public Square getStartSquare() {
			return startSquare;
		}

		public DragData dragTo(int currentX, int currentY) {
			return new DragData(startSquare, currentX - deltaX, currentY - deltaY);
		}
	}

	private final int squareSize;
	private final int progressBarHeight;
	private final int margin;
	private final static Color[] SQUARE_COLORS = new Color[] { new Color(209,139, 71), new Color(255, 206, 158) };
	private final BufferedImage piecesImage;
	private final static Piece[] PIECES_IN_IMAGE = new Piece[] { KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN };
	private Map<Square, PlayerPiece> squareToPlayerPiece = new HashMap<>();
	private DragData dragData;
	private int percentage;

	private final int movesPanelWidth = 150;
	private final MoveNotation moveNotation;
	private List<String> moves;

	@Inject
	public BoardPanel(Board board, MoveNotation moveNotation, int squareSize) {
		this.squareSize = squareSize;
		this.moveNotation = moveNotation;
		this.progressBarHeight = squareSize / 2;
		this.margin = progressBarHeight / 10;
		initSquareToPlayerPiece(board);
		initMoves(board);

		try {
			piecesImage = ImageIO.read(getClass().getResourceAsStream("/pieces.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load pieces from the resources: " + e.getMessage(), e);
		}
	}

	public void updateBoard(Board board) {
		updateBoard(board, null);
	}

	public void updateBoard(Board board, DragData dragData) {
		initSquareToPlayerPiece(board);
		initMoves(board);
		this.dragData = dragData;
		repaint();
	}

	private void initSquareToPlayerPiece(Board board) {
		squareToPlayerPiece.clear();
		for (int y=0; y<8; y++) {
			for (int x = 0; x < 8; x++) {
				Square square = new Square(x, y);
				squareToPlayerPiece.put(square, board.pieceAt(square));
			}
		}
	}

	private void initMoves(Board board) {
		moves = new LinkedList<>();
		Move move = board.lastMove();
		while (move != null && move.getPrecedingMove() != null) {
			moves.add(0, moveNotation.format(move));
			move = move.getPrecedingMove();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(8 * squareSize + movesPanelWidth,8 * squareSize + progressBarHeight + margin);
	}

	@Override
	public void paint(Graphics g) {
		paintBoardAndPiecesExceptDraggedPiece(g);
		paintDraggedPiece(g);
		paintProgressBar(g);
		paintPanelWithMoves(g);
	}

	private void paintBoardAndPiecesExceptDraggedPiece(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);

		for (int y=0; y<8; y++) {
			for (int x=0; x<8; x++) {
				Square square = new Square(x, y);
				Color squareColor = SQUARE_COLORS[(x + y) % 2];
				if (targets != null && targets.contains(square)) {
					squareColor = squareColor.darker();
				}
				g.setColor(squareColor);
				g.fillRect(left(x), top(y), squareSize, squareSize);

				if (dragData != null && dragData.startSquare.equals(square)) {
					continue;
				}
				paintPieceAtSquare(g, square, 0, 0);
			}
		}
	}

	private void paintDraggedPiece(Graphics g) {
		if (dragData != null) {
			paintPieceAtSquare(g, dragData.startSquare, dragData.deltaX, dragData.deltaY);
		}
	}

	private void paintPieceAtSquare(Graphics g, Square square, int deltaX, int deltaY) {
		PlayerPiece playerPiece = squareToPlayerPiece.get(square);
		if (playerPiece != null) {
			g.drawImage(
					piecesImage,
					left(square.column()) + deltaX, top(square.row()) + deltaY, left(square.column()+1) + deltaX, top(square.row()-1) + deltaY,
					pieceLeft(playerPiece), pieceTop(playerPiece), pieceRight(playerPiece), pieceBottom(playerPiece),
					null);
		}
	}

	private void paintProgressBar(Graphics g) {
		g.setColor(Color.BLACK);
		int boardSize = 8 * squareSize;
		int arcHeight = 2 * margin;
		g.fillRoundRect(0, boardSize + margin, boardSize, progressBarHeight, arcHeight, arcHeight);
		g.setColor(Color.BLUE);
		g.fillRoundRect(0, boardSize + margin, boardSize * percentage / 100, progressBarHeight, arcHeight, arcHeight);
	}

	private int pieceLeft(PlayerPiece playerPiece) {
		return columnOfPlayerPiece(playerPiece) * piecesImage.getWidth() / PIECES_IN_IMAGE.length;
	}

	private int pieceTop(PlayerPiece playerPiece) {
		return playerPiece.getPlayer() == WHITE ? 0 : piecesImage.getHeight() / 2;
	}

	private int pieceRight(PlayerPiece playerPiece) {
		return (columnOfPlayerPiece(playerPiece) + 1) * piecesImage.getWidth() / PIECES_IN_IMAGE.length;
	}

	private int pieceBottom(PlayerPiece playerPiece) {
		return playerPiece.getPlayer() == WHITE ? piecesImage.getHeight() / 2 : piecesImage.getHeight();
	}

	private int columnOfPlayerPiece(PlayerPiece playerPiece) {
		Piece piece = playerPiece.getPiece();
		for (int i = 0; i<PIECES_IN_IMAGE.length; i++) {
			if (piece == PIECES_IN_IMAGE[i]) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unknown piece encountered: " + piece);
	}

	private int left(int x) {
		return x * squareSize;
	}

	private int top(int y) {
		return (7 - y) * squareSize;
	}

	public Square getSquare(int x, int y) {
		return new Square(x / squareSize, 7 - y/squareSize);
	}

	public void setTargets(List<Square> targets) {
		this.targets = targets;
	}

	public void updatePercentage(int percentage) {
		if (this.percentage != percentage) {
			this.percentage = percentage;
			repaint();
		}
	}

	private void paintPanelWithMoves(Graphics g) {
		int left = 8 * squareSize + margin;
		g.setColor(Color.BLACK);
		g.fillRect(left, 0, movesPanelWidth, 8*squareSize);

		g.setColor(Color.LIGHT_GRAY);
		Player player = WHITE;
		int y = margin + g.getFontMetrics().getHeight();
		for (String move : moves) {
			int textX =  player == WHITE ? left + margin : left + movesPanelWidth / 2 + margin;
			g.drawString(move, textX, y);
			player = player.other();
			if (player == WHITE) {
				y += g.getFontMetrics().getHeight() * 150 / 100;
			}
		}
	}
}
