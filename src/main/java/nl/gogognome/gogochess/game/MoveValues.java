package nl.gogognome.gogochess.game;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static nl.gogognome.gogochess.game.Player.WHITE;

public class MoveValues {

	public static int compareTo(int value1, int value2, Player player) {
		if (player == WHITE) {
			return Integer.compare(value1, value2);
		} else {
			return Integer.compare(value2, value1);
		}
	}

	public static int maxValue(Player player) {
		return player == WHITE ? MAX_VALUE : MIN_VALUE;
	}

	public static int minValue(Player player) {
		return player == WHITE ? MIN_VALUE : MAX_VALUE;
	}

	public static int add(int value, int delta, Player player) {
		return value + (player == WHITE ? delta : -delta);
	}

	public static int reduce(int value, int amountToReduce) {
		return value > 0 ? Math.max(0, value - amountToReduce) : Math.min(0, value + amountToReduce);
	}
}
