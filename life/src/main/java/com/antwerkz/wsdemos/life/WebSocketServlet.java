package com.antwerkz.wsdemos.life;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.sun.grizzly.websockets.WebSocketEngine;

@WebServlet(urlPatterns = "/life", loadOnStartup = 1)
public class WebSocketServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        WebSocketEngine.getEngine().register(LifeGame.GAME);
        LifeGame.GAME.start();
    }

    @Override
    public void destroy() {
        System.out.println("WebSocketServlet.destroy");
        LifeGame.GAME.stop();
    }
}