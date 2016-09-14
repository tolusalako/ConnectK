package connectK.tournament;

/**
 * Holds the BoardConfiguration for a game
 */
public class BoardConfig {
	final int width;
	final int height;
	final int k;
	final boolean gravity;
	/**
	 * @param width
	 * @param height
	 * @param k
	 * @param gravity
	 */
	public BoardConfig(int width, int height, int k, int gravity) {
		this.width = width;
		this.height = height;
		this.k = k;
		this.gravity = (gravity == 0) ? false : true;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}
	/**
	 * @return the gravity
	 */
	public boolean getGravity() {
		return gravity;
	}

}
