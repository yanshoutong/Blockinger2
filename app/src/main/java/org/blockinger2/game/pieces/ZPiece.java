package org.blockinger2.game.pieces;

import android.content.Context;

import org.blockinger2.game.engine.Square;

public class ZPiece extends Piece3x3
{
    private Square zSquare;

    public ZPiece(Context c)
    {
        super(c);
        zSquare = new Square(Piece.type_Z, c);
        pattern[1][0] = zSquare;
        pattern[1][1] = zSquare;
        pattern[2][1] = zSquare;
        pattern[2][2] = zSquare;
        reDraw();
    }

    @Override
    public void reset(Context c)
    {
        super.reset(c);
        pattern[1][0] = zSquare;
        pattern[1][1] = zSquare;
        pattern[2][1] = zSquare;
        pattern[2][2] = zSquare;
        reDraw();
    }
}
