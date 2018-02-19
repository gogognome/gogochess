package nl.gogognome.gogochess.gui;

import static java.awt.BorderLayout.*;
import static javax.swing.WindowConstants.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.awt.*;
import javax.swing.*;

public class Start {

	public static void main(String args[]) {
		JFrame frame = new JFrame("Gogo Chess");

		BoardController controller = new BoardController(BLACK);

		frame.setLayout(new BorderLayout());
		frame.add(controller.getBoardPanel(), CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		controller.playGame();
	}
}
