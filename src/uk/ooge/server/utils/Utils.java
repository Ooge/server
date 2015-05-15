package uk.ooge.server.utils;

import java.util.Random;

public class Utils {
	
	public static String generateRandom(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String string = "";
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			string += Character.toString(chars.charAt(rand.nextInt(chars.length())));
		}
		return string;
	}
}
