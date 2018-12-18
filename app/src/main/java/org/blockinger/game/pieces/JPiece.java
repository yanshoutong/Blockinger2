package org.blockinger.game.pieces;

import org.blockinger.game.Square;

import android.content.Context;

public class JPiece extends Piece3x3 {

    private Square jSquare;

    public JPiece(Context c) {
        super(c);
        jSquare = new Square(Piece.type_J,c);
        pattern[1][0] = jSquare;
        pattern[1][1] = jSquare;
        pattern[1][2] = jSquare;
        pattern[2][2] = jSquare;
        reDraw();
    }

    @Override
    public void reset(Context c) {
        super.reset(c);
        pattern[1][0] = jSquare;
        pattern[1][1] = jSquare;
        pattern[1][2] = jSquare;
        pattern[2][2] = jSquare;
        reDraw();
    }

}
