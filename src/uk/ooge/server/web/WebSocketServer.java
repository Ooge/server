package uk.ooge.server.web;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;

public class WebSocketServer {
	
	public static WebSocketServer instance;
	
	private WebSocketEvents eventHandler;
	private Server server;
	
	public WebSocketServer() {
		server = new Server("localhost", 9001, "/", null, GameEndpoint.class);
		eventHandler = new WebSocketEvents();
		instance = this;
	}
	
	public WebSocketEvents getEventHandler() {
		return eventHandler;
	}
	
	public Server getServer() {
		return server;
	}
	
	public boolean start() {
		try {
			server.start();
		} catch (DeploymentException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void stop() {
		server.stop();
	}
	
}
