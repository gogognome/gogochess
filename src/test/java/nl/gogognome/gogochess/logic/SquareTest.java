package nl.gogognome.gogochess.logic;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class SquareTest {

	@Test
	 void createSquareWithInvalidBoardIndex() {
		assertThrows(IllegalArgumentException.class, () -> new Square(-1));
		assertThrows(IllegalArgumentException.class, () -> new Square(64));
	}

	@Test
	void createSquareWithValidBoardIndex() {
		assertEquals(new Square("A1"), new Square(0));
		assertEquals(new Square("A2"), new Square(1));
		assertEquals(new Square("A8"), new Square(7));
		assertEquals(new Square("B1"), new Square(8));
		assertEquals(new Square("H8"), new Square(63));
	}

	@Test
	void createWithValidString() {
		assertEquals("A1", new Square("A1").toString());
		assertEquals("C6", new Square("C6").toString());
		assertEquals("H7", new Square("H7").toString());
	}

	@Test
	void createWithInvalidString() {
		assertInvalidSquare(null);
		assertInvalidSquare("X");
		assertInvalidSquare("XXX");
		assertInvalidSquare("@1");
		assertInvalidSquare("A9");
		assertInvalidSquare("H0");
		assertInvalidSquare("H9");
	}

	private void assertInvalidSquare(String square) {
		assertThrows(IllegalArgumentException.class, () -> new Square(square));
	}

	@Test
	void createWithInvalidRowOrColumn() {
		assertInvalidRowOrColumn(-1, 0);
		assertInvalidRowOrColumn(8, 0);
		assertInvalidRowOrColumn(0, -1);
		assertInvalidRowOrColumn(0, 8);
		assertInvalidRowOrColumn(-1, 8);
	}

	private void assertInvalidRowOrColumn(int col, int row) {
		assertThrows(IllegalArgumentException.class, () -> new Square(col, row));
	}

	@Test
	void toStringFormatsColumnAsLetter() {
		assertToString(0, 0, "A1");
		assertToString(0, 1, "A2");
		assertToString(0, 7, "A8");
		assertToString(1, 4, "B5");
		assertToString(7, 0, "H1");
		assertToString(7, 7, "H8");
	}

	private void assertToString(int column, int row, String expectedValue) {
		assertEquals(expectedValue, new Square(column, row).toString());
	}

	@Test
	void columnReturnsCorrectValue() {
		assertEquals(0, new Square(0, 5).column());
		assertEquals(3, new Square(3, 1).column());
		assertEquals(7, new Square(7, 5).column());
	}

	@Test
	void rowReturnsCorrectValue() {
		assertEquals(0, new Square(5, 0).row());
		assertEquals(3, new Square(1, 3).row());
		assertEquals(7, new Square(5, 7).row());
	}

	@Test
	void boardIndexReturnsValueInRange0_63() {
		assertBoardIndex("A1", 0);
		assertBoardIndex("A2", 1);
		assertBoardIndex("A8", 7);
		assertBoardIndex("B1", 8);
		assertBoardIndex("C1", 16);
		assertBoardIndex("H8", 63);
	}

	private void assertBoardIndex(String square, int expectedIndex) {
		assertEquals(expectedIndex, new Square(square).boardIndex());
	}

	@Test
	void testEqualsAndHashCode() {
		Square A1_1 = new Square("A1");
		Square A1_2 = new Square("A1");
		Square A2 = new Square("A2");

		assertEquals(A1_1, A1_1);
		assertEquals(A1_1, A1_2);
		assertEquals(A1_2, A1_1);
		assertNotEquals(null, A1_1);
		assertNotEquals(A1_1, null);
		assertNotEquals(A1_1, A2);
		assertNotEquals(A2, A1_1);
		assertNotEquals(A1_1, "foobar");

		assertEquals(A1_1.hashCode(), A1_2.hashCode());
		assertNotEquals(A1_1.hashCode(), A2.hashCode());
	}
}