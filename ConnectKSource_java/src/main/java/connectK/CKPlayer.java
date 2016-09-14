package connectK;

public abstract class CKPlayer {
	protected String teamName = ""; //Should be overridden by the sub-class
	protected byte player; //(1|2) player 1 always goes first
	protected BoardModel startState;
	
	public CKPlayer(byte player, BoardModel state) {
		this.player = player;
		this.startState = state;
	}
	
	public String toString(){
		return teamName;
	}
	
	public abstract java.awt.Point getMove(BoardModel state);
	public abstract java.awt.Point getMove(BoardModel state, int deadline);
	
	public void destroy(){
		
	}
}