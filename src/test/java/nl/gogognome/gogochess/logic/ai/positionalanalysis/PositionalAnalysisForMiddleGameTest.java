package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Move;
import org.junit.jupiter.api.Test;

import static nl.gogognome.gogochess.logic.Player.WHITE;
import static nl.gogognome.gogochess.logic.Squares.*;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.BLACK_KNIGHT;
import static nl.gogognome.gogochess.logic.piece.PlayerPieces.WHITE_KING;
import static org.assertj.core.api.Assertions.assertThat;

class PositionalAnalysisForMiddleGameTest extends PositionalAnalysisBaseTest {

    PositionalAnalysisForMiddleGameTest() {
        super(new PositionalAnalysisForMiddleGame(new CentralControlHeuristic(), new KingFieldHeuristic()));
    }

    @Test
    void blackKnightMovingToCenterScoresBetterThanKnightMovingAwayFromCenter() {
        int towardsCenterValue = valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        int awayFromCenterValue = valueOfMove(new Move(WHITE, WHITE_KING.addTo(A1), BLACK_KNIGHT.addTo(C6)),
                BLACK_KNIGHT.removeFrom(C6), BLACK_KNIGHT.addTo(B8));

        assertThat(towardsCenterValue).isLessThan(awayFromCenterValue);
    }

    @Test
    void blackKnightMovingToWhiteKingScoresBetterThanBlackKnightMovingAwayFromWhiteKing() {
        int towardsOppononentsKing = valueOfMove(new Move(WHITE, WHITE_KING.addTo(C5), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        int awayFromOppononentsKing = valueOfMove(new Move(WHITE, WHITE_KING.addTo(A8), BLACK_KNIGHT.addTo(B8)),
                BLACK_KNIGHT.removeFrom(B8), BLACK_KNIGHT.addTo(C6));

        assertThat(towardsOppononentsKing).isLessThan(awayFromOppononentsKing);
    }
}