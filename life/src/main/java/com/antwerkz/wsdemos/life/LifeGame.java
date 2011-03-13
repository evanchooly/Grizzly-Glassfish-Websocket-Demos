package com.antwerkz.wsdemos.life;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LifeGame {
    private boolean[][] board;
    private int dimension;
    private static boolean DEBUG = true;
    private boolean active;

    public LifeGame(int size) {
        dimension = size;
        board = new boolean[size][size];
        active = true;
    }

    public void generation() {
        List<Runnable> actions = new ArrayList<Runnable>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                final int neighbors = getNeighbors(x, y);
                if (neighbors < 2 && board[x][y]) {
                    actions.add(turnOff(x, y));
//                } else if(board[x][y] && (neighbors == 2 || neighbors == 3)) {
                } else if (board[x][y] && neighbors > 3) {
                    actions.add(turnOff(x, y));
                } else if (!board[x][y] && neighbors == 3) {
                    actions.add(turnOn(x, y));
                }

            }
        }
        for (Runnable action : actions) {
            action.run();
        }
        if (DEBUG) {
            dump();
        }
        active = !actions.isEmpty();
    }

    private Runnable turnOn(final int x, final int y) {
        return new Runnable() {
            @Override
            public void run() {
                board[x][y] = true;
            }
        };
    }

    private Runnable turnOff(final int x, final int y) {
        return new Runnable() {
            @Override
            public void run() {
                board[x][y] = false;
            }
        };
    }

    private int getNeighbors(final int x, final int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isAlive(x + i, y + j)) {
                    count++;
                }
            }
        }
        if (board[x][y]) {
            count--; // to account for "this" one
        }
        return count;
    }

    private boolean isAlive(final int x, final int y) {
        return x >= 0 && x < dimension
            && y >= 0 && y < dimension
            && board[x][y];
    }

    public void dump() {
        char[] chars = new char[dimension + 2];
        Arrays.fill(chars, '-');
        System.out.println(new String(chars));
        for (boolean[] row : board) {
            System.out.print("|");
            for (boolean cell : row) {
                System.out.print(cell ? "*" : " ");
            }
            System.out.println("|");
        }
        System.out.println(new String(chars));
    }

    public void set(final int x, final int y) {
        board[x][y] = true;
    }

    public void randomize() {
        Random random = new Random();
        int count = random.nextInt(dimension * dimension);
        while (count > 0) {
            int x = random.nextInt(dimension);
            int y = random.nextInt(dimension);
            if (!board[x][y]) {
                board[x][y] = true;
                count--;
            }
        }
        active = true;
    }

    public boolean active() {
        return active;
    }
}
