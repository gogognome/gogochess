package nl.gogognome.gogochess.logic.ai;

import static java.util.Collections.*;
import java.util.*;
import javax.inject.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class OpeningsDatabaseArtificialIntelligenceWrapper implements ArtificialIntelligence, RecursiveSearchAI {

	private final ArtificialIntelligence wrappedArtificialIntelligence;
	private final MoveNotation moveNotation = new ReverseAlgebraicNotation();

	final static String[][] OPENINGS = new String[][] {
			{ "e2-e4", "e7-e5", "Ng1-f3", "Nb8-c6", "Bf1-b5" }, // Ruy Lopez (Spanish)
			{ "e2-e4", "e7-e5", "Ng1-f3", "Nb8-c6", "Bf1-c4", "Bf8-c5", "d2-d3" }, // Guioco Pianissimo
			{ "e2-e4", "e7-e5", "Ng1-f3", "Nb8-c6", "Bf1-c4", "Bf8-c5", "b2-b4" }, // Evans Gambit
			{ "e2-e4", "e7-e5", "d2-d4", "e5xd4", "Qd1xd4" }, // Center game
			{ "e2-e4", "e7-e5", "f2-f4" }, // King's Gambit
			{ "e2-e4", "c7-c5",  "Ng1-f3", "d7-d6", "d2-d4", "c5xd4", "Nf3xd4", "Ng8-f6", "Nb1-c3", "g7-g6" }, // Sicilian Defense, Dragon variation
			{ "e2-e4", "e7-e6", "d2-d4", "d7-d5"}, // French Defense
			{ "e2-e4", "c7-c6" }, // Caro-Kann Defense
			{ "e2-e4", "d7-d6" }, // Pirc Defense
			{ "d2-d4", "d7-d5", "c2-c4" }, // Queen's Gambit
			{ "d2-d4", "d7-d5", "e2-e4", "d5xe4", "Nb1-c3" }, // Blackmar-Diemer Gambit
			{ "d2-d4", "Ng8-f6", }, // Indian Defense
			{ "c2-c4", "e7-e5"}, // English Opening

	};

	private final Random random = new Random();

	@Inject
	public OpeningsDatabaseArtificialIntelligenceWrapper(ArtificialIntelligence wrappedArtificialIntelligence) {
		this.wrappedArtificialIntelligence = wrappedArtificialIntelligence;
	}

	@Override
	public void setMaxDepth(int maxDepth) {
		if (wrappedArtificialIntelligence instanceof RecursiveSearchAI) {
			((RecursiveSearchAI) wrappedArtificialIntelligence).setMaxDepth(maxDepth);
		}
	}

	@Override
	public Move nextMove(Board board, Player player, ProgressListener progressListener) {
		List<Integer> matchingOpenings = new ArrayList<>();
		for (int opening=0; opening<OPENINGS.length; opening++) {
			if (matchesOpeningWithFollowingMove(OPENINGS[opening], board.lastMove())) {
				matchingOpenings.add(opening);
			}
		}

		if (matchingOpenings.isEmpty() || !board.gameStartedFromInitialSetup()) {
			return wrappedArtificialIntelligence.nextMove(board, player, progressListener);
		}

		int opening = random.nextInt(matchingOpenings.size());
		String followingMoveDescription = OPENINGS[matchingOpenings.get(opening)][board.lastMove().depthInTree()];
		Move nextMove = board.currentPlayer().validMoves(board).stream()
				.filter(move -> moveNotation.format(move).equals(followingMoveDescription))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Could not find next move " + followingMoveDescription + " in valid moves " + board.currentPlayer().validMoves(board)));
		progressListener.consumeBestMoves(singletonList(nextMove));
		return nextMove;
	}

	@Override
	public void cancel() {
		wrappedArtificialIntelligence.cancel();
	}

	private boolean matchesOpeningWithFollowingMove(String[] opening, Move lastMove) {
		if (lastMove.depthInTree() + 1 > opening.length) {
			return false;
		}

		for (int index = lastMove.depthInTree() - 1; index >= 0; index--) {
			if (!moveNotation.format(lastMove).equals(opening[index])) {
				return false;
			}
			lastMove = lastMove.getPrecedingMove();
		}
		return true;
	}
}
