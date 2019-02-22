package nl.gogognome.gogochess.logic;

import static nl.gogognome.gogochess.logic.MoveValue.*;
import static nl.gogognome.gogochess.logic.Player.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;

class MoveValueTest {

	@Test
	void testToString() {
		MoveValue fiveForWhite = forWhite(5, "My heuristic");
		assertThat(fiveForWhite.toString())
				.isEqualTo("white: 5, black: 0, combined: 5, reason: My heuristic");

		assertThat(fiveForWhite.add(0, WHITE, "No delta").toString())
				.isEqualTo("white: 5, black: 0, combined: 5, reason: My heuristic");

		assertThat(fiveForWhite.add(5, BLACK, "Good for black").toString())
				.isEqualTo(
						"white: 5, black: 5, combined: 0, reason: Good for black\n" +
						"    white: 5, black: 0, combined: 5, reason: My heuristic");

		MoveValue fiveForBlack = forBlack(5, "Another heuristic");
		assertThat(fiveForWhite.add(fiveForBlack, "Good for black").toString())
				.isEqualTo(
						"white: 5, black: 5, combined: 0, reason: Good for black\n" +
						"    white: 5, black: 0, combined: 5, reason: My heuristic; \n" +
						"    white: 0, black: 5, combined: -5, reason: Another heuristic");

		assertThat(fiveForWhite.add(5, BLACK, "Good for black").add(10, WHITE, "Counter from white").toString())
				.isEqualTo(
						"white: 15, black: 5, combined: 10, reason: Counter from white\n" +
						"    white: 5, black: 5, combined: 0, reason: Good for black\n" +
						"        white: 5, black: 0, combined: 5, reason: My heuristic");

		assertThat(ZERO.add(5, BLACK, "Good for black").toString()).isEqualTo(
				"white: 0, black: 5, combined: -5, reason: Good for black"
		);

		assertThat(fiveForBlack.add(ZERO, "nothing changes").toString()).isEqualTo(
				"white: 0, black: 5, combined: -5, reason: Another heuristic"
		);
	}

}