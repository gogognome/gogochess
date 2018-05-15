package nl.gogognome.gogochess.gui;

import java.awt.*;
import javax.swing.*;

public class BoardMovesAndSettingsPanel extends JPanel {

	public BoardMovesAndSettingsPanel(BoardPanel boardPanel, MovesPanel movesPanel, ProgressBar progressBar, SettingsPanel settingsPanel) {
		super(new GridBagLayout());
		add(boardPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(movesPanel, new GridBagConstraints(1, 0, 1, 1, 0.2, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
		add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
		add(settingsPanel, new GridBagConstraints(1, 1, 1, 1, 0.2, 0.1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
	}
}
