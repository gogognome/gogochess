package nl.gogognome.gogochess.game.ai;

public class ComplexBoardEvaluator {

	public static BoardEvaluator newInstance() {
		return new CompositeBoardEvaluator(
				new CheckMateBoardEvaluator(),
				new PieceValueEvaluator(1000),
				new NumberOfPossibleMovesEvaluator(),
				new PiecePositionEvaluator());
	}
}
