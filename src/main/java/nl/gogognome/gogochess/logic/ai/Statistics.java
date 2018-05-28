package nl.gogognome.gogochess.logic.ai;

public class Statistics {

	private int nrPositionsEvaluated;
	private int nrPositionsGenerated;
	private int nrCutOffsByKillerMove;

	public void reset() {
		nrPositionsEvaluated = 0;
		nrPositionsGenerated = 0;
		nrCutOffsByKillerMove = 0;
	}

	public void onPositionsGenerated(int nrPositions) {
		nrPositionsGenerated += nrPositions;
	}

	public void onPositionEvaluated() {
		nrPositionsEvaluated++;
	}

	public void onCutOffByKillerMove() {
		nrCutOffsByKillerMove++;
	}

	public int getNrPositionsEvaluated() {
		return nrPositionsEvaluated;
	}

	public int getNrPositionsGenerated() {
		return nrPositionsGenerated;
	}

	public int getNrCutOffsByKillerMove() {
		return nrCutOffsByKillerMove;
	}
}
