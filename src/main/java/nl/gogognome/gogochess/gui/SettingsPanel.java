package nl.gogognome.gogochess.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class SettingsPanel extends JPanel {

	private final GamePresentationModel presentationModel;
	private int buttonSize;
	private final BufferedImage whiteHumanPlayer;
	private final BufferedImage whiteComputerPlayer;
	private final BufferedImage blackHumanPlayer;
	private final BufferedImage blackComputerPlayer;

	public SettingsPanel(GamePresentationModel presentationModel) {
		this.presentationModel = presentationModel;
		this.buttonSize = 20;

		try {
			whiteHumanPlayer = ImageIO.read(getClass().getResourceAsStream("/white-human-player.png"));
			whiteComputerPlayer = ImageIO.read(getClass().getResourceAsStream("/white-computer-player.png"));
			blackHumanPlayer = ImageIO.read(getClass().getResourceAsStream("/black-human-player.png"));
			blackComputerPlayer = ImageIO.read(getClass().getResourceAsStream("/black-computer-player.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load pieces from the resources: " + e.getMessage(), e);
		}

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getX() < buttonSize) {
					presentationModel.onWhitePlayerAI(!presentationModel.isWhitePlayerAi());
				} else {
					presentationModel.onBlackPlayerAI(!presentationModel.isBlackPlayerAi());
				}
			}
		});

		presentationModel.addListener(this::event);
	}

	private void event(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.SETTING_CHANGED) {
			repaint();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		buttonSize = Math.min(getHeight(), getWidth() / 2);
		BufferedImage image = presentationModel.isWhitePlayerAi() ? whiteComputerPlayer : whiteHumanPlayer;
		g.drawImage(image, 0, 0, buttonSize, buttonSize, 0, 0, 80, 86, null);

		image = presentationModel.isBlackPlayerAi() ? blackComputerPlayer : blackHumanPlayer;
		g.drawImage(image, buttonSize, 0, 2*buttonSize, buttonSize, 0, 0, 80, 86, null);
	}

}
