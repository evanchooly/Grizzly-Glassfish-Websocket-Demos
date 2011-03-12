package com.antwerkz.wsdemos.war;

import java.io.IOException;

import com.sun.grizzly.arp.DefaultAsyncHandler;
import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.tcp.StaticResourcesAdapter;
import com.sun.grizzly.util.Utils;
import com.sun.grizzly.websockets.WebSocketAsyncFilter;
import com.sun.grizzly.websockets.WebSocketEngine;

public class Main {
    private static SelectorThread createSelectorThread(int port)
            throws IOException, InstantiationException {
        WebSocketEngine.getEngine().register(new WarGame());
        SelectorThread st = new SelectorThread();

        st.setSsBackLog(8192);
        st.setCoreThreads(2);
        st.setMaxThreads(2);
        st.setPort(port);
        st.setDisplayConfiguration(Utils.VERBOSE_TESTS);
        st.setAdapter(new StaticResourcesAdapter("target/ws-war"));
        st.setAsyncHandler(new DefaultAsyncHandler());
        st.setEnableAsyncExecution(true);
        st.getAsyncHandler().addAsyncFilter(new WebSocketAsyncFilter());
        st.setTcpNoDelay(true);
        st.listen();

        return st;
    }

    public static void main(String[] args) throws IOException, InstantiationException {
        final SelectorThread st = createSelectorThread(8080);

    }
}
