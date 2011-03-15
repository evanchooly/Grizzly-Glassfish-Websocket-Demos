package com.antwerkz.wsdemos.life;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;

public class LifeGame extends WebSocketApplication implements Runnable {
    private boolean[][] board;
    private int width;
    private int height;
    private static boolean DEBUG = false;
    private boolean active;
    private int delay;
    private Timer timer;
    private ExecutorService service;

    public LifeGame(int x, int y) {
        width = x;
        height = y;
        createBoard();
        active = true;
        delay = 100;
        service = Executors.newSingleThreadExecutor();
        service.submit(this);
    }

    private void createBoard() {
        board = new boolean[height][width];
        randomize();
    }

    @Override
    public void onConnect(final WebSocket socket) {
        super.onConnect(socket);
        sendBoard(socket);
    }

    private void sendBoard(final WebSocket socket) {
        StringBuilder builder = new StringBuilder();
        try {
            socket.send(String.format("create(%s,%s,%s);", height, width, delay));
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    builder.append(String.format("set(%s,%s,%s);", y, x, board[y][x]));
                }
            }
            socket.send(builder.toString());
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(final WebSocket socket, final DataFrame frame) throws IOException {
        super.onMessage(socket, frame);
        final String message = frame.getTextPayload();
        if(message.startsWith("delay")) {
            delay = Integer.parseInt(message.split(":")[1]);
            broadcast("setValue('delay', " + delay + ");");
        } else if (message.startsWith("width")) {
            width = Integer.parseInt(message.split(":")[1]);
            createBoard();
            sendBoard(socket);
        } else if (message.startsWith("height")) {
            height = Integer.parseInt(message.split(":")[1]);
            createBoard();
            sendBoard(socket);
        } else if("randomize".equals(message)) {
            createBoard();
            sendBoard(socket);
        }
    }

    public void run() {
        while (active) {
            List<Runnable> actions = new ArrayList<Runnable>();
            StringBuilder builder = new StringBuilder();
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    final int neighbors = getNeighbors(x, y);
                    if (board[y][x] && (neighbors < 2 || neighbors > 3)) {
                        actions.add(turnOff(x, y));
                        builder.append(String.format("set(%s,%s,%s);", y, x, false));
                    } else if (!board[y][x] && neighbors == 3) {
                        actions.add(turnOn(x, y));
                        builder.append(String.format("set(%s,%s,%s);", y, x, true));
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
            broadcast(builder.toString());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
        randomize();
        service.submit(this);
    }

    private void broadcast(final String message) {
        for (WebSocket socket : getWebSockets()) {
            try {
                socket.send(message);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable turnOn(final int x, final int y) {
        return new Runnable() {
            @Override
            public void run() {
                set(x, y, true);
            }
        };
    }

    private Runnable turnOff(final int x, final int y) {
        return new Runnable() {
            @Override
            public void run() {
                set(x, y, false);
            }
        };
    }

    private int getNeighbors(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isAlive(x + i, y + j)) {
                    count++;
                }
            }
        }
        if (board[y][x]) {
            count--; // to account for "this" one
        }
        return count;
    }

    private boolean isAlive(int x, int y) {
        return x >= 0 && x < width
            && y >= 0 && y < height
            && board[y][x];
    }

    public void dump() {
        char[] chars = new char[width + 2];
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
        for(int index = 0; index < 47 - board.length; index++) {
            System.out.println();
        }
    }

    public void set(int x, int y, final boolean b) {
        board[y][x] = b;
    }

    public void randomize() {
        Random random = new Random();
        int count = random.nextInt(width * height);
        while (count > 0) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (!board[y][x]) {
                board[y][x] = true;
                count--;
            }
        }
        active = true;
    }

    public boolean active() {
        return active;
    }

    @Override
    public boolean isApplicationRequest(Request request) {
        return request.requestURI().equals("/life");
    }
}
