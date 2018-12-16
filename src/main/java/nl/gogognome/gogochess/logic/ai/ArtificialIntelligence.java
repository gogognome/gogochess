package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public interface ArtificialIntelligence {

	/**
	 * Determine the next move for the current player
	 * @param board the board
	 * @param player the player to make the next move
	 * @return the next move
	 */
	Move nextMove(Board board, Player player, ProgressListener progressListener);

	/**
	 * Cancels nextMove(). If nextMove() is running, it will throw an ArtificalIntelligenceCanceledException.
	 */
	void cancel();
}