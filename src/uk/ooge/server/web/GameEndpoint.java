package uk.ooge.server.web;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ServerEndpoint(value = "/game")
public class GameEndpoint {
	
	@OnMessage
	public String onMessage(String message, Session session) {
		try {
			JsonObject message_json = new JsonParser().parse(message).getAsJsonObject();
			return formatReturn(true, null, null);
		} catch (Exception e) {
			return formatReturn(false, null, "Malformed message transfer.");
		}
	}
	
	@OnOpen
    public void onOpen(Session session) throws IOException {
		System.out.println("open");
        session.getBasicRemote().sendText("onOpen");
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
    	System.out.println("close");
    }
    
    private String formatReturn(boolean success, JsonObject extra, String error) {
    	JsonObject obj = new JsonObject();
    	if (extra != null) {
    		obj = extra;
    	}
    	obj.addProperty("success", success);
    	if (error != null) {
    		obj.addProperty("error", error);
    	}
    	return obj.toString();
    }
}
