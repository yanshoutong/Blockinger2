package org.blockinger2.game.pieces;

import org.blockinger2.game.Square;

import android.content.Context;

public class SPiece extends Piece3x3 {

    private Square sSquare;

    public SPiece(Context c) {
        super(c);
        sSquare = new Square(Piece.type_S,c);
        pattern[1][1] = sSquare;
        pattern[1][2] = sSquare;
        pattern[2][0] = sSquare;
        pattern[2][1] = sSquare;
        reDraw();
    }

    @Override
    public void reset(Context c) {
        super.reset(c);
        pattern[1][1] = sSquare;
        pattern[1][2] = sSquare;
        pattern[2][0] = sSquare;
        pattern[2][1] = sSquare;
        reDraw();
    }

}
