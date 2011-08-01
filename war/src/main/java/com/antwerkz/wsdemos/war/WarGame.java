package com.antwerkz.wsdemos.war;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;

public class WarGame extends WebSocketApplication {
    public static final int DIMENSION = 25;
    
    private Map<WebSocket, Player> players = new ConcurrentHashMap<WebSocket, Player>();

    @Override
    public void onClose(WebSocket socket) {
        super.onClose(socket);
        players.remove(socket);
    }

    @Override
    public void onConnect(WebSocket socket) {
        if (players.size() < 2 && players.put(socket, new Player(socket)) == null) {
            super.onConnect(socket);
            final Player opponent = getOpponent(socket);
            if(opponent != null) {
                socket.send("ready:opponent");
            }
        } else {
            socket.close();
        }
    }

    @Override
    public void onMessage(WebSocket socket, String frame) {
        //        System.out.println("textPayload = " + textPayload);
        final String[] payload = frame.split(":");
//        System.out.println("payload = " + Arrays.toString(payload));
        int index = 0;
        Result result;
        final String operation = payload[index++];
//        System.out.println("operation = '" + operation + "'");
        Type type = null;
        final int x;
        final int y;
        if ("place".equals(operation)) {
            type = Type.valueOf(payload[index++]);
            x = Integer.parseInt(payload[index++]);
            y = Integer.parseInt(payload[index++]);
            result = getPlayer(socket)
                .place(type, x, y);
            respond(socket, result, type, x, y);
        } else if ("strike".equals(operation))  {
            x = Integer.parseInt(payload[index++]);
            y = Integer.parseInt(payload[index++]);
//            System.out.println("x = " + x);
//            System.out.println("y = " + y);
            result = getOpponent(socket)
                .strike(x, y);
//            System.out.println("result = " + result);
            respond(socket, result, type, x, y);
        }
    }

    public Result strike(WebSocket player, int x, int y) {
        return players.get(player).strike(x, y);
    }

    private void respond(WebSocket socket, Result result, Type type, int x, int y) {
        switch (result) {
            case READY:
                sendToOpponent(socket, "ready:opponent");
                socket.send("ready:you");
            case PLACED:
                final int[] dimensions = type.dimensions();
                socket.send(String.format("placed:%s:%s:%s:%s:%s", type, x, y, dimensions[0], dimensions[1]));
                break;
            case VICTORY:
                socket.send(String.format("boom:%s:%s", x, y));
                socket.send("you win");
                sendToOpponent(socket, "you lose");
                break;
            case HIT:
                socket.send(String.format("boom:%s:%s", x, y));
                sendToOpponent(socket, String.format("ouch:%s:%s", x, y));
                break;
            case MISS:
                socket.send(String.format("whiff:%s:%s", x, y));
                sendToOpponent(socket, String.format("whew:%s:%s", x, y));
                break;
        }
    }

    private void sendToOpponent(WebSocket socket, String message) {
        final Player opponent = getOpponent(socket);
        if(opponent != null) {
            opponent.getSocket().send(message);
        }
    }

    private Player getPlayer(WebSocket socket) {
        return players.get(socket);
    }

    private Player getOpponent(WebSocket socket) {
        for (Entry<WebSocket, Player> webSocket : players.entrySet()) {
            if (!webSocket.getKey().equals(socket)) {
                return webSocket.getValue();
            }
        }
//        System.out.println("no opponent found");
        return null;
    }

    @Override
    public boolean isApplicationRequest(Request request) {
        return request.requestURI().equals("/war");
    }
}