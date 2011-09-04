package com.antwerkz.wsdemos.life;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = AsyncServlet.CONTEXT_PATH, asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "/async";

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("text/javascript");
        res.setHeader("Cache-Control", "private");
        res.setHeader("Pragma", "no-cache");

        LifeGame.GAME.add(req.startAsync());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/javascript");
        res.setHeader("Cache-Control", "private");
        res.setHeader("Pragma", "no-cache");
        
        LifeGame.GAME.add(req.startAsync());
        LifeGame.GAME.parse(req.getReader().readLine());
    }
}
