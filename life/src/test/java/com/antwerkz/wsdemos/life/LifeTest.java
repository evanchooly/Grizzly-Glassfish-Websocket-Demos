package com.antwerkz.wsdemos.life;

import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketAdapter;
import com.sun.grizzly.websockets.WebSocketClient;
import org.testng.annotations.Test;

@Test
public class LifeTest {
    private static final int MILLIS = 10000;
    final Queue<String> messages = new ArrayBlockingQueue<String>(5000);
    final AtomicReference<Date> start = new AtomicReference<Date>();
    final AtomicReference<Date> end = new AtomicReference<Date>();

    public void boards() throws InterruptedException, IOException, InstantiationException, ExecutionException {
        websockets();
        polling();
    }

    private void websockets() throws IOException, InterruptedException {
        messages.clear();
        WebSocketClient websocket = new WebSocketClient("ws://localhost:8080/life", new WebSocketAdapter() {
            @Override
            public void onConnect(WebSocket socket) {
                super.onConnect(socket);
                start.set(new Date());
            }

            @Override
            public void onClose(WebSocket socket, DataFrame frame) {
                super.onConnect(socket);
                end.set(new Date());
            }

            @Override
            public void onMessage(WebSocket socket, String text) {
                messages.add(text);
            }
        });
        websocket.connect();
        Thread.sleep(MILLIS);
        websocket.close();
        System.out.printf("Received %s messages via websocket\n", messages.size());
    }

    private void polling() throws IOException, ExecutionException, InterruptedException {
        System.out.println("LifeTest.polling");
        messages.clear();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        start.set(new Date());
        while(System.currentTimeMillis() - start.get().getTime() < MILLIS) {
            asyncHttpClient.prepareGet("http://localhost:8080/comet")
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        messages.add(response.getResponseBody());
                        return response;
                    }
                }).get();
            System.out.println("response");
        }
        end.set(new Date());
        System.out.printf("Received %s messages via long polling\n", messages.size());

    }
}