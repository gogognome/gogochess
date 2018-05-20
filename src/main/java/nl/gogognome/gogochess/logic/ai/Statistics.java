package nl.gogognome.gogochess.logic.ai;

public class Statistics {

	private int nrPositionsEvaluated;
	private int nrPositionsGenerated;

	public void reset() {
		nrPositionsEvaluated = 0;
		nrPositionsGenerated = 0;
	}

	public void onPositionsGenerated(int nrPositions) {
		nrPositionsGenerated += nrPositions;
	}

	public void onPositionEvaluated() {
		nrPositionsEvaluated++;
	}

	public int getNrPositionsEvaluated() {
		return nrPositionsEvaluated;
	}

	public int getNrPositionsGenerated() {
		return nrPositionsGenerated;
	}
}
