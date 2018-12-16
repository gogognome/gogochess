package nl.gogognome.gogochess.gui;

import java.awt.*;
import javax.swing.*;

public class ProgressBar extends JPanel {

	private final GamePresentationModel presentationModel;

	public ProgressBar(GamePresentationModel presentationModel) {
		this.presentationModel = presentationModel;
		presentationModel.addListener(this::onEvent);
		setPreferredSize(new Dimension(200, 20));
	}

	private void onEvent(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.PERCENTAGE_CHANGED) {
			repaint();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		int margin = 10;
		int arcSize = 2 * margin;
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize);
		g.setColor(Color.BLUE);
		g.fillRoundRect(0, 0, getWidth() * presentationModel.getPercentage() / 100, getHeight(), arcSize, arcSize);
	}
}
