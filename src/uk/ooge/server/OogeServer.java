package uk.ooge.server;

public class OogeServer {
	
	public static OogeGame game;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting");
		// Ooge server start
		game = new OogeGame();
		
		
		while (true) {
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
