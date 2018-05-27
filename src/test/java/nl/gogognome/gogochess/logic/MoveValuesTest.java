package nl.gogognome.gogochess.logic;

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
		assertEquals(1_000_000, MoveValues.maxValue(WHITE));
		assertEquals(-1_000_000, MoveValues.maxValue(BLACK));
	}

	@Test
	void minValue() {
		assertEquals(-1_000_000, MoveValues.minValue(WHITE));
		assertEquals(1_000_000, MoveValues.minValue(BLACK));
	}

	@Test
	void reduceDoesNotReducePastZero() {
		assertEquals(0, MoveValues.reduce(0, 10));
		assertEquals(0, MoveValues.reduce(5, 10));
		assertEquals(0, MoveValues.reduce(-5, 10));
	}

	@Test
	void reduceBringsNumberNearerToZero() {
		assertEquals(90, MoveValues.reduce(100, 10));
		assertEquals(-90, MoveValues.reduce(-100, 10));
	}

}