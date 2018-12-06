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
		assertEquals(new Square("a1"), new Square(0));
		assertEquals(new Square("a2"), new Square(1));
		assertEquals(new Square("a8"), new Square(7));
		assertEquals(new Square("b1"), new Square(8));
		assertEquals(new Square("h8"), new Square(63));
	}

	@Test
	void createWithValidString() {
		assertEquals("a1", new Square("a1").toString());
		assertEquals("c6", new Square("c6").toString());
		assertEquals("h7", new Square("h7").toString());
	}

	@Test
	void createWithInvalidString() {
		assertInvalidSquare(null);
		assertInvalidSquare("X");
		assertInvalidSquare("XXX");
		assertInvalidSquare("@1");
		assertInvalidSquare("a9");
		assertInvalidSquare("h0");
		assertInvalidSquare("h9");
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

	private void assertInvalidRowOrColumn(int file, int rank) {
		assertThrows(IllegalArgumentException.class, () -> new Square(file, rank));
	}

	@Test
	void toStringFormatsColumnAsLetter() {
		assertToString(0, 0, "a1");
		assertToString(0, 1, "a2");
		assertToString(0, 7, "a8");
		assertToString(1, 4, "b5");
		assertToString(7, 0, "h1");
		assertToString(7, 7, "h8");
	}

	private void assertToString(int file, int rank, String expectedValue) {
		assertEquals(expectedValue, new Square(file, rank).toString());
	}

	@Test
	void fileReturnsCorrectValue() {
		assertEquals(0, new Square(0, 5).file());
		assertEquals(3, new Square(3, 1).file());
		assertEquals(7, new Square(7, 5).file());
	}

	@Test
	void rankReturnsCorrectValue() {
		assertEquals(0, new Square(5, 0).rank());
		assertEquals(3, new Square(1, 3).rank());
		assertEquals(7, new Square(5, 7).rank());
	}

	@Test
	void boardIndexReturnsValueInRange0_63() {
		assertBoardIndex("a1", 0);
		assertBoardIndex("a2", 1);
		assertBoardIndex("a8", 7);
		assertBoardIndex("b1", 8);
		assertBoardIndex("c1", 16);
		assertBoardIndex("h8", 63);
	}

	private void assertBoardIndex(String square, int expectedIndex) {
		assertEquals(expectedIndex, new Square(square).boardIndex());
	}

	@Test
	void testEqualsAndHashCode() {
		Square A1_1 = new Square("a1");
		Square A1_2 = new Square("a1");
		Square A2 = new Square("a2");

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