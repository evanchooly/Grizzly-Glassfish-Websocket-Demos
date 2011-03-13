package com.antwerkz.wsdemos.life;

import org.testng.annotations.Test;

@Test
public class LifeTest {
    public void boards() throws InterruptedException {
        LifeGame game = new LifeGame(40);
        int generations = 0;
        while (generations < 100) {
            generations = 0;
            game.randomize();
            game.dump();
            while (game.active()) {
                generations++;
                game.generation();
                System.out.println("generations = " + generations);
                for (int x = 0; x < 6; x++) {
                    System.out.println();
                }
                Thread.sleep(100);
            }
        }
    }
}