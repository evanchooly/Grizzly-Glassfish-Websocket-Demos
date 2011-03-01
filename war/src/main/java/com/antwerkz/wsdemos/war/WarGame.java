package com.antwerkz.wsdemos.war;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;

public class WarGame extends WebSocketApplication {
    public static final int DIMENSION = 64;
    Map<WebSocket, Player> players = new ConcurrentHashMap<WebSocket, Player>();

    public Result strike(WebSocket player, int x, int y) {
        return players.get(player).strike(x, y);
    }

    @Override
    public void onConnect(final WebSocket socket) {
        if (players.put(socket, new Player(socket)) == null) {
            super.onConnect(socket);
        } else {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onMessage(final WebSocket socket, final DataFrame frame) throws IOException {
        final String[] payload = frame.getTextPayload().split(":");
        int index = 0;
        Result result;
        final String operation = payload[index++];
        Type type = null;
        final int x;
        final int y;
        if ("place".equals(operation)) {
            type = Type.valueOf(payload[index++]);
            x = Integer.parseInt(payload[index++]);
            y = Integer.parseInt(payload[index++]);
            result = getPlayer(socket)
                .place(type, x, y);
        } else /*if ("strike".equals(operation)) */ {
            x = Integer.parseInt(payload[index++]);
            y = Integer.parseInt(payload[index++]);
            result = getOpponent(socket)
                .strike(x, y);
        }
        switch (result) {
            case PLACED:
                final int[] dimensions = type.dimensions();
                socket.send(String.format("placed:%s:%s:%s:%s", x, y, dimensions[0], dimensions[1]));
                break;
            case VICTORY:
                socket.send("you win");
                getOpponent(socket).getSocket().send("you lose");
                break;
            case HIT:
                socket.send(String.format("boom:%s:%s", x, y));
                getOpponent(socket).getSocket().send(String.format("ouch:%s:%s", x, y));
                break;
            case MISS:
                socket.send(String.format("whiff:%s:%s", x, y));
                getOpponent(socket).getSocket().send(String.format("whew:%s:%s", x, y));
                break;
        }
    }

    private Player getPlayer(final WebSocket socket) {
        final Player player = players.get(socket);
        if (player == null) {
            throw new RuntimeException("Can't find the player.  What's wrong with me?");
        }
        return player;
    }

    private Player getOpponent(final WebSocket socket) {
        for (Entry<WebSocket, Player> webSocket : players.entrySet()) {
            if (!webSocket.getKey().equals(socket)) {
                return webSocket.getValue();
            }
        }
        throw new RuntimeException("Can't find the opponent.  What's wrong with me?");
    }

    @Override
    public boolean isApplicationRequest(final Request request) {
        return request.requestURI().equals("/war");
    }
}
