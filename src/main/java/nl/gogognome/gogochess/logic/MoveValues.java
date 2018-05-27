package nl.gogognome.gogochess.logic;

import static java.lang.Math.*;
import static nl.gogognome.gogochess.logic.Player.*;

public class MoveValues {

	public static int compareTo(int value1, int value2, Player player) {
		if (player == WHITE) {
			return Integer.compare(value1, value2);
		} else {
			return Integer.compare(value2, value1);
		}
	}

	public static int maxValue(Player player) {
		return player == WHITE ? 1_000_000 : -1_000_000;
	}

	public static int minValue(Player player) {
		return player == WHITE ? -1_000_000 : 1_000_000;
	}

	public static int negateForBlack(int value, Move move) {
		return negateForBlack(value, move.getPlayer());
	}

	public static int negateForBlack(int value, Player player) {
		return player == WHITE ? value : -value;
	}

	public static int reduce(int value, int amountToReduce) {
		return value > 0 ? max(0, value - amountToReduce) : min(0, value + amountToReduce);
	}
}
