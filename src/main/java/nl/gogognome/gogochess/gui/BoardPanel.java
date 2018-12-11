package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.gui.GamePresentationModel.State.*;
import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static nl.gogognome.gogochess.logic.Square.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.inject.*;
import javax.swing.*;

import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

public class BoardPanel extends JPanel {

	private final GamePresentationModel presentationModel;
	private final static Color[] SQUARE_COLORS = new Color[] { new Color(209,139, 71), new Color(255, 206, 158) };
	private final BufferedImage piecesImage;
	private final static Piece[] PIECES_IN_IMAGE = new Piece[] { KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN };
	private Map<Square, PlayerPiece> squareToPlayerPiece = new HashMap<>();
	private int squareSize = 1;
	private Square dragStartSquare;
	private Point dragStartPoint;
	private Point dragDelta;

	private PlayerPiece[] promotionPieces;
	private PlayerPiece promotionHighlightedPiece;

	@Inject
	public BoardPanel(GamePresentationModel presentationModel) {
		this.presentationModel = presentationModel;
		presentationModel.addListener(this::onEvent);

		MouseListener mouseListener = new MouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		try {
			piecesImage = ImageIO.read(getClass().getResourceAsStream("/pieces.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load pieces from the resources: " + e.getMessage(), e);
		}
	}

	private void onEvent(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.STATE_CHANGED) {
			updateBoard();
		}
	}

	private void updateBoard() {
		initSquareToPlayerPiece(presentationModel.getBoard());
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintBoardAndPiecesExceptDraggedPiece(g);
		paintDraggedPiece(g);
		if (presentationModel.getState() == PROMOTING_WHITE_PAWN) {
			paintPromotionPieces(g, WHITE);
		}
		if (presentationModel.getState() == PROMOTING_BLACK_PAWN) {
			paintPromotionPieces(g, BLACK);
		}
	}

	private void paintBoardAndPiecesExceptDraggedPiece(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		squareSize = Math.min(getWidth(), getHeight()) / 8;

		for (int y=0; y<8; y++) {
			for (int x=0; x<8; x++) {
				Square square = new Square(x, y);
				Color squareColor = SQUARE_COLORS[(x + y) % 2];
				if (presentationModel.getTargets() != null && presentationModel.getTargets().contains(square)) {
					squareColor = squareColor.darker();
				}
				g.setColor(squareColor);
				g.fillRect(left(x), top(y), squareSize, squareSize);

				if (dragStartSquare != null && dragStartSquare.equals(square)) {
					continue;
				}
				paintPieceAtSquare(g, square, 0, 0);
			}
		}
	}

	private void paintDraggedPiece(Graphics g) {
		if (dragStartSquare != null) {
			paintPieceAtSquare(g, dragStartSquare, dragDelta.x, dragDelta.y);
		}
	}

	private void paintPieceAtSquare(Graphics g, Square square, int deltaX, int deltaY) {
		PlayerPiece playerPiece = squareToPlayerPiece.get(square);
		if (playerPiece != null) {
			g.drawImage(
					piecesImage,
					left(square.file()) + deltaX, top(square.rank()) + deltaY, left(square.file()+1) + deltaX, top(square.rank()-1) + deltaY,
					pieceLeft(playerPiece), pieceTop(playerPiece), pieceRight(playerPiece), pieceBottom(playerPiece),
					null);
		}
	}

	private void paintPromotionPieces(Graphics g, Player player) {
		g.setColor(Color.GRAY);
		int borderSize = squareSize / 10;
		int rank = player == WHITE ? RANK_7 : RANK_2;
		int file = FILE_C;
		promotionPieces = new PlayerPiece[] {
				new Knight(player), new Bishop(player), new Rook(player), new Queen(player)
		};
		g.fillRect(left(file) - borderSize, top(rank) - borderSize, squareSize * promotionPieces.length + 2 * borderSize, squareSize + 2 * borderSize);
		for (PlayerPiece playerPiece : promotionPieces) {
			if (playerPiece.equals(promotionHighlightedPiece)) {
				g.setColor(player == WHITE ? Color.DARK_GRAY : Color.LIGHT_GRAY);
				g.fillRect(left(file), top(rank), squareSize, squareSize);
			}
			g.drawImage(
					piecesImage,
					left(file), top(rank), left(file+1), top(rank-1),
					pieceLeft(playerPiece), pieceTop(playerPiece), pieceRight(playerPiece), pieceBottom(playerPiece),
					null);
			file++;
		}
	}

	private int pieceLeft(PlayerPiece playerPiece) {
		return fileOfPlayerPiece(playerPiece) * piecesImage.getWidth() / PIECES_IN_IMAGE.length;
	}

	private int pieceTop(PlayerPiece playerPiece) {
		return playerPiece.getPlayer() == WHITE ? 0 : piecesImage.getHeight() / 2;
	}

	private int pieceRight(PlayerPiece playerPiece) {
		return (fileOfPlayerPiece(playerPiece) + 1) * piecesImage.getWidth() / PIECES_IN_IMAGE.length;
	}

	private int pieceBottom(PlayerPiece playerPiece) {
		return playerPiece.getPlayer() == WHITE ? piecesImage.getHeight() / 2 : piecesImage.getHeight();
	}

	private int fileOfPlayerPiece(PlayerPiece playerPiece) {
		Piece piece = playerPiece.getPiece();
		for (int i = 0; i<PIECES_IN_IMAGE.length; i++) {
			if (piece == PIECES_IN_IMAGE[i]) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unknown piece encountered: " + piece);
	}

	private int left(int file) {
		return file * squareSize;
	}

	private int top(int rank) {
		return (RANK_8 - rank) * squareSize;
	}

	private Square getSquare(int x, int y) {
		return new Square(x / squareSize, 7 - y/squareSize);
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (presentationModel.getState() == WAITING_FOR_DRAG) {
				dragStartSquare = getSquare(e.getX(), e.getY());
				dragStartPoint = e.getPoint();
				dragDelta = new Point(0, 0);
				presentationModel.onStartDragPiece(dragStartSquare);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (presentationModel.getState() == DRAGGING) {
				dragDelta = new Point(e.getX() - dragStartPoint.x, e.getY() - dragStartPoint.y);
				updateBoard();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (presentationModel.getState() == DRAGGING) {
				Square targetSquare = getSquare(e.getX(), e.getY());
				presentationModel.onPlayerMove(dragStartSquare, targetSquare);
				dragStartSquare = null;
				dragStartPoint = null;
				dragDelta = null;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (presentationModel.getState() == PROMOTING_WHITE_PAWN || presentationModel.getState() == PROMOTING_BLACK_PAWN) {
				PlayerPiece selectedPiece = findSelectedPromotionPiece(e);
				if (selectedPiece != null) {
					presentationModel.onPromoteTo(selectedPiece);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (presentationModel.getState() == PROMOTING_WHITE_PAWN || presentationModel.getState() == PROMOTING_BLACK_PAWN) {
				promotionHighlightedPiece = findSelectedPromotionPiece(e);
				updateBoard();
			}
		}

		private PlayerPiece findSelectedPromotionPiece(MouseEvent e) {
			try {
				Square targetSquare = getSquare(e.getX(), e.getY());
				if (targetSquare.rank() == (presentationModel.getState() == PROMOTING_WHITE_PAWN ? RANK_7 : RANK_2) &&
						FILE_C <= targetSquare.file() && targetSquare.file() <= FILE_F) {
					return promotionPieces[targetSquare.file() - FILE_C];
				}
			} catch (IllegalArgumentException ex) {
				// ignore, because mouse moved outside board
			}
			return null;
		}
	}

}
