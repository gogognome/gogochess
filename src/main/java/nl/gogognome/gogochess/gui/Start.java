package nl.gogognome.gogochess.gui;

import static java.awt.BorderLayout.*;
import static javax.swing.WindowConstants.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;

public class Start {

	public static void main(String args[]) {
		Injector injector = Guice.createInjector(new Module());
		JFrame frame = new JFrame("Gogo Chess");

		GamePresentationModel controller = injector.getInstance(GamePresentationModel.class);
		BoardMovesAndSettingsPanel mainPanel = injector.getInstance(BoardMovesAndSettingsPanel.class);

		frame.setLayout(new BorderLayout());
		frame.add(mainPanel, CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(500, 500));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.onClose();
			}
		});
		frame.setVisible(true);

		SwingUtilities.invokeLater(() -> controller.playGame());
	}
}
