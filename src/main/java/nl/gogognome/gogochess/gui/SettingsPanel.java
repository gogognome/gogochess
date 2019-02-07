package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.gui.AIThinkingLimit.Unit.*;
import static nl.gogognome.gogochess.gui.AIThinkingLimit.*;
import static nl.gogognome.gogochess.gui.GamePresentationModel.State.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import javax.imageio.*;
import javax.swing.*;
import org.slf4j.*;
import com.google.common.collect.*;

public class SettingsPanel extends JPanel {

	private static final Color DARK = Color.BLACK;
	private final static Color MIDDLE = new Color(95, 98, 112);
	private static final Color LIGHT = Color.WHITE;

	private final Logger logger = LoggerFactory.getLogger(SettingsPanel.class);
	private final GamePresentationModel presentationModel;
	private int buttonSize;
	private final BufferedImage whiteHumanPlayer;
	private final BufferedImage whiteComputerPlayer;
	private final BufferedImage blackHumanPlayer;
	private final BufferedImage blackComputerPlayer;
	private final BufferedImage undoMove;
	private final BufferedImage time;
	private final BufferedImage level;

	private AIThinkingLimit otherUnit;

	private final static List<Integer> THINKING_SECONDS = ImmutableList.of(5, 10, 15, 30, 45, 60, 120, 300);

	public SettingsPanel(GamePresentationModel presentationModel) {
		this.presentationModel = presentationModel;
		this.buttonSize = 40;
		setPreferredSize(new Dimension(7*buttonSize, buttonSize));
		otherUnit = presentationModel.getThinkingLimit().getUnit() == SECONDS ?
				level(3) :
				seconds(15);

		try {
			whiteHumanPlayer = ImageIO.read(getClass().getResourceAsStream("/white-human-player.png"));
			whiteComputerPlayer = ImageIO.read(getClass().getResourceAsStream("/white-computer-player.png"));
			blackHumanPlayer = ImageIO.read(getClass().getResourceAsStream("/black-human-player.png"));
			blackComputerPlayer = ImageIO.read(getClass().getResourceAsStream("/black-computer-player.png"));
			undoMove = ImageIO.read(getClass().getResourceAsStream("/undo-move.png"));
			time = ImageIO.read(getClass().getResourceAsStream("/hourglass.png"));
			level = ImageIO.read(getClass().getResourceAsStream("/level.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load pieces from the resources: " + e.getMessage(), e);
		}

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getX() < buttonSize) {
					presentationModel.onWhitePlayerAI(!presentationModel.isWhitePlayerAi());
				} else if (e.getX() < 2 * buttonSize) {
					presentationModel.onBlackPlayerAI(!presentationModel.isBlackPlayerAi());
				} else if (e.getX() < 3 * buttonSize){
					presentationModel.onTogglePause();
				} else if (e.getX() < 4 * buttonSize){
					presentationModel.onUndoMove();
				} else if (e.getX() < 5 * buttonSize) {
					AIThinkingLimit currentThinkingLimitUnit = presentationModel.getThinkingLimit();
					presentationModel.setThinkingLimit(otherUnit);
					otherUnit = currentThinkingLimitUnit;
				} else if (e.getX() < 11 * buttonSize / 2) {
					onLowerThinkingLimit();
				} else if (13 * buttonSize / 2 <=  e.getX() && e.getX() < 7 * buttonSize) {
					onRaiseThinkingLimit();
				}
			}
		});

		presentationModel.addListener(this::event);
	}

	private void onLowerThinkingLimit() {
		AIThinkingLimit thinkingLimit = presentationModel.getThinkingLimit();
		switch (thinkingLimit.getUnit()) {
			case SECONDS:
				int index = THINKING_SECONDS.indexOf(thinkingLimit.getValue());
				if (index > 0) {
					presentationModel.setThinkingLimit(seconds(THINKING_SECONDS.get(index-1)));
				}
				break;

			case LEVEL:
				int value = thinkingLimit.getValue();
				if (value > 1) {
					presentationModel.setThinkingLimit(level(value - 1));
				}
				break;

			default:
				throw new IllegalStateException("Unknown unit encountered: " + presentationModel.getThinkingLimit().getUnit());
		}
	}

	private void onRaiseThinkingLimit() {
		AIThinkingLimit thinkingLimit = presentationModel.getThinkingLimit();
		switch (thinkingLimit.getUnit()) {
			case SECONDS:
				int index = THINKING_SECONDS.indexOf(thinkingLimit.getValue());
				if (index + 1 < THINKING_SECONDS.size()) {
					presentationModel.setThinkingLimit(seconds(THINKING_SECONDS.get(index+1)));
				}
				break;

			case LEVEL:
				int value = thinkingLimit.getValue();
				presentationModel.setThinkingLimit(level(value + 1));
				break;

			default:
				throw new IllegalStateException("Unknown unit encountered: " + presentationModel.getThinkingLimit().getUnit());
		}
	}

	private void event(GamePresentationModel.Event event) {
		if (event == GamePresentationModel.Event.SETTING_CHANGED) {
			repaint();
		}
	}

	@Override
	public void paint(Graphics g) {
		logger.debug("Painting settings panel");
		g.setColor(DARK);
		g.fillRect(0, 0, getWidth(), getHeight());
		buttonSize = Math.min(getHeight(), getWidth() / 2);
		int x = 0;
		drawWhitePlayerStatus(g, x);

		x += buttonSize;
		drawBlackPlayerStatus(g, x);

		x += buttonSize;
		g.setColor(LIGHT);
		g.fillRect(x, 0, buttonSize, buttonSize);
		g.setColor(DARK);
		if (presentationModel.getState() == PAUSED) {
			g.fillRect(x + 3 * buttonSize / 10, 2 * buttonSize / 10, 2 * buttonSize / 10, 6 * buttonSize / 10);
			g.fillRect(x + 6 * buttonSize / 10, 2 * buttonSize / 10, 2 * buttonSize / 10, 6 * buttonSize / 10);
		} else {
			g.fillPolygon(
					new int[] { x + 2 * buttonSize / 10, x + 2 * buttonSize / 10, x + 8 * buttonSize / 10, x + 2 * buttonSize / 10 },
					new int[] { 2 * buttonSize / 10, 8 * buttonSize / 10,  buttonSize / 2, 2 * buttonSize / 10 },
					4);
		}

		x += buttonSize;
		g.drawImage(undoMove, x, 0, x+buttonSize, buttonSize, 0, 0, 80, 86, null);

		x += buttonSize;
		drawThinkingLimitValueAndControls(g, x);
	}

	private void drawWhitePlayerStatus(Graphics g, int x) {
		BufferedImage image = presentationModel.isWhitePlayerAi() ? whiteComputerPlayer : whiteHumanPlayer;
		g.drawImage(image, x, 0, x + buttonSize, buttonSize, 0, 0, 80, 86, null);
	}

	private void drawBlackPlayerStatus(Graphics g, int x) {
		BufferedImage image = presentationModel.isBlackPlayerAi() ? blackComputerPlayer : blackHumanPlayer;
		g.drawImage(image, x, 0, x+buttonSize, buttonSize, 0, 0, 80, 86, null);
	}

	private BufferedImage getImageForThinkingLimit() {
		BufferedImage thinkingImage;
		switch (presentationModel.getThinkingLimit().getUnit()) {
			case SECONDS:
				thinkingImage = time;
				break;
			case LEVEL:
				thinkingImage = level;
				break;
			default:
				throw new IllegalStateException("Unknown unit encountered: " + presentationModel.getThinkingLimit().getUnit());
		}
		return thinkingImage;
	}

	private void drawThinkingLimitValueAndControls(Graphics g, int x) {
		g.drawImage(getImageForThinkingLimit(), x, 0, x + buttonSize, buttonSize, 0, 0, 80, 86, null);

		g.setColor(MIDDLE);
		x += buttonSize;
		g.fillRect(x, 0, x + buttonSize, buttonSize);

		g.setColor(LIGHT);
		Font font = new Font("SansSerif", Font.PLAIN, buttonSize * 3 / 4);
		drawStringCentered(g, font, "\u2b07", x, 0, buttonSize / 2, buttonSize);

		g.setColor(DARK);
		g.fillRoundRect(x + buttonSize / 2, 0, buttonSize, buttonSize, buttonSize, buttonSize);
		g.setColor(MIDDLE);
		drawStringCentered(g, font, Integer.toString(presentationModel.getThinkingLimit().getValue()), x + buttonSize / 2, 0, buttonSize, buttonSize);

		g.setColor(LIGHT);
		drawStringCentered(g, font, "\u2b06", x + 3 * buttonSize / 2, 0, buttonSize / 2, buttonSize);
	}

	private void drawStringCentered(Graphics g, Font font, String text, int left, int top, int width, int height) {
		int textWidth = width;
		int textHeight = height;

		while (textWidth >= 8 * width / 10) {
			FontMetrics fontMetrics = g.getFontMetrics(font);
			textWidth = fontMetrics.stringWidth(text);
			textHeight = 7 * fontMetrics.getAscent() / 10; // it seems that about 70% of ascent is used by the font

			if (textWidth >= 8 * width / 10) {
				font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
			}
		}

		g.setFont(font);
		g.drawString(text, left + (width - textWidth) / 2, top + (height + textHeight) / 2);
	}

}
