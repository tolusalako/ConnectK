import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class RandomAI extends CKPlayer {

	public RandomAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "RandomAI";
	}

	@Override
	public Point getMove(BoardModel state) {
		Map<Point, Byte> spaces = new HashMap<>();
		for(int i=0; i<state.getWidth(); ++i)
			for(int j=0; j<state.getHeight(); ++j)
				spaces.put(new Point(i,j), state.getSpace(i, j));
		for (Point p : spaces.keySet())
			if (state.getSpace(p) == (byte) 0)
				return p;
		return null;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
