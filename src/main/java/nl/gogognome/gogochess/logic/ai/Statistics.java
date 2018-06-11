package nl.gogognome.gogochess.logic.ai;

public class Statistics {

	private int nrPositionsEvaluated;
	private int nrPositionsGenerated;
	private int nrCutOffsByKillerMove;
	private int nrCacheHits;

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

	public void onCacheHit() {
		nrCacheHits++;
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

	public int getNrCacheHits() {
		return nrCacheHits;
	}
}
