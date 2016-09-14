package connectK;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardModel {
	public static final int DEFAULT_WIDTH = 9;
	public static final int DEFAULT_HEIGHT = 7;
	public static final int DEFAULT_K = 5;
	public static final boolean DEFAULT_GRAVITY = false;
	public int width;
	public int height;
	public int kLength;
	public boolean gravity;
	public int spacesLeft;
	private int hash;
	private byte winner = -2;
	public java.awt.Point lastMove;// stored as a convenience to AI; not part of
									// state

	public byte[][] pieces; // [column][row] 1, 2, or null for empty

	public BoardModel(int width, int height, int k, boolean gravity) {// new
																		// board
		this.width = width;
		this.height = height;
		this.kLength = k;
		this.gravity = gravity;
		spacesLeft = width * height;
		hash = 0;

		pieces = new byte[width][height];
	}

	// returns a new board with the piece placed
	public BoardModel placePiece(java.awt.Point p, byte player) {
		assert (pieces[p.x][p.y] == 0);
		java.awt.Point move = (Point) p.clone();
		BoardModel nextBoard = this.clone();
		// while gravity is on and there is space under the piece
		while (gravity && move.y > 0 && pieces[move.x][move.y - 1] == 0)
			--move.y;// drop the piece
		nextBoard.lastMove = move;
		nextBoard.pieces[move.x][move.y] = player;
		nextBoard.spacesLeft = spacesLeft - 1;
		return nextBoard;
	}

	@Deprecated
	static public BoardModel newBoard(int width, int height, int k, boolean gravity) {
		return new BoardModel(width, height, k, gravity);
	}

	static public BoardModel defaultBoard() {
		return new BoardModel(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_K, DEFAULT_GRAVITY);
	}

	public byte getSpace(Point p) {
		assert (p.x >= 0 && p.x < width);
		assert (p.y >= 0 && p.y < height);
		return pieces[p.x][p.y];
	}

	public byte getSpace(int x, int y) {
		assert (x >= 0 && x < width);
		assert (y >= 0 && y < height);
		return pieces[x][y];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getkLength() {
		return kLength;
	}

	public boolean gravityEnabled() {
		return gravity;
	}

	public java.awt.Point getLastMove() {
		return lastMove;
	}

	public boolean hasMovesLeft() {
		return spacesLeft > 0;
	}

	// returns winner (1|2) if there is one, 0 if draw, else -1
	public byte winner() {
		uncached: if (winner == -2) {
			for (int i = 0; i < width; ++i) {
				for (int j = 0; j < height; ++j) {
					// if the space previous is either not the same as current,
					// empty, or OOB
					// while the next thing is the same AND not OOB
					// increment contiguous count
					// if count greater than k, return the winner
					// returns on first winning sequence found
					// searches to the right and up

					if (pieces[i][j] == 0) {
						if (gravity)
							break;// go to next column
						else
							continue;// move up
					}

					if (i - 1 < 0 || pieces[i - 1][j] != pieces[i][j]) { // horizontal
						int count = 1;
						while (i + count < width && pieces[i][j] == pieces[i + count][j]) {
							++count;
							if (count >= kLength) {
								winner = pieces[i][j];
								break uncached;
							}
						}
					}

					if (i - 1 < 0 || j - 1 < 0 || pieces[i - 1][j - 1] != pieces[i][j]) { // diagonal,
																							// (j-1<0)
																							// needed
																							// to
																							// avoid
																							// OOB
						int count = 1;
						while (i + count < width && j + count < height
								&& pieces[i][j] == pieces[i + count][j + count]) {
							++count;
							if (count >= kLength) {
								winner = pieces[i][j];
								break uncached;
							}
						}
					}

					if (i - 1 < 0 || j + 1 >= height || pieces[i - 1][j + 1] != pieces[i][j]) { // diagonal,
																								// (j+1>=height)
																								// needed
																								// to
																								// avoid
																								// OOB
						int count = 1;
						while (i + count < width && j - count >= 0 && pieces[i][j] == pieces[i + count][j - count]) {
							++count;
							if (count >= kLength) {
								winner = pieces[i][j];
								break uncached;
							}
						}
					}

					if (j - 1 < 0 || pieces[i][j - 1] != pieces[i][j]) { // vertical
						int count = 1;
						while (j + count < height && pieces[i][j] == pieces[i][j + count]) {
							++count;
							if (count >= kLength) {
								winner = pieces[i][j];
								break uncached;
							}
						}
					}
				}
			}
			winner = (byte) (hasMovesLeft() ? -1 : 0);
		}
		return winner;
	}

	List<Point> winningSpaces() {
		List<Point> ws = new ArrayList<Point>(kLength);
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				// if the space previous is either not the same as current,
				// empty, or OOB
				// while the next thing is the same AND not OOB
				// increment contiguous count
				// if count greater than k, return the winner
				// returns on first winning sequence found
				// searches to the right and up

				if (pieces[i][j] == 0) {
					if (gravity)
						break;// go to next column
					else
						continue;// move up
				}

				if (i - 1 < 0 || pieces[i - 1][j] != pieces[i][j]) { // horizontal
					int count = 1;
					while (i + count < width && pieces[i][j] == pieces[i + count][j]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j));
							return ws;
						}
					}
				}

				if (i - 1 < 0 || j - 1 < 0 || pieces[i - 1][j - 1] != pieces[i][j]) { // diagonal
																						// up,
																						// (j-1<0)
																						// needed
																						// to
																						// avoid
																						// OOB
					int count = 1;
					while (i + count < width && j + count < height && pieces[i][j] == pieces[i + count][j + count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j + k));
							return ws;
						}
					}
				}

				if (i - 1 < 0 || j + 1 >= height || pieces[i - 1][j + 1] != pieces[i][j]) { // diagonal
																							// down,
																							// (j+1>=height)
																							// needed
																							// to
																							// avoid
																							// OOB
					int count = 1;
					while (i + count < width && j - count >= 0 && pieces[i][j] == pieces[i + count][j - count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i + k, j - k));
							return ws;
						}
					}
				}

				if (j - 1 < 0 || pieces[i][j - 1] != pieces[i][j]) { // vertical
					int count = 1;
					while (j + count < height && pieces[i][j] == pieces[i][j + count]) {
						++count;
						if (count >= kLength) {
							for (int k = 0; k < kLength; ++k)
								ws.add(new Point(i, j + k));
							return ws;
						}
					}
				}
			}
		}
		return ws;
	}

	@Override
	public String toString() {
		String ret = "";
		for (int j = height - 1; j >= 0; --j) {
			for (int i = 0; i < width; ++i) {
				ret += pieces[i][j];
			}
			ret += "\n";
		}
		return ret;
	}

	@Override
	public BoardModel clone() {
		BoardModel cloned = new BoardModel(width, height, kLength, gravity);
		cloned.lastMove = this.lastMove;
		cloned.spacesLeft = this.spacesLeft;
		for (int i = 0; i < width; ++i)
			for (int j = 0; j < height; ++j)
				cloned.pieces[i][j] = this.pieces[i][j];
		return cloned;
	}

	// two games are equal if their shape, rules, and pieces are equal; last
	// move does not matter
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BoardModel))
			return false;
		BoardModel b = (BoardModel) o;
		if (this.width != b.width || this.height != b.height || this.kLength != b.kLength || this.gravity != b.gravity)
			return false;
		for (int i = 0; i < width; ++i)
			for (int j = 0; j < height; ++j)
				if (this.pieces[i][j] != b.pieces[i][j])
					return false;
		return true;
	}

	// hashCode operates on the same variables as equals()
	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = gravity ? 1 : 0;
			hash ^= width ^ height ^ kLength;
			hash ^= Arrays.deepHashCode(pieces);
		}
		return hash;
	}
}