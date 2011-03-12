package com.antwerkz.wsdemos.war;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sun.grizzly.arp.DefaultAsyncHandler;
import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.tcp.StaticResourcesAdapter;
import com.sun.grizzly.util.Utils;
import com.sun.grizzly.websockets.ClientWebSocket;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketAdapter;
import com.sun.grizzly.websockets.WebSocketAsyncFilter;
import com.sun.grizzly.websockets.WebSocketEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class WarGameTest {
    public void duel() throws IOException, InstantiationException, InterruptedException {
        final SelectorThread st = createSelectorThread(8080);
        final WarGame game = new WarGame();
        WebSocketEngine.getEngine().register(game);
        try {
            final WarAdapter adapter1 = new WarAdapter();
            final WarAdapter adapter2 = new WarAdapter();

            ClientWebSocket client1 = new ClientWebSocket("ws://localhost:8080/war", adapter1);
            client1.send(String.format("place:%s:%s:%s", Type.AA, 20, 20));
            confirm(adapter1, "placed");

            ClientWebSocket client2 = new ClientWebSocket("ws://localhost:8080/war", adapter2);
//            System.out.println("adapter2.getQueue() = " + adapter2.getQueue());
            String connected = adapter2.getQueue().poll(10, TimeUnit.SECONDS);
//            System.out.println("poll after connect: " + connected);
            client2.send(String.format("place:%s:%s:%s", Type.AA, 0, 0));
            confirm(adapter2, "placed");

            client1.send("strike:0:0");
            confirm(adapter1, "boom");
            
            client1.send("strike:10:10");
            confirm(adapter1, "whiff");

            client1.send("strike:0:1");
            confirm(adapter1, "you win");
            
//            System.out.println("adapter2.getQueue() = " + adapter2.getQueue());
        } finally {
            st.stopEndpoint();
        }
    }

    private void confirm(WarAdapter adapter, String start) throws InterruptedException {
        final BlockingQueue<String> queue = adapter.getQueue();
//        System.out.println("queue = " + queue);
        final String polled = queue.poll(10, TimeUnit.SECONDS);
//        System.out.printf("polled = %s, expected = %s\n", polled, start);
        Assert.assertTrue(polled != null && polled.startsWith(start));
    }
    
    private SelectorThread createSelectorThread(int port)
            throws IOException, InstantiationException {
        SelectorThread st = new SelectorThread();

        st.setSsBackLog(8192);
        st.setCoreThreads(2);
        st.setMaxThreads(2);
        st.setPort(port);
        st.setDisplayConfiguration(Utils.VERBOSE_TESTS);
        st.setAdapter(new StaticResourcesAdapter());
        st.setAsyncHandler(new DefaultAsyncHandler());
        st.setEnableAsyncExecution(true);
        st.getAsyncHandler().addAsyncFilter(new WebSocketAsyncFilter());
        st.setTcpNoDelay(true);
        st.listen();

        return st;
    }

    private static class WarAdapter extends WebSocketAdapter {
        private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(5, true);
        @Override
        public void onMessage(WebSocket socket, DataFrame frame) throws IOException {
//            System.out.println("queuing response: " + frame);
            queue.add(frame.getTextPayload());
        }

        public BlockingQueue<String> getQueue() {
            return queue;
        }
    }
}
