package com.antwerkz.wsdemos.life;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LifeGameTest {
    public void block() {
        LifeGame game = new LifeGame(4, 4);
        final boolean[][] board = game.getBoard();
        create(board, "....",
                      ".++.",
                      ".++.",
                      "...."
        );
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }

    private void create(boolean[][] board, String... template) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = template[row].charAt(col) == '+';
            }
        }
    }

    public void beehive() {
        LifeGame game = new LifeGame(6, 5);
        create(game.getBoard(), "......",
                                "..++..",
                                ".+..+.",
                                "..++..",
                                "......");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }
    
    public void boat() {
        LifeGame game = new LifeGame(5, 5);
        create(game.getBoard(), ".....",
                                ".++..",
                                ".+.+.",
                                "..+..",
                                ".....");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }

    public void loaf() {
        LifeGame game = new LifeGame(6, 6);
        create(game.getBoard(), "......",
                                "..++..",
                                ".+..+.",
                                "..+.+.",
                                "...+..",
                                "......");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }

    public void blinker() {
        LifeGame game = new LifeGame(5, 5);
        boolean[][] stage = new boolean[5][5];
        create(game.getBoard(), ".....",
                                "..+..",
                                "..+..",
                                "..+..",
                                ".....");
        create(stage, ".....",
                      ".....",
                      ".+++.",
                      ".....",
                      ".....");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(toString(stage), toString(game.getBoard()));
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }
    
    public void toad() {
        LifeGame game = new LifeGame(6, 6);
        boolean[][] stage = new boolean[6][6];
        create(game.getBoard(), "......",
                                "......",
                                "..+++.",
                                ".+++..",
                                "......",
                                "......");
        create(stage, "......",
                      "...+..",
                      ".+..+.",
                      ".+..+.",
                      "..+...",
                      "......");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(toString(stage), toString(game.getBoard()));
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }
    
    public void beacon() {
        LifeGame game = new LifeGame(6, 6);
        boolean[][] stage = new boolean[6][6];
        create(game.getBoard(), "......",
                                ".++...",
                                ".++...",
                                "...++.",
                                "...++.",
                                "......");
        create(stage, "......",
                      ".++...",
                      ".+....",
                      "....+.",
                      "...++.",
                      "......");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(toString(stage), toString(game.getBoard()));
        game.generation();
        Assert.assertEquals(save, toString(game.getBoard()));
    }
    
    public void diamond() {
        LifeGame game = new LifeGame(5, 5);
        create(game.getBoard(), ".....",
                                "..+..",
                                ".+.+.",
                                "..+..",
                                ".....");
        final String save = toString(game.getBoard());
        game.generation();
        Assert.assertEquals(toString(game.getBoard()), save);
    }

    @Test(enabled = false)
    public void forever() throws InterruptedException {
        final LifeGame game = new LifeGame(130, 48);
        game.randomize();
        while(true) {
            game.generation();
            dump(game.getBoard());
            Thread.sleep(125);
        }
    }
    
    private void dump(boolean[][] board) {
        System.out.println(toString(board));
    }

    private String toString(boolean[][] board) {
        StringBuilder builder = new StringBuilder();
        for (boolean[] booleans : board) {
            for (boolean aBoolean : booleans) {
                builder.append(aBoolean ? "+" : ".");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}