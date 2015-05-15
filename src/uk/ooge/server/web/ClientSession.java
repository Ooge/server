package uk.ooge.server.web;

import java.util.Random;

import javax.websocket.Session;

import com.google.gson.JsonObject;

import uk.ooge.server.utils.Utils;

public class ClientSession {
	
	private Session session;
	private String id = "";
	private double x = 0;
	private double y = 0;
	private double boundX = 0;
	private double boundY = 0;
	private double radius = 50;
	private Colour colour = Colour.random();
	
	public ClientSession(Session session) {
		this.session = session;
		this.id = Utils.generateRandom(32);
		// TODO generate initial position of client
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public Colour getColour() {
		return colour;
	}
	
	public Session getSession() {
		return session;
	}
	
	public String getId() {
		return id;
	}
	
	
	public static class Colour {
		public int r = 0;
		public int g = 0;
		public int b = 0;
		
		public JsonObject toJsonObject() {
			JsonObject object = new JsonObject();
			object.addProperty("r", this.r);
			object.addProperty("g", this.g);
			object.addProperty("b", this.b);
			return object;
		}
		
		public static Colour random() {
			Colour c = new Colour();
			Random rand = new Random();
			c.r = rand.nextInt(255);
			c.g = rand.nextInt(255);
			c.b = rand.nextInt(255);
			return c;
		}
	}
}
