package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.function.*;

public class ProgressListener {

	private List<Integer> nrStepsPerLevel = new ArrayList<>();
	private List<Integer> currentStepsPerLevel = new ArrayList<>();
	private final Consumer<Integer> progressConsumer;

	public ProgressListener(Consumer<Integer> progressConsumer) {
		this.progressConsumer = progressConsumer;
	}

	public void setNrSteps(int level, int nrSteps) {
		while (nrStepsPerLevel.size() <= level) {
			nrStepsPerLevel.add(1);
			currentStepsPerLevel.add(0);
		}
		nrStepsPerLevel.set(level, nrSteps);
		currentStepsPerLevel.set(level, 0);
		progressConsumer.accept(progressPercentage());
	}

	public void nextStep(int level) {
		currentStepsPerLevel.set(level, currentStepsPerLevel.get(level) + 1);
		level++;
		while (level < currentStepsPerLevel.size()) {
			nrStepsPerLevel.set(level, 1);
			currentStepsPerLevel.set(level, 0);
			level++;
		}
		progressConsumer.accept(progressPercentage());
	}

	public int progressPercentage() {
		double low = 0;
		double high = 100;
		for (int level=0; level<nrStepsPerLevel.size(); level++) {
			double oldLow = low;
			low = low + (high - low) * (double) currentStepsPerLevel.get(level) / (double) nrStepsPerLevel.get(level);
			high = oldLow + (high - oldLow) * (double) (currentStepsPerLevel.get(level) + 1) / (double) nrStepsPerLevel.get(level);
		}
		return (int) (low + 0.5);
	}
}
