package nl.gogognome.gogochess.gui;

import static java.awt.BorderLayout.*;
import static javax.swing.WindowConstants.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Start {

	public static void main(String args[]) {
		JFrame frame = new JFrame("Gogo Chess");

		BoardController controller = new BoardController(BLACK);

		frame.setLayout(new BorderLayout());
		frame.add(controller.getBoardPanel(), CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.onClose();
			}
		});
		frame.setVisible(true);

		controller.playGame();
	}
}
