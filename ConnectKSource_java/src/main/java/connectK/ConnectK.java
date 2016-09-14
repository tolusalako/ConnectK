package connectK;

/**
 * @author Alex Van Buskirk
 *
 */
public class ConnectK {
//	public static final BoardModel defaultModel = new BoardModel(9,6,4,false);
	private ConnectKGUI view;
	private BoardModel currentBoard;
	private CKPlayer[] players = new CKPlayer[3];
	
	public ConnectK(BoardModel model, CKPlayer player1, CKPlayer player2){
		currentBoard = model;
		players[1] = player1;
		players[2] = player2;
	}
	
	public ConnectK(BoardModel model, CKPlayer player1, CKPlayer player2, ConnectKGUI view){
		this(model, player1, player2);
		this.view = view;
	}
	
	public byte play(){
		byte currentPlayer = 1;
		while(currentBoard.winner() == -1){
			long begin = System.currentTimeMillis();
			System.out.println("Player " + currentPlayer + " says:");
			java.awt.Point p;
			begin = System.currentTimeMillis();
			PlayerThread pf = new PlayerThread(players[currentPlayer], (BoardModel) currentBoard.clone(), 5000);
			pf.start();
			try {
				if(players[currentPlayer] instanceof GUIPlayer)
					pf.join();
				else
					pf.join(10*1000);
			//} catch (InterruptedException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
			p = pf.move;
			if(p == null || currentBoard.getSpace(p) != 0){
				System.err.println("Player " + currentPlayer
						+ " returned a bad move " + p
						+ " or went over time (" + (System.currentTimeMillis() - begin) + ").");
				return currentPlayer==(byte)1?(byte)2:(byte)1;//Forfeit, other player wins
			}
			currentBoard = currentBoard.placePiece(p, currentPlayer);
			System.out.println("Player " + currentPlayer + " returns move " + p.x + ", " + p.y + ".");
			if(view != null)
				view.placePiece(currentBoard.lastMove, currentPlayer);
			currentPlayer = (byte) (currentPlayer == 1? 2 : 1);
		}
		if(view != null){
			if(currentBoard.winner() != 0){
				view.setStatus("Player " 
					+ currentBoard.winner()
					+ " (" + players[currentBoard.winner()].teamName + ")" + " wins");
				view.highlightSpaces(currentBoard.winningSpaces(), view.playerColors[currentBoard.winner()]);
			}
			if(!currentBoard.hasMovesLeft()){
				view.setStatus("Draw");
			}
		}

        if(!currentBoard.hasMovesLeft()){
            System.out.println("Draw");
            return (byte)0;
        }
		System.out.println("Player " + currentBoard.winner() + 
				" (" + players[currentBoard.winner()].teamName + ")" + " wins!");
		
		//Clean up remaining threads from teams
		players[1].destroy();
		players[2].destroy();
		
		//Return winner
		return currentBoard.winner();
	}

	public int width() {
		return currentBoard.width;
	}

	public int height() {
		return currentBoard.height;
	}
}

class PlayerThread extends Thread{
	java.awt.Point move = null;
	CKPlayer player;
	BoardModel model;
	int deadline;
	
	public PlayerThread(CKPlayer player, BoardModel model, int deadline) {
		this.player = player;
		this.model = model;
		this.deadline = deadline;
	}
	
	public void run(){
		try {
			move = player.getMove(model, 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
