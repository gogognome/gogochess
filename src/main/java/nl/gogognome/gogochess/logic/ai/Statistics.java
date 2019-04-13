package nl.gogognome.gogochess.logic.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int nrPositionsEvaluated;
	private int nrPositionsGenerated;
	private int nrCutOffsByKillerMove;
	private int nrCacheHits;
	private long startTime;

	void reset() {
		nrPositionsEvaluated = 0;
		nrPositionsGenerated = 0;
		nrCutOffsByKillerMove = 0;
		startTime = System.nanoTime();
	}

	void onPositionsGenerated(int nrPositions) {
		nrPositionsGenerated += nrPositions;
	}

	void onPositionEvaluated() {
		nrPositionsEvaluated++;
	}

	void onCutOffByKillerMove() {
		nrCutOffsByKillerMove++;
	}

	void onCacheHit() {
		nrCacheHits++;
	}

	int getNrPositionsEvaluated() {
		return nrPositionsEvaluated;
	}

	int getNrPositionsGenerated() {
		return nrPositionsGenerated;
	}

	int getNrCutOffsByKillerMove() {
		return nrCutOffsByKillerMove;
	}

	int getNrCacheHits() {
		return nrCacheHits;
	}

	void logStatistics() {
		long endTime = System.nanoTime();
		double durationMillis = (endTime - startTime) / 1000000000.0;
		logger.debug("evaluating " + nrPositionsEvaluated+ " positions took " + durationMillis + " s (" + (nrPositionsEvaluated / (durationMillis)) + " positions/s");
		logger.debug("generating " + nrPositionsGenerated + " positions took " + durationMillis + " s (" + (nrPositionsGenerated / (durationMillis)) + " positions/s");
		logger.debug("nr cut offs caused by killer heuristic: " + nrCutOffsByKillerMove);
		logger.debug("nr cache hits: " + nrCacheHits);
	}
}
