package nl.gogognome.gogochess.logic.ai;

import java.util.function.*;
import nl.gogognome.gogochess.logic.*;

public interface ArtificialIntelligence {

	/**
	 * Determine the next move for the current player
	 * @param board the board
	 * @param player the player to make the next move
	 * @param progressUpdateConsumer consumer that receives update on the thinking process. The Integer value represents
	 *                       a percentage and will be in the range [0..100]
	 * @return
	 */
	Move nextMove(Board board, Player player, Consumer<Integer> progressUpdateConsumer);

	/**
	 * Cancels nextMove(). If nextMove() is running, it will throw an ArtificalIntelligenceCanceledException.
	 */
	void cancel();
}