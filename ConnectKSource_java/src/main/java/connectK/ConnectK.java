package connectK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import connectK.utils.LogUtils;

/**
 * @author Alex Van Buskirk
 *
 */
public class ConnectK {
	Logger LOG = LoggerFactory.getLogger(ConnectK.class);
	private ConnectKGUI view;
	private BoardModel currentBoard;
	private CKPlayer[] players = new CKPlayer[3];
	private final static int TIMEOUT = 6000; //6s

	public ConnectK(BoardModel model, CKPlayer player1, CKPlayer player2) {
		currentBoard = model;
		players[1] = player1;
		players[2] = player2;
	}

	public ConnectK(BoardModel model, CKPlayer player1, CKPlayer player2, ConnectKGUI view) {
		this(model, player1, player2);
		this.view = view;
	}

	public byte play() {
		byte currentPlayer = 1;
		while (currentBoard.winner() == -1) {
			long begin = System.currentTimeMillis();
			log(currentPlayer, " says:", Level.INFO);
			java.awt.Point p;
			begin = System.currentTimeMillis();
			PlayerThread pf = new PlayerThread(players[currentPlayer], (BoardModel) currentBoard.clone(), TIMEOUT);
			pf.start();
			try {
				if (players[currentPlayer] instanceof GUIPlayer)
					pf.join();
				else
					pf.join(TIMEOUT);
			} catch (Exception e) {
				e.printStackTrace();
			}
			p = pf.move;
			if (p == null || currentBoard.getSpace(p) != 0) {
				log(currentPlayer, " returned a bad move " + p + " or went over time ("
						+ (System.currentTimeMillis() - begin) + ").", Level.ERROR);
				byte winner =  currentPlayer == (byte) 1 ? (byte) 2 : (byte) 1; // Forfeit
				log(winner, " (" + players[winner].teamName + ")" + " wins!", Level.INFO);
				return winner;													
			}
			currentBoard = currentBoard.placePiece(p, currentPlayer);
			log(currentPlayer, " returns move " + p.x + ", " + p.y + ".", Level.INFO);
			if (view != null)
				view.placePiece(currentBoard.lastMove, currentPlayer);
			currentPlayer = (byte) (currentPlayer == 1 ? 2 : 1);
		}
		byte winner = currentBoard.winner();
		if (winner != 0) {
			if (view != null) {
				view.setStatus("Player " + winner + " (" + players[winner].teamName + ")" + " wins");
				view.highlightSpaces(currentBoard.winningSpaces(), view.playerColors[winner]);
			}
			log(winner, " (" + players[winner].teamName + ")" + " wins!", Level.INFO);
		} else {
			if (!currentBoard.hasMovesLeft()) {
				LOG.info("DRAW!");
				if (view != null)
					view.setStatus("Draw");
			}
		}

		// Clean up remaining threads from teams
		players[1].destroy();
		players[2].destroy();

		// Return winner
		return winner;
	}

	public int width() {
		return currentBoard.width;
	}

	public int height() {
		return currentBoard.height;
	}

	public void log(byte player, String message, Level logLevel){
		String toLog = String.format("Player %s [%s] %s", player, players[player].teamName, message);
		LogUtils.logAIs(LOG, logLevel, toLog, null, players[1].teamName, players[2].teamName);
		if (logLevel == Level.ERROR)
			System.err.println(toLog);
		else
			System.out.println(toLog);
	}
	
}


class PlayerThread extends Thread {
	java.awt.Point move = null;
	CKPlayer player;
	BoardModel model;
	int deadline;

	public PlayerThread(CKPlayer player, BoardModel model, int deadline) {
		this.player = player;
		this.model = model;
		this.deadline = deadline;
	}

	public void run() {
		try {
			move = player.getMove(model, deadline);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
