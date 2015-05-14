package uk.ooge.server.web;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;

public class WebSocketServer {
	
	private Server server;
	
	public WebSocketServer() {
		server = new Server("localhost", 9001, "/", null, GameEndpoint.class);
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
