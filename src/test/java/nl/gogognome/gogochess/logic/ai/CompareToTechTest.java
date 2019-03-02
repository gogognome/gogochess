package nl.gogognome.gogochess.logic.ai;

import static nl.gogognome.gogochess.logic.Player.*;
import java.util.*;
import org.junit.jupiter.api.*;
import com.google.inject.*;
import nl.gogognome.gogochess.juice.Module;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.movenotation.*;

class CompareToTechTest {

	// The chess games are described in James J.Gillogly, "The Technology Chess Program" (1972).
	private static final String COKO_III_VS_TECH_SECOND_COMPUTER_CHESS_CHAMPIONSHIP =
			"e2-e4 e7-e5 Ng1-f3 Nb8-c6 Bf1-c4 Ng8-f6 d2-d3 d7-d5 Bc4xd5 Nf6xBd5 e4xNd5 Qd8xd5 " +
			"Nb1-c3 Bf8-b4 O-O Bb4xNc3 b2xBc3 O-O Nf3-g5 Bc8-f5 Ra1-b1 f7-f6 c3-c4 Qd5-c5 " +
			"Ng5-h3 Bf5xNh3 Bc1-e3 Nc6-d4 g2xBh3 Qc5-c6 c2-c3 Nd4-f3+ Kg1-h1 Nf3-d2+ " +
			"f2-f3 Nd2xRf1 Qd1xNf1 f6-f5 Rb1-b5 f5-f4 Rb5-c5 Qc6-e6 Be3-c1 c7-c6 d3-d4 Ra8-e8 " +
			"Rc5xe5 Qe6-g6 Re5xRe8 Qg6xRe8 Qf1-f2 Qe8-e6 Qf2-f1 Rf8-f5 h3-h4 c6-c5 d4-d5 Qe6-d6 " +
			"Qf1-h3 Qd6-e5 Qh3-f1 Qe5xc3 d5-d6 Qc3-d4 Qf1-e2 Qd4xd6 Qe2-e8+ Rf5-f8 Qe8-a4 Rf8-f5 " +
			"Qa4-e8+ Rf5-f8 Qe8-a4 Qd6-e6 Qa4-b3 Qe6-e2 h2-h3 Rf8-d8 Bc1xf4 Rd8-d1+";

	private static final String TECH_DEPTH_4_VS_DAVID_LEVY_19710729_CARNEGGIE_MELLON_UNIVERSITY =
			"e2-e4 d7-d6 d2-d4 Ng8-f6 Nb1-c3 g7-g6 Ng1-f3 Bf8-g7 Bf1-d3 O-O O-O c7-c6 " +
			"Bc1-e3 Nb8-d7 Qd1-d2 b7-b5 Be3-h6 Nd7-b6 Bh6xBg7 Kg8xBg7 Nf3-g5 Nb6-c4 Bd3xNc4 b5xBc4 " +
			"f2-f4 Ra8-b8 e4-e5 Nf6-e8 e5xd6 Ne8xd6 Ra1-b1 Bc8-f5 d4-d5 c6-c5 Rf1-f3 h7-h6 " +
			"Ng5-h3 Qd8-a5 Nh3-f2 Nd6-b5 Rf3-e3 Nb5-d4 Nf2-e4 Rf8-d8 Kg1-h1 Bf5xNe4 Re3xBe4 Rd8xd5 " +
			"Re4xe7 Rd5-d6 Nc3-e4 Qa5xQd2 Ne4xQd2 Nd4xc2 Nd2xc4 Rd6-d4 Nc4-e5 Rd4xf4 Re7xa7 Rb8xb2 " +
			"Ra7xf7+ Rf4xRf7 Rb1-d1 Nc2-e3 Rd1-c1 Rf7-f2 Ne5-d3 Rb2-c2 Rc1-a1 Rf2xg2 Ra1-b1 Rg2xh2+ " +
			"Kh1-g1 Rc2-g2++";

	private static final String GENIE_VS_TECH_SECOND_ANNUAL_COMPUTER_CHESS_CHAMPIONSHIP =
			"e2-e4 e7-e5 Ng1-f3 Nb8-c6 Bf1-b5 Ng8-f6 O-O Bf8-c5 Nb1-c3 d7-d6 d2-d4 e5xd4 Nf3xd4 Bc8-d7 " +
			"Nd4-f5 O-O Bc1-g5 Nc6-e5 Nc3-a4 Bd7xBb5 Na4xBc5 Bb5xRf1"; // ant TECH eventually won.


	private Board board = new Board();
	private ReverseAlgebraicNotation moveNotation = new ReverseAlgebraicNotation();

	@Disabled("Run manually, because this test takes a lot of time and is only intended to measure how far Gogo Chess produces the same moves as TECH.")
	@Test
	void techDepth4_vs_DavidLevy_1971_07_29_Carneggie_Melon_University() {
		System.out.println("GENIE vs TECH, Second Annual Computer Chess Championship");
		System.out.println();
		countNrSameMovesAsTech(BLACK, GENIE_VS_TECH_SECOND_ANNUAL_COMPUTER_CHESS_CHAMPIONSHIP);

		System.out.println();
		System.out.println("COKO III vs TECH, Second Computer Chess Championship");
		System.out.println();
		countNrSameMovesAsTech(BLACK, COKO_III_VS_TECH_SECOND_COMPUTER_CHESS_CHAMPIONSHIP);

		System.out.println();
		System.out.println("TECH depth 4 vs David Levy, 1971 July 29, Carneggie-Mellon University");
		System.out.println();
		countNrSameMovesAsTech(WHITE, TECH_DEPTH_4_VS_DAVID_LEVY_19710729_CARNEGGIE_MELLON_UNIVERSITY);
	}

	private void countNrSameMovesAsTech(Player tech, String movesStrings) {
		ArtificialIntelligence ai = buildAI(4);

		board.initBoard();
		List<Move> moves = new BoardSetup(moveNotation).parseMoves(movesStrings.split(" "));

		int nrMovesEqual = 0;
		int nrMovesDifferent = 0;
		for (Move move : moves) {
			if (tech == move.getPlayer()) {
				Move techsMove = ai.nextMove(board, WHITE, new ProgressListener());
				if (move.boardMutationsEqual(techsMove)) {
					System.out.println(moveNotation.format(techsMove) + " as expected");
					nrMovesEqual += 1;
				} else {
					System.out.println(moveNotation.format(techsMove) + " differs from expected move " + moveNotation.format(move));
					nrMovesDifferent += 1;
				}
			} else {
				System.out.println(moveNotation.format(move));
			}

			board.process(move);
		}
		System.out.println("Nr moves equal: " + nrMovesEqual + ", Nr moves different: " + nrMovesDifferent);
		float percentageEquals = ((float) nrMovesEqual) * 100.0f / (nrMovesEqual + nrMovesDifferent);
		System.out.println("Percentage moves equal: " + percentageEquals + "%");
	}

	private ArtificialIntelligence buildAI(int maxDepth) {
		MiniMaxAlphaBetaArtificialIntelligence ai = Guice.createInjector(new Module()).getInstance(MiniMaxAlphaBetaArtificialIntelligence.class);
		ai.setMaxDepth(maxDepth);
		return ai;
	}

}
