package nl.gogognome.gogochess.logic;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;

public class MoveValue implements Comparable<MoveValue> {

	public static final MoveValue ZERO = new MoveValue(0, 0, "");
	// Use Integer.MIN_VALUE + 1 so that negating the value does not result in an overflow
	public static final MoveValue WHITE_MIN_VALUE = new MoveValue(Integer.MIN_VALUE + 1, 0, "");
	public static final MoveValue BLACK_MIN_VALUE = new MoveValue(0, Integer.MIN_VALUE + 1, "");

	private final int whiteScore;
	private final int blackScore;
	private final String reason;
	private final List<MoveValue> precedingMoveValues;

	public MoveValue(int score, Move move, String reason) {
		this(score, move.getPlayer(), reason);
	}

	public MoveValue(int score, Player player, String reason) {
		this(
				player == WHITE ? score : 0,
				player == BLACK ? score : 0,
				reason);
	}

	public MoveValue(int whiteScore, int blackScore, String reason) {
		this(whiteScore, blackScore, reason, emptyList());
	}

	private MoveValue(int whiteScore, int blackScore, String reason, List<MoveValue> precedingMoveValues) {
		this.whiteScore = whiteScore;
		this.blackScore = blackScore;
		this.reason = reason;
		this.precedingMoveValues = precedingMoveValues;
	}

	public static MoveValue forWhite(int whiteScore, String reason) {
		return new MoveValue(whiteScore, 0, reason);
	}

	public static MoveValue forBlack(int blackScore, String reason) {
		return new MoveValue(0, blackScore, reason);
	}

	public static MoveValue max(MoveValue value1, MoveValue value2) {
		return value1.getCombinedScore() >= value2.getCombinedScore() ? value1 : value2;
	}

	public static MoveValue min(MoveValue value1, MoveValue value2) {
		return value1.getCombinedScore() <= value2.getCombinedScore() ? value1 : value2;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public int getCombinedScore() {
		return whiteScore - blackScore;
	}

	public boolean isGreaterThan(MoveValue that) {
		return this.getCombinedScore() > that.getCombinedScore();
	}

	public boolean isGreaterThanOrEqualTo(MoveValue that) {
		return this.getCombinedScore() >= that.getCombinedScore();
	}

	public boolean isLessThan(MoveValue that) {
		return this.getCombinedScore() < that.getCombinedScore();
	}

	public boolean isLessThanOrEqualTo(MoveValue that) {
		return this.getCombinedScore() <= that.getCombinedScore();
	}

	public MoveValue add(MoveValue that, String reason) {
		if (that == ZERO) {
			return this;
		} else if (this == ZERO) {
			return that;
		}
		return new MoveValue(this.whiteScore + that.whiteScore, this.blackScore + that.blackScore, reason, asList(this, that));
	}

	public MoveValue add(int score, Move move, String reason) {
		return add(score, move.getPlayer(), reason);
	}

	public MoveValue add(int score, Player player, String reason) {
		if (score == 0) {
			return this;
		}
		return player == WHITE ? addForWhite(score, reason) : addForBlack(score, reason);
	}

	public MoveValue addForWhite(int score, String reason) {
		return new MoveValue(whiteScore + score, blackScore, reason, this == ZERO ? emptyList() : singletonList(this));
	}

	public MoveValue addForBlack(int score, String reason) {
		return new MoveValue(whiteScore, blackScore + score, reason, this == ZERO ? emptyList() : singletonList(this));
	}

	@Override
	public int compareTo(MoveValue that) {
		return Integer.compare(this.getCombinedScore(), that.getCombinedScore());
	}

	@Override
	public int hashCode() {
		return whiteScore * 1031 + blackScore;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		MoveValue that = (MoveValue) obj;
		return this.whiteScore == that.whiteScore && this.blackScore == that.blackScore;
	}

	@Override
	public String toString() {
		return toString("");
	}

	private String toString(String prefix) {
		return prefix + "white: " + whiteScore + ", black: " + blackScore + ", combined: " + getCombinedScore() + ", reason: " + reason +
				(precedingMoveValues.isEmpty()
				? ""
				: precedingMoveValues.stream().map(m -> "\n" + m.toString(prefix + "    ")).collect(joining("; ")));
	}
}
