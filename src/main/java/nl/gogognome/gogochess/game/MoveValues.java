package nl.gogognome.gogochess.game;

import static java.lang.Integer.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static nl.gogognome.gogochess.game.Player.*;

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

	public static int negateForBlack(int value, Player player) {
		return player == WHITE ? value : -value;
	}

	public static int add(int value, int delta) {
		long result = (long) value + (long) delta;
		result = max(MIN_VALUE, result);
		result = min(MAX_VALUE, result);
		return (int) result;
	}

	public static int reduce(int value, int amountToReduce) {
		return value > 0 ? max(0, value - amountToReduce) : min(0, value + amountToReduce);
	}
}
