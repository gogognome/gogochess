package nl.gogognome.gogochess.logic.ai;

import nl.gogognome.gogochess.logic.*;

public interface ArtificialIntelligence {

	Move nextMove(Board board, Player player, ProgressListener progressListener);

}