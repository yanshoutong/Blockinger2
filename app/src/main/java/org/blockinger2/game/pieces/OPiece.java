package org.blockinger2.game.pieces;

import android.content.Context;

import org.blockinger2.game.engine.Square;

public class OPiece extends Piece4x4
{
    private Square oSquare;

    public OPiece(Context c)
    {
        super(c);
        oSquare = new Square(Piece.type_O, c);
        pattern[1][1] = oSquare;
        pattern[1][2] = oSquare;
        pattern[2][1] = oSquare;
        pattern[2][2] = oSquare;
        reDraw();
    }

    @Override
    public void reset(Context c)
    {
        super.reset(c);
        pattern[1][1] = oSquare;
        pattern[1][2] = oSquare;
        pattern[2][1] = oSquare;
        pattern[2][2] = oSquare;
        reDraw();
    }
}
