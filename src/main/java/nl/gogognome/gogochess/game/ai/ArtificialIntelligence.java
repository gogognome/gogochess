package nl.gogognome.gogochess.game.ai;

import nl.gogognome.gogochess.game.*;

public interface ArtificialIntelligence {

	Move nextMove(Board board, Player player);

}