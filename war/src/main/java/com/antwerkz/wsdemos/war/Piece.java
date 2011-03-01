package com.antwerkz.wsdemos.war;

public class Piece {
    private Type type;
    private boolean[][] hits;

    public Piece(final Type type) {
        this.type = type;
        type.createHits(this);
    }

    public void setHits(final boolean[][] values) {
        hits = values;
    }

    public boolean isDestroyed() {
        boolean destroyed = true;
        for (boolean[] hit : hits) {
            for (boolean b : hit) {
                destroyed &= b;
            }
        }

        return destroyed;
    }

    public Result strike(final int x, final int y) {
        hits[x][y] = true;
        return Result.HIT;
    }
}
