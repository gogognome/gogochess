package nl.gogognome.gogochess.game;

public class Board {

	private Move lastMove;

	private PlayerPiece[] playerPiecesPerSquare = new PlayerPiece[8*8];

	public void process(BoardMutation mutation) {
		switch (mutation.getMutation()) {
			case ADD:
				int index = mutation.getSquare().getBoardIndex();
				PlayerPiece playerPiece = playerPiecesPerSquare[index];
				if (playerPiece != null) {
					throw new IllegalArgumentException("The square " + mutation.getSquare() + " is not empty. It contains " + playerPiece);
				}
				playerPiecesPerSquare[index] = mutation.getPlayerPiece();
				break;
		}
	}

	public PlayerPiece pieceAt(Square square) {
		return playerPiecesPerSquare[square.getBoardIndex()];
	}
}
