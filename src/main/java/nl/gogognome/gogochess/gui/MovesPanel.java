package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class MovesPanel extends JPanel implements Scrollable, MouseMotionListener {

	private final MoveNotation moveNotation;
	private final GamePresentationModel presentationModel;
	private Runnable onMoveAddedListener;
	private List<String> moves = new LinkedList<>();

	private int width = 150;
	private int fontHeight = 10;

	public MovesPanel(MoveNotation moveNotation, GamePresentationModel presentationModel) {
		this.moveNotation = moveNotation;
		this.presentationModel = presentationModel;
		presentationModel.addListener(this::event);

		setAutoscrolls(true);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(width, 10 * getRowHeight()));
	}

	void setOnMoveAddedListener(Runnable onMoveAddedListener) {
		this.onMoveAddedListener = onMoveAddedListener;
	}

	private void event(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.STATE_CHANGED) {
			updateMoves();
		}
	}

	private void updateMoves() {
		int oldNrMoves = moves.size();
		moves.clear();

		Move move = presentationModel.getBoard().lastMove();
		while (move != null && move.getPrecedingMove() != null) {
			moves.add(0, moveNotation.format(move));
			move = move.getPrecedingMove();
		}

		if (oldNrMoves != moves.size() && onMoveAddedListener != null) {
			SwingUtilities.invokeLater(() -> {
				int nrRows = Math.max(10, (moves.size() + 3) / 2);
				setPreferredSize(new Dimension(width, (nrRows) * getRowHeight()));
				repaint();
				revalidate();
				onMoveAddedListener.run();
			});
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int margin = width * 10 / 100;
		int left = 0;
		g.setColor(Color.BLACK);
		g.fillRect(left, 0, width - margin, getHeight());

		g.setColor(Color.LIGHT_GRAY);
		Player player = WHITE;
		fontHeight = g.getFontMetrics().getHeight();
		int y = margin + fontHeight;
		for (String move : moves) {
			int textX =  player == WHITE ? left + margin : left + (width-left) / 2;
			g.drawString(move, textX, y);
			player = player.opponent();
			if (player == WHITE) {
				y += getRowHeight();
			}
		}
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
