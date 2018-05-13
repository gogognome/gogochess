package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Player.WHITE;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class MovesPanel extends JPanel {
	private final Dimension preferredSize;

	private final MoveNotation moveNotation;
	private List<String> moves;

	public MovesPanel(MoveNotation moveNotation, int width, int height) {
		this.moveNotation = moveNotation;
		this.preferredSize = new Dimension(width, height);
	}

	public void updateBoard(Board board) {
		moves = new LinkedList<>();
		Move move = board.lastMove();
		while (move != null && move.getPrecedingMove() != null) {
			moves.add(0, moveNotation.format(move));
			move = move.getPrecedingMove();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	public void paint(Graphics g) {
		int margin = preferredSize.width * 10 / 100;
		int left = margin;
		g.setColor(Color.BLACK);
		g.fillRect(left, 0, preferredSize.width - margin, preferredSize.height);

		g.setColor(Color.LIGHT_GRAY);
		Player player = WHITE;
		int y = margin + g.getFontMetrics().getHeight();
		for (String move : moves) {
			int textX =  player == WHITE ? left : left + (preferredSize.width-left) / 2;
			g.drawString(move, textX, y);
			player = player.other();
			if (player == WHITE) {
				y += g.getFontMetrics().getHeight() * 150 / 100;
			}
		}
	}
}
