package nl.gogognome.gogochess.logic.ai;

public class BoardEvaluatorFactory {

	public static BoardEvaluator newInstance() {
		return new CompositeBoardEvaluator(
				new CheckMateBoardEvaluator(),
				new PieceValueEvaluator(1000),
				new NumberOfPossibleMovesEvaluator(),
				new PiecePositionEvaluator(10));
	}
}
