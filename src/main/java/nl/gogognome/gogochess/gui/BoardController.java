package nl.gogognome.gogochess.gui;

import static nl.gogognome.gogochess.logic.Status.*;
import java.io.*;
import java.util.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;

public class BoardController {

	private final Board board = new Board();
	private final BoardPanel boardPanel;
	private final Player computerPlayer;

	public BoardController(Player computerPlayer) {
		this.computerPlayer = computerPlayer;
		boardPanel = new BoardPanel(board, 100);
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public void playGame() {
		MiniMaxAlphaBetaPruningArtificialIntelligence ai = new MiniMaxAlphaBetaPruningArtificialIntelligence(5, 2, 0);
		board.process(Move.INITIAL_BOARD);
		Move move;
		do {

			Player player = board.lastMove().getPlayer().other();
			if (player == computerPlayer) {
				move = ai.nextMove(board, player);
			} else {
				move = enterMove(board.validMoves(player));
			}
			board.process(move);
			System.out.println(move);
			boardPanel.updateBoard(board);
		} while (move.getStatus() != CHECK_MATE && move.getStatus() != STALE_MATE);
	}

	private Move enterMove(List<Move> moves) {
		System.out.println("Enter your move:");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Optional<Move> move;
			do {
				try {
					String line = br.readLine().toLowerCase();
					String from = line.substring(0, 2);
					String to = line.substring(3, 5);
					move = moves.stream()
							.filter(m -> m.toString().contains(from) && m.toString().contains(to))
							.findFirst();
				} catch (StringIndexOutOfBoundsException e) {
					move = Optional.empty();
				}
			} while (!move.isPresent());
			return move.get();
		} catch (IOException e) {
			throw new RuntimeException("Could not read line from System.in: " + e.getMessage(), e);
		}
	}
}
