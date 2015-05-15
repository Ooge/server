package uk.ooge.server;

import uk.ooge.server.web.WebSocketServer;

public class OogeGame {
	
	private WebSocketServer server = null;
	
	public OogeGame() {
		// Start webserver
		server = new WebSocketServer();
		server.start();
		System.out.println("wss started");
	}
	
	public WebSocketServer getWebSocketServer() {
		return server;
	}
	
	public void exit() {
		server.stop();
	}
	
}
