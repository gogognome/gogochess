package nl.gogognome.gogochess.logic.ai.positionalanalysis;

import nl.gogognome.gogochess.logic.Piece;

import static nl.gogognome.gogochess.logic.Piece.KING;

class CastlingHeuristics {


    int getCastlingValue(Piece movedPiece, int fromColumn, int toColumn) {
        int castlingValue = 0;
        if (movedPiece == KING && toColumn - fromColumn == 2) {
            castlingValue = 30;
        }

        if (movedPiece == KING && fromColumn - toColumn == 2) {
            castlingValue = 10;
        }
        return castlingValue;
    }

}
