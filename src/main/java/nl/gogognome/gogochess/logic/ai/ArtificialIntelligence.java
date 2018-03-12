package nl.gogognome.gogochess.logic.ai;

import java.util.*;
import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public interface ArtificialIntelligence {

	/**
	 * Determine the next move for the current player
	 * @param board the board
	 * @param player the player to make the next move
	 * @param progressUpdateConsumer consumer that receives updates on the thinking process. The Integer value represents
	 *                       a percentage and will be in the range [0..100]
	 * @param bestMovesConsumer consumer that receives updates on the best moves forward discovered during the thinking process
	 * @return the next move
	 */
	Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer, Consumer<List<Move>> bestMovesConsumer);

	/**
	 * Cancels nextMove(). If nextMove() is running, it will throw an ArtificalIntelligenceCanceledException.
	 */
	void cancel();
}