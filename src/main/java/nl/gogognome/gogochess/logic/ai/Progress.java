package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.function.*;

public class Progress {

	public class Job {
		private final int nrSteps;
		private int currentStep;

		Job(int nrSteps) {
			this.nrSteps = nrSteps;
		}

		public void onNextStep() {
			currentStep++;
			removeSubsequentJobProgresses(this);
		}

		public int getNrSteps() {
			return nrSteps;
		}

		public int getCurrentStep() {
			return currentStep;
		}
	}

	private final List<Job> jobs = new ArrayList<>();
	private final Consumer<Integer> progressUpdateConsumer;

	public Progress(Consumer<Integer> progressUpdateConsumer) {
		this.progressUpdateConsumer = progressUpdateConsumer;
	}

	public Job onStartJobWithNrSteps(int nrSteps) {
		Job job = new Job(nrSteps);
		jobs.add(job);
		progressUpdateConsumer.accept(progressPercentage());
		return job;
	}

	void removeSubsequentJobProgresses(Job job) {
		int newSize = jobs.indexOf(job) + 1;
		while (jobs.size() > newSize) {
			jobs.remove(jobs.size() - 1);
		}
		progressUpdateConsumer.accept(progressPercentage());
	}

	public int progressPercentage() {
		double low = 0;
		double high = 100;
		for (Job job : jobs) {
			double oldLow = low;
			low = low + (high - low) * (double) job.getCurrentStep() / (double) job.getNrSteps();
			high = oldLow + (high - oldLow) * (double) (job.getCurrentStep() + 1) / (double) job.getNrSteps();
		}
		return (int) (low + 0.5);
	}
}
