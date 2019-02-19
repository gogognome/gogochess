package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Player.*;

public class MoveValues {

	public static int compareTo(int value1, int value2, Player player) {
		if (player == WHITE) {
			return Integer.compare(value1, value2);
		} else {
			return Integer.compare(value2, value1);
		}
	}

	public static MoveValue maxValue(Player player, int depthInTree) {
		// Reduec max value with depth in tree dependent value. This ensures that a check mate in fewer moves
		// gets a higher value than a check mate in more moves if the latter happens to have a better positional
		// score or an unnecessary capture.
		return new MoveValue(10_000_000 - 1000 * depthInTree, player);
	}

	static int minValue(Player player) {
		return player == WHITE ? -1_000_000 : 1_000_000;
	}

}
