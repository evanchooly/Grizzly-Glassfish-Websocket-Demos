package com.antwerkz.wsdemos.life;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.grizzly.arp.DefaultAsyncHandler;
import com.sun.grizzly.comet.CometAsyncFilter;
import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.http.servlet.ServletContextImpl;
import com.sun.grizzly.tcp.StaticResourcesAdapter;
import com.sun.grizzly.util.Utils;
import com.sun.grizzly.websockets.WebSocketAsyncFilter;
import com.sun.grizzly.websockets.WebSocketEngine;

public class Main {
    private static SelectorThread createSelectorThread(int port)
            throws IOException, InstantiationException {

        SelectorThread st = new SelectorThread();
        st.setSsBackLog(8192);
        st.setCoreThreads(2);
        st.setMaxThreads(2);
        st.setPort(port);
        st.setDisplayConfiguration(Utils.VERBOSE_TESTS);
        st.setAdapter(new StaticResourcesAdapter("src/main/webapp"));

        st.setAsyncHandler(new DefaultAsyncHandler());
        st.setEnableAsyncExecution(true);
        st.getAsyncHandler().addAsyncFilter(new WebSocketAsyncFilter());
        st.setTcpNoDelay(true);
        st.listen();

        return st;
    }

    public static void main(String[] args) throws IOException, InstantiationException {
        final SelectorThread st = createSelectorThread(8080);

        GrizzlyWebServer server = new GrizzlyWebServer(8081, "src/main/webapp");
        server.addGrizzlyAdapter(new MyServletAdapter(), new String[]{"*", "/comet"});
        server.addAsyncFilter(new CometAsyncFilter());

        server.start();

        st.start();
        WebSocketEngine.getEngine().register(new LifeGame(70, 40));
    }

    private static class MyServletAdapter extends ServletAdapter {
        public MyServletAdapter() {
            super("src/main/webapp", new ServletContextImpl(),new HashMap<String,String>(), new HashMap<String,String>(),
                new ArrayList<String>());
            setContextPath("/");
            addRootFolder("src/main/webapp");
            setServletInstance(new CometServlet());
            setHandleStaticResources(true);
        }
    }

}
