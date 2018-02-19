package nl.gogognome.gogochess.logic;

public class Square {

	private final static int MASK = 0xffffffff ^ 7;

	private final int boardIndex;

	public Square(String square) {
		if (square == null || square.length() != 2) {
			throw new IllegalArgumentException("Square must be a string of length 2 like B6");
		}
		int column = square.charAt(0) - 'A';
		int row = square.charAt(1) - '1';
		validateColumnAndRow(column, row);
		boardIndex = column * 8 + row;
	}

	public Square(int column, int row) {
		validateColumnAndRow(column, row);
		this.boardIndex = column * 8 + row;
	}

	private void validateColumnAndRow(int column, int row) {
		if (((column | row) & MASK) != 0) {
			throw new IllegalArgumentException("column and row must be in the range [0..7]");
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
	 * @return the column in the range [0..7]
	 */
	public int column() {
		return boardIndex / 8;
	}

	/**
	 * @return the row in the range [0..7]
	 */
	public int row() {
		return boardIndex % 8;
	}

	@Override
	public String toString() {
		return Character.toString((char)('A' + column())) + (row() + 1);
	}

	int boardIndex() {
		return boardIndex;
	}

	public Square addRow(int nrRows) {
		return buildSquareIfValid(column(), row() + nrRows);
	}

	public Square addColumnAndRow(int nrColumns, int nrRows) {
		return buildSquareIfValid(column() + nrColumns, row() + nrRows);
	}

	private Square buildSquareIfValid(int newColumn, int newRow) {
		return ((newColumn | newRow) & MASK) == 0 ? new Square(newColumn, newRow) : null;
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
