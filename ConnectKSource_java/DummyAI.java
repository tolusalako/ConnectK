import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;

public class DummyAI extends CKPlayer {

	public DummyAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "DummyAI";
	}

	@Override
	public Point getMove(BoardModel state) {
		for(int i=0; i<state.getWidth(); ++i)
			for(int j=0; j<state.getHeight(); ++j)
				if(state.getSpace(i, j) == 0)
					return new Point(i,j);
		return null;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
