package nl.gogognome.gogochess.logic;

public class Square {

	private final static int MASK = 0xffffffff ^ 7;

	private final int boardIndex;

	public final static int FILE_A = 0;
	public final static int FILE_B = 1;
	public final static int FILE_C = 2;
	public final static int FILE_D = 3;
	public final static int FILE_E = 4;
	public final static int FILE_F = 5;
	public final static int FILE_G = 6;
	public final static int FILE_H = 7;

	public final static int RANK_1 = 0;
	public final static int RANK_2 = 1;
	public final static int RANK_3 = 2;
	public final static int RANK_4 = 3;
	public final static int RANK_5 = 4;
	public final static int RANK_6 = 5;
	public final static int RANK_7 = 6;
	public final static int RANK_8 = 7;

	public Square(String square) {
		if (square == null || square.length() != 2) {
			throw new IllegalArgumentException("Square must be a string of length 2 like B6");
		}
		int file = square.charAt(0) - 'a';
		int rank = square.charAt(1) - '1';
		validateFileAndRank(file, rank);
		boardIndex = file * 8 + rank;
	}

	public Square(int file, int rank) {
		validateFileAndRank(file, rank);
		this.boardIndex = file * 8 + rank;
	}

	private void validateFileAndRank(int file, int rank) {
		if (((file | rank) & MASK) != 0) {
			throw new IllegalArgumentException("file and rank must be in the range [0..7]");
		}
	}

	public Square(int boardIndex) {
		validateBoardIndex(boardIndex);
		this.boardIndex = boardIndex;
	}

	private void validateBoardIndex(int boardIndex) {
		if (boardIndex < 0 || boardIndex >= 8*8) {
			throw new IllegalArgumentException("Board index must be in the range [0..63]");
		}
	}

	/**
	 * @return the file in the range [0..7]
	 */
	public int file() {
		return boardIndex / 8;
	}

	/**
	 * @return the rank in the range [0..7]
	 */
	public int rank() {
		return boardIndex % 8;
	}

	@Override
	public String toString() {
		return Character.toString((char)('a' + file())) + (rank() + 1);
	}

	int boardIndex() {
		return boardIndex;
	}

	public Square addRanks(int nrRanks) {
		return buildSquareIfValid(file(), rank() + nrRanks);
	}

	public Square addFilesAndRanks(int nrFiles, int nrRanks) {
		return buildSquareIfValid(file() + nrFiles, rank() + nrRanks);
	}

	private Square buildSquareIfValid(int newFile, int newRank) {
		return ((newFile | newRank) & MASK) == 0 ? new Square(newFile, newRank) : null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		Square that = (Square) obj;
		return this.boardIndex == that.boardIndex;
	}

	@Override
	public int hashCode() {
		return boardIndex;
	}
}
