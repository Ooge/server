package uk.ooge.server.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.websocket.Session;

import uk.ooge.server.web.ClientSession.Colour;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebSocketEvents {
	
	// String - id of Session (NOT ClientSession)
	private HashMap<String, ClientSession> sessions = new HashMap<String, ClientSession>();
	
	public void onMessage(String message, Session session) {
		ClientSession cs = sessions.get(session.getId());
		try {
			JsonObject message_json = new JsonParser().parse(message).getAsJsonObject();
			if (!message_json.has("type")) {
				session.getAsyncRemote().sendText(formatError("Malformed message").toString());
				return;
			}
			JsonElement typeElement = message_json.get("type");
			if (!typeElement.isJsonPrimitive() || !typeElement.getAsJsonPrimitive().isString()) {
				session.getAsyncRemote().sendText(formatError("Malformed message").toString());
				return;
			}
			String type = typeElement.getAsString().toLowerCase();
			switch (type) {
			case "position":
				// TODO position / anti cheat verification based on size of client
				if (!message_json.has("x") || !message_json.has("y")) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				JsonElement xElement = message_json.get("x"),
						yElement = message_json.get("y");
				if (!xElement.isJsonPrimitive() || !xElement.getAsJsonPrimitive().isNumber()) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				if (!yElement.isJsonPrimitive() || !yElement.getAsJsonPrimitive().isNumber()) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				double x = xElement.getAsDouble(),
					y = yElement.getAsDouble(),
					xNew = (x < 0 ? 0 : x),
					yNew = (y < 0 ? 0 : y);
				if (x < 0 || y < 0) {
					session.getAsyncRemote().sendText(formatClientSetPosition(xNew, yNew, cs.getRadius(), null).toString());
				}
				cs.setX(xNew);
				cs.setY(yNew);
				broadcastExcluding(formatClientPositionUpdate(cs), session.getId());
				break;
			case "alert":
				if (!message_json.has("alert")) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				JsonElement alertElement = message_json.get("alert");
				if (!alertElement.isJsonPrimitive() || !alertElement.getAsJsonPrimitive().isString()) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				String alert = alertElement.getAsString();
				broadcastExcluding(formatAlert(alert), session.getId());
				session.getAsyncRemote().sendText("Your banter message was sent: " + alert);
				break;
			case "radius":
				if (!message_json.has("radius")) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				JsonElement radiusElement = message_json.get("radius");
				if (!radiusElement.isJsonPrimitive() || !radiusElement.getAsJsonPrimitive().isNumber()) {
					session.getAsyncRemote().sendText(formatError("Malformed message").toString());
					return;
				}
				double radius = radiusElement.getAsDouble();
				cs.setRadius(radius);
				broadcastExcluding(formatClientPositionUpdate(cs), session.getId());
				session.getAsyncRemote().sendText(formatClientSetPosition(cs.getX(), cs.getY(), radius, null).toString());
				break;
			}
		} catch (Exception e) {
			session.getAsyncRemote().sendText(formatError("Malformed message").toString());
		}
	}
	
    public void onOpen(Session session) throws IOException {
		System.out.println("open");
		ClientSession new_client = new ClientSession(session);
		session.getAsyncRemote().sendText("Welcome to the banter.");
		session.getAsyncRemote().sendText(formatClientJoin(new_client).toString());
		broadcast(formatClientOpen(new_client));
		sessions.put(session.getId(), new_client);
    }

    public void onClose(Session session) {
    	System.out.println("close");
    	broadcast(formatClientClose(sessions.remove(session.getId())));
    }
    
    private void broadcast(JsonObject object) {
    	broadcastExcluding(object, null);
    }
    
    private void broadcastExcluding(JsonObject object, String id) {
    	System.out.println("broadcast to " + sessions.size());
    	for (Entry<String, ClientSession> entry : sessions.entrySet()) {
    		if (id != null && entry.getKey().equals(id)) {
    			continue;
    		}
    		entry.getValue().getSession().getAsyncRemote().sendText(object.toString());
    	}
    }
    
    private JsonObject formatClientJoin(ClientSession cs) {
    	JsonObject object = formatClientSetPosition(0, 0, cs.getRadius(), cs.getColour());
    	JsonArray players = new JsonArray();
    	for (Entry<String, ClientSession> entry : sessions.entrySet()) {
    		JsonObject pl = new JsonObject();
    		pl.addProperty("player", entry.getValue().getId());
    		pl.addProperty("x", entry.getValue().getX());
    		pl.addProperty("y", entry.getValue().getY());
    		pl.addProperty("radius", entry.getValue().getRadius());
    		pl.add("colour", entry.getValue().getColour().toJsonObject());
    		players.add(pl);
    	}
    	object.add("players", players);
    	object.addProperty("type", "connect");
    	return object;
    }
    
    private JsonObject formatClientSetPosition(double x, double y, double radius, Colour colour) {
    	JsonObject object = new JsonObject();
    	object.addProperty("type", "position");
    	object.addProperty("x", x);
    	object.addProperty("y", y);
    	object.addProperty("radius", radius);
    	if (colour != null) {
    		object.add("colour", colour.toJsonObject());
    	}
    	return object;
    }
    
    private JsonObject formatClientPositionUpdate(ClientSession session) {
    	JsonObject object = new JsonObject();
    	object.addProperty("type", "position_update");
    	object.addProperty("player", session.getId());
    	object.addProperty("x", session.getX());
    	object.addProperty("y", session.getY());
    	object.addProperty("radius", session.getRadius());
    	return object;
    }
    
    private JsonObject formatClientOpen(ClientSession session) {
    	JsonObject object = new JsonObject();
    	object.addProperty("type", "client_open");
    	object.addProperty("player", session.getId());
    	object.addProperty("x", session.getX());
    	object.addProperty("y", session.getY());
    	object.addProperty("radius", session.getRadius());
    	object.add("colour", session.getColour().toJsonObject());
    	return object;
    }
    
    private JsonObject formatClientClose(ClientSession session) {
    	JsonObject object = new JsonObject();
    	object.addProperty("type", "client_close");
    	object.addProperty("player", session.getId());
    	return object;
    }
    
    private JsonObject formatAlert(String alert) {
    	JsonObject object = new JsonObject();
    	object.addProperty("type", "alert");
    	object.addProperty("alert", alert);
    	return object;
    }
    
    private JsonObject formatError(String error) {
    	JsonObject obj = new JsonObject();
    	obj.addProperty("success", false);
    	if (error != null) {
    		obj.addProperty("error", error);
    	}
    	return obj;
    }
	
}
