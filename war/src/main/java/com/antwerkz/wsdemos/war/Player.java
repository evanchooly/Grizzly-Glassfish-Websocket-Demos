package com.antwerkz.wsdemos.war;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.grizzly.websockets.WebSocket;

public class Player {
    private ActionHandler[][] handlers;
    private String name;
    private boolean ready = false;
    private List<Piece> pieces = new ArrayList<Piece>();
    private WebSocket socket;

    public Player(final WebSocket socket) {
        this.socket = socket;
        handlers = new ActionHandler[WarGame.DIMENSION][];
        for (int i = 0; i < handlers.length; i++) {
            handlers[i] = new ActionHandler[WarGame.DIMENSION];
            for (int j = 0; j < handlers.length; j++) {
                handlers[i][j] = new ActionHandler();
            }
        }
    }

    public WebSocket getSocket() {
        return socket;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(final boolean ready) {
        this.ready = ready;
    }

    public boolean isDefeated() {
        boolean defeated = true;
        for (Piece piece : pieces) {
            defeated &= piece.isDestroyed();
        }
        return defeated;
    }

    public Result place(Type type, final int mapX, final int mapY) {
        final int[] dimensions = type.dimensions();
        final Piece piece = new Piece(type);
        pieces.add(piece);
        for (int xIndex = 0; xIndex < dimensions[0]; xIndex++) {
            for (int yIndex = 0; yIndex < dimensions[1]; yIndex++) {
                final int x = xIndex;
                final int y = yIndex;
                handlers[mapX + xIndex][mapY + yIndex] = new ActionHandler() {
                    @Override
                    public Result strike() {
                        piece.strike(x, y);
                        return isDefeated() ? Result.VICTORY : Result.HIT;
                    }
                };
            }
        }
        ready = pieces.size() == Type.values().length;
//        dump();
        return ready ? Result.READY : Result.PLACED;
    }

    private void dump() {
        char[] chars = new char[WarGame.DIMENSION];
        Arrays.fill(chars, '-');
        System.out.println(new String(chars));
        for (ActionHandler[] handler : handlers) {
            for (ActionHandler actionHandler : handler) {
                System.out.print(actionHandler.getClass().isAnonymousClass() ? "X" : "O");
            }
            System.out.println();
        }
        System.out.println(new String(chars));
    }

    public Result strike(int x, int y) {
        if (x < 0 || y < 0 || x >= WarGame.DIMENSION || y >= WarGame.DIMENSION) {
            return Result.OFFMAP;
        }
        return handlers[x][y].strike();
    }

}