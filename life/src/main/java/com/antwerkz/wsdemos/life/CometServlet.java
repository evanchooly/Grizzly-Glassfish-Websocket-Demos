package com.antwerkz.wsdemos.life;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;
import com.sun.grizzly.comet.DefaultNotificationHandler;

@WebServlet(urlPatterns = CometServlet.CONTEXT_PATH)
public class CometServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "/comet";
    public static String contextPath;

    private static class MyNotificationHandler extends DefaultNotificationHandler {
        @Override
        public void setThreadPool(ExecutorService threadPool) {
            super.setThreadPool(threadPool);
        }
    }

    private class LifeHandler implements CometHandler<HttpServletResponse> {
        private HttpServletResponse response;
        private boolean resume = true;

        public LifeHandler(String resume) {
            this.resume = resume == null ? true : Boolean.valueOf(resume);
        }

        public void onEvent(CometEvent event) throws IOException {
            if (CometEvent.NOTIFY == event.getType()) {
                PrintWriter writer = response.getWriter();
                writer.write((String) event.attachment());
                writer.flush();
                if (resume) {
                    event.getCometContext().resumeCometHandler(this);
                }
            }
        }

        public void onInitialize(CometEvent event) throws IOException {
        }

        public void onInterrupt(CometEvent event) throws IOException {
            PrintWriter writer = response.getWriter();
            if (writer != null && event != null && event.attachment() != null) {
                writer.write((String) event.attachment());
                writer.flush();
            }
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
        final MyNotificationHandler notificationHandler = new MyNotificationHandler();
        notificationHandler.setBlockingNotification(false);
        notificationHandler.setThreadPool(Executors.newFixedThreadPool(5));
        cometContext.setNotificationHandler(notificationHandler);
        cometContext.setExpirationDelay(5 * 30 * 1000);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/javascript");
        res.setHeader("Cache-Control", "private");
        res.setHeader("Pragma", "no-cache");

        CometEngine engine = CometEngine.getEngine();
        CometContext context = engine.getCometContext(contextPath);

        final HttpSession session = req.getSession();
        LifeHandler handler = new LifeHandler(req.getParameter("resume"));
        handler.attach(res);
        context.addCometHandler(handler);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LifeGame.GAME.parse(req.getReader().readLine());
    }
}
