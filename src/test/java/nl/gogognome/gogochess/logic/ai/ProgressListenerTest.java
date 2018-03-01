package nl.gogognome.gogochess.logic.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;

class ProgressListenerTest {

	private ProgressListener progressListener = new ProgressListener(percentage -> lastPercentage = percentage);
	private int lastPercentage;

	@Test
	void progressListenerOneLevel() {
		progressListener.setNrSteps(0, 10);
		assertEquals(0, lastPercentage);

		progressListener.nextStep(0);
		assertEquals(10, lastPercentage);

		progressListener.nextStep(0);
		assertEquals(20, lastPercentage);
	}

	@Test
	void progressListenerTwoLevels() {
		progressListener.setNrSteps(0, 10);
		progressListener.setNrSteps(1, 10);
		assertEquals(0, lastPercentage);
		progressListener.nextStep(1);
		assertEquals(1, lastPercentage);
		progressListener.nextStep(1);
		assertEquals(2, lastPercentage);

		progressListener.nextStep(0);
		progressListener.setNrSteps(1, 10);
		assertEquals(10, lastPercentage);
		progressListener.nextStep(1);
		assertEquals(11, lastPercentage);
		progressListener.nextStep(1);
		assertEquals(12, lastPercentage);
	}
}