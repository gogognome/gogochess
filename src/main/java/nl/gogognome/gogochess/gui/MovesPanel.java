package nl.gogognome.gogochess.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class MovesPanel extends JPanel implements Scrollable, MouseMotionListener {

	interface MoveChangeListener {
		void onLastMove(int y);
	}

	private final MoveNotation moveNotation;
	private final GamePresentationModel presentationModel;
	private MoveChangeListener onMoveAddedListener;
	private List<String> moves = new ArrayList<>();
	private int indexOfLastMove = -1;

	private int width = 180;
	private int fontHeight = 10;

	public MovesPanel(MoveNotation moveNotation, GamePresentationModel presentationModel) {
		this.moveNotation = moveNotation;
		this.presentationModel = presentationModel;
		presentationModel.addListener(this::onEvent);

		setAutoscrolls(true);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(width, 10 * getRowHeight()));
	}

	void setOnMoveAddedListener(MoveChangeListener onMoveAddedListener) {
		this.onMoveAddedListener = onMoveAddedListener;
	}

	private void onEvent(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.STATE_CHANGED) {
			updateMoves();
		}
	}

	private void updateMoves() {
		int oldNrMoves = moves.size();
		int oldIndexOfLastMove = indexOfLastMove;

		moves.clear();
		presentationModel.getMoves().stream()
				.skip(1) // skip board setup move
				.map(moveNotation::format)
				.forEach(moves::add);
		indexOfLastMove = presentationModel.getLastMoveIndex() - 1; // skip board of setup move

		if (oldNrMoves != moves.size() || oldIndexOfLastMove != indexOfLastMove) {
			if (onMoveAddedListener == null) {
				return;
			}

			SwingUtilities.invokeLater(() -> {
				int nrRows = Math.max(10, (moves.size() + 3) / 2);
				setPreferredSize(new Dimension(width, (nrRows) * getRowHeight()));
				repaint();
				revalidate();
				onMoveAddedListener.onLastMove((indexOfLastMove / 2) * getRowHeight());
			});
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int margin = width * 10 / 100;
		int left = 0;
		g.setColor(getBackground());
		g.fillRect(left, 0, width, getHeight());

		g.setColor(Color.DARK_GRAY);
		Font normalFont = g.getFont();
		Font boldFont = normalFont.deriveFont(Font.BOLD);
		fontHeight = g.getFontMetrics().getHeight();
		int y = margin + fontHeight;
		int nrRows = (moves.size() + 1) / 2;
		for (int row=0; row < nrRows; row++) {
			g.drawString(Integer.toString(row+1) + '.', margin / 2, y);

			paintMove(g, 2 * row, normalFont, boldFont, left + 2 * margin, y);

			if (2 * row + 1 < moves.size()) {
				paintMove(g, 2 * row + 1, normalFont, boldFont, left + (width - left) / 2 + margin, y);
			}

			y += getRowHeight();
		}
	}

	private void paintMove(Graphics g, int index, Font normalFont, Font boldFont, int textX, int y) {
		g.setFont(index == indexOfLastMove ? boldFont : normalFont);
		g.drawString(moves.get(index), textX, y);
	}

	private int getRowHeight() {
		return fontHeight * 150 / 100;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(width, 10 * getRowHeight());
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? getRowHeight() : 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? getHeight() - getRowHeight() : 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		scrollRectToVisible(r);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
