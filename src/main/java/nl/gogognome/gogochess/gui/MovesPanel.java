package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class MovesPanel extends JPanel {

	private final MoveNotation moveNotation;
	private final GamePresentationModel presentationModel;
	private List<String> moves = new LinkedList<>();

	public MovesPanel(MoveNotation moveNotation, GamePresentationModel presentationModel) {
		this.moveNotation = moveNotation;
		this.presentationModel = presentationModel;
		presentationModel.addListener(this::event);
	}

	private void event(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.STATE_CHANGED) {
			updateMoves();
			repaint();
		}
	}

	private void updateMoves() {
		moves.clear();
		Move move = presentationModel.getBoard().lastMove();
		while (move != null && move.getPrecedingMove() != null) {
			moves.add(0, moveNotation.format(move));
			move = move.getPrecedingMove();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int margin = getWidth() * 10 / 100;
		int left = 0;
		g.setColor(Color.BLACK);
		g.fillRect(left, 0, getWidth() - margin, getHeight());

		g.setColor(Color.LIGHT_GRAY);
		Player player = WHITE;
		int y = margin + g.getFontMetrics().getHeight();
		for (String move : moves) {
			int textX =  player == WHITE ? left + margin : left + (getWidth()-left) / 2;
			g.drawString(move, textX, y);
			player = player.opponent();
			if (player == WHITE) {
				y += g.getFontMetrics().getHeight() * 150 / 100;
			}
		}
	}
}
