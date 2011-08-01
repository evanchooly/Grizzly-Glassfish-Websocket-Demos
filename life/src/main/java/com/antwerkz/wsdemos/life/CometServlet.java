package com.antwerkz.wsdemos.life;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;

@WebServlet(urlPatterns = CometServlet.CONTEXT_PATH)
public class CometServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "/poll";
    public static String contextPath;

    private class LifeHandler implements CometHandler<HttpServletResponse> {
        private HttpServletResponse response;

        public void onEvent(CometEvent event) throws IOException {
            if (CometEvent.NOTIFY == event.getType()) {
                PrintWriter writer = response.getWriter();
                writer.write((String) event.attachment());
                writer.flush();
                event.getCometContext().resumeCometHandler(this);
            }
        }

        public void onInitialize(CometEvent event) throws IOException {
        }

        public void onInterrupt(CometEvent event) throws IOException {
            PrintWriter writer = response.getWriter();
            writer.write((String) event.attachment());
            writer.flush();
        }

        public void onTerminate(CometEvent event) throws IOException {
        }

        public void attach(HttpServletResponse attachment) {
            response = attachment;
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        contextPath = context.getContextPath() + CONTEXT_PATH;
        CometEngine engine = CometEngine.getEngine();
        CometContext cometContext = engine.register(contextPath);
        cometContext.setExpirationDelay(5 * 30 * 1000);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LifeHandler handler = new LifeHandler();
        handler.attach(res);
        CometEngine engine = CometEngine.getEngine();
        CometContext context = engine.getCometContext(contextPath);
        context.addCometHandler(handler);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LifeGame.GAME.parse(req.getReader().readLine());
    }
}
