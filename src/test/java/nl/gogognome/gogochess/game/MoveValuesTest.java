package nl.gogognome.gogochess.game;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static nl.gogognome.gogochess.game.Player.BLACK;
import static nl.gogognome.gogochess.game.Player.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
		assertEquals(MAX_VALUE, MoveValues.maxValue(WHITE));
		assertEquals(MIN_VALUE, MoveValues.maxValue(BLACK));
	}

	@Test
	void minValue() {
		assertEquals(MIN_VALUE, MoveValues.minValue(WHITE));
		assertEquals(MAX_VALUE, MoveValues.minValue(BLACK));
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

	@Test
	void add() {
		assertEquals(100, MoveValues.add(90, 10, WHITE));
		assertEquals(80, MoveValues.add(90, 10, BLACK));
		assertEquals(-100, MoveValues.add(-90, -10, WHITE));
		assertEquals(-80, MoveValues.add(-90, -10, BLACK));
		assertEquals(MAX_VALUE, MoveValues.add(MAX_VALUE - 1, 10, WHITE));
		assertEquals(MIN_VALUE, MoveValues.add(MIN_VALUE + 1, -10, WHITE));
	}

}