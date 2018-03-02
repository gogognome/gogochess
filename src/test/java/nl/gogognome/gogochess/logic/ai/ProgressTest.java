package nl.gogognome.gogochess.logic.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ProgressTest {

	private Progress progress = new Progress(percentage -> lastPercentage = percentage);
	private int lastPercentage;

	@Test
	void progressListenerOneLevel() {
		Progress.Job firstLevel = progress.onStartJobWithNrSteps(10);
		assertEquals(0, lastPercentage);

		firstLevel.onNextStep();
		assertEquals(10, lastPercentage);

		firstLevel.onNextStep();
		assertEquals(20, lastPercentage);
	}

	@Test
	void progressListenerTwoLevels() {
		Progress.Job firstLevel = progress.onStartJobWithNrSteps(10);
		Progress.Job secondLevel = progress.onStartJobWithNrSteps(10);
		assertEquals(0, lastPercentage);
		secondLevel.onNextStep();
		assertEquals(1, lastPercentage);
		secondLevel.onNextStep();
		assertEquals(2, lastPercentage);

		firstLevel.onNextStep();
		secondLevel = progress.onStartJobWithNrSteps(5);
		assertEquals(10, lastPercentage);
		secondLevel.onNextStep();
		assertEquals(12, lastPercentage);
		secondLevel.onNextStep();
		assertEquals(14, lastPercentage);
	}
}