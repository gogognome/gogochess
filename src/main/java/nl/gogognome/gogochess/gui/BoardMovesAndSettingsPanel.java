package nl.gogognome.gogochess.gui;

import java.awt.*;
import javax.swing.*;

public class BoardMovesAndSettingsPanel extends JPanel {

	private final JScrollPane movesScrollPane;

	public BoardMovesAndSettingsPanel(
			BoardPanel boardPanel,
			MovesPanel movesPanel,
			ProgressBar progressBar,
			SettingsPanel settingsPanel) {
		super(new BorderLayout());
		this.movesScrollPane = new JScrollPane(movesPanel);
		movesPanel.setOnMoveAddedListener(this::scrollToLastMove);

		add(boardPanel, BorderLayout.CENTER);
		add(movesScrollPane, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(progressBar, BorderLayout.CENTER);
		bottomPanel.add(settingsPanel, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void scrollToLastMove(int y) {
		SwingUtilities.invokeLater(() -> movesScrollPane.getVerticalScrollBar().setValue(y));
	}
}
