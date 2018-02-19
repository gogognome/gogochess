package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Piece.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.piece.*;

public class BoardPanel extends JPanel {

	private final int squareSize;
	private final static Color[] SQUARE_COLORS = new Color[] { new Color(148, 170, 255 ), new Color(255, 255, 	173) };
	private final BufferedImage piecesImage;
	private final static Piece[] PIECES_IN_IMAGE = new Piece[] { KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN };
	private Map<Square, PlayerPiece> squareToPlayerPiece = new HashMap<>();

	public BoardPanel(Board board, int squareSize) {
		this.squareSize = squareSize;
		initSquareToPlayerPiece(board);

		try {
			piecesImage = ImageIO.read(getClass().getResourceAsStream("/pieces.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load pieces from the resources: " + e.getMessage(), e);
		}
	}

	public void updateBoard(Board board) {
		initSquareToPlayerPiece(board);
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
	public Dimension getPreferredSize() {
		return new Dimension(8 * squareSize,8 * squareSize);
	}

	@Override
	public void paint(Graphics g) {
		for (int y=0; y<8; y++) {
			for (int x=0; x<8; x++) {
				g.setColor(SQUARE_COLORS[(x+y) % 2]);
				g.fillRect(left(x), top(y), squareSize, squareSize);

				PlayerPiece playerPiece = squareToPlayerPiece.get(new Square(x, y));
				if (playerPiece != null) {
					g.drawImage(
							piecesImage,
							left(x), top(y), left(x+1), top(y-1),
							pieceLeft(playerPiece), pieceTop(playerPiece), pieceRight(playerPiece), pieceBottom(playerPiece),
							null);
				}
			}
		}
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
}
