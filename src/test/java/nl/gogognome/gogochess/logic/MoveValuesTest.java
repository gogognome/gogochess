package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class MoveValuesTest {

	@Test
	void compareValues() {
		assertEquals(0, MoveValues.compareTo(100, 100, WHITE));
		assertEquals(0, MoveValues.compareTo(100, 100, BLACK));
		assertEquals(-1, MoveValues.compareTo(100, 200, WHITE));
		assertEquals(-1, MoveValues.compareTo(-100, -200, BLACK));
		assertEquals(1, MoveValues.compareTo(200, 100, WHITE));
		assertEquals(1, MoveValues.compareTo(-200, -100, BLACK));
	}

	@Test
	void maxValue() {
		assertEquals(forWhite(10_000_000, ""), MoveValues.winValue(WHITE, 0));
		assertEquals(forWhite(10_000_000 - 1_000, ""), MoveValues.winValue(WHITE, 1));
		assertEquals(forWhite(10_000_000 - 2_000, ""), MoveValues.winValue(WHITE, 2));

		assertEquals(forBlack(10_000_000, ""), MoveValues.winValue(BLACK, 0));
		assertEquals(forBlack(10_000_000 - 1_000, ""), MoveValues.winValue(BLACK, 1));
		assertEquals(forBlack(10_000_000 - 2_000, ""), MoveValues.winValue(BLACK, 2));
	}

	@Test
	void minValue() {
		assertEquals(forWhite(-20_000_000, ""), MoveValues.minValue(WHITE));
		assertEquals(forBlack(-20_000_000, ""), MoveValues.minValue(BLACK));
	}

}