package uk.ooge.server.web;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/game")
public class GameEndpoint {
	
	@OnMessage
	public void onMessage(String message, Session session) {
		WebSocketServer.instance.getEventHandler().onMessage(message, session);
	}
	
	@OnOpen
    public void onOpen(Session session) throws IOException {
		WebSocketServer.instance.getEventHandler().onOpen(session);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	WebSocketServer.instance.getEventHandler().onClose(session);
    }
}
