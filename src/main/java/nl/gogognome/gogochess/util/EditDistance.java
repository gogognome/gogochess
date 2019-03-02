package nl.gogognome.gogochess.util;

import java.util.*;

/** Levenshtein distance algorithm to calculate the edit distance of two strings. */
public class EditDistance {

	public static int calculate(String x, String y) {
		if (x.isEmpty()) {
			return y.length();
		}

		if (y.isEmpty()) {
			return x.length();
		}

		int substitution = calculate(x.substring(1), y.substring(1)) + costOfSubstitution(x.charAt(0), y.charAt(0));
		int insertion = calculate(x, y.substring(1)) + 1;
		int deletion = calculate(x.substring(1), y) + 1;

		return min(substitution, insertion, deletion);
	}

	private static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	private static int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}

}
