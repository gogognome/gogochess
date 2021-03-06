package nl.gogognome.gogochess.gui;

import static java.awt.BorderLayout.*;
import static javax.swing.WindowConstants.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.List;
import javax.swing.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class Start {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new Module());
		JFrame frame = new JFrame("Gogo Chess");

		GamePresentationModel gamePresentationModel = injector.getInstance(GamePresentationModel.class);
		BoardMovesAndSettingsPanel mainPanel = injector.getInstance(BoardMovesAndSettingsPanel.class);

		frame.setLayout(new BorderLayout());
		frame.add(mainPanel, CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(600, 500));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gamePresentationModel.onClose();
			}
		});
		frame.setVisible(true);

		setIcon(frame);

		SwingUtilities.invokeLater(() -> {
			gamePresentationModel.init();
			List<Move> initialMoves = new BoardSetup(new ReverseAlgebraicNotation()).parseMoves(args);
			for (Move move : initialMoves) {
				gamePresentationModel.onMove(move);
			}
		});
	}

	private static void setIcon(JFrame frame) {
		URL url = ClassLoader.getSystemResource("icon-32x32.png");
		Image image = Toolkit.getDefaultToolkit().createImage(url);
		frame.setIconImage(image);
	}
}
