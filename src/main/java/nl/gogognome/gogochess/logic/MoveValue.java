package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.Player.*;

public class MoveValue implements Comparable<MoveValue> {

	public static final MoveValue ZERO = new MoveValue(0, 0);
	// Use Integer.MIN_VALUE + 1 so that negating the value does not result in an overflow
	public static final MoveValue WHITE_MIN_VALUE = new MoveValue(Integer.MIN_VALUE + 1, 0);
	public static final MoveValue BLACK_MIN_VALUE = new MoveValue(0, Integer.MIN_VALUE + 1);

	private final int whiteScore;
	private final int blackScore;

	public MoveValue(int whiteScore, int blackScore) {
		this.whiteScore = whiteScore;
		this.blackScore = blackScore;
	}

	public MoveValue(int score, Move move) {
		this(score, move.getPlayer());
	}

	public MoveValue(int score, Player player) {
		this(
				player == WHITE ? score : 0,
				player == BLACK ? score : 0);
	}

	public static MoveValue forWhite(int whiteScore) {
		return new MoveValue(whiteScore, 0);
	}

	public static MoveValue forBlack(int blackScore) {
		return new MoveValue(0, blackScore);
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

	public MoveValue add(MoveValue that) {
		return new MoveValue(this.whiteScore + that.whiteScore, this.blackScore + that.blackScore);
	}

	public MoveValue add(int score, Move move) {
		return add(score, move.getPlayer());
	}

	public MoveValue add(int score, Player player) {
		return player == WHITE ? addForWhite(score) : addForBlack(score);
	}

	public MoveValue addForWhite(int score) {
		return new MoveValue(whiteScore + score, blackScore);
	}

	public MoveValue addForBlack(int score) {
		return new MoveValue(whiteScore, blackScore + score);
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
		if (obj == null || !obj.getClass().equals(MoveValue.class)) {
			return false;
		}

		MoveValue that = (MoveValue) obj;
		return this.whiteScore == that.whiteScore && this.blackScore == that.blackScore;
	}

	@Override
	public String toString() {
		return "white: " + whiteScore + ", black: " + blackScore + ", combined: " + getCombinedScore();
	}
}
