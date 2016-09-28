/**
 * 
 */
package connectK;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * @author ateam
 *
 */
public class GUIPlayer extends CKPlayer implements ActionListener{
	Point lastMove;
	
	protected GUIPlayer(byte player, BoardModel state){
		super(player, state);
		teamName = "GUI";
	}
	
	@Override
	public Point getMove(BoardModel state) {
		synchronized(this){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lastMove;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		lastMove = new java.awt.Point((Integer) b.getClientProperty("x"),(Integer) b.getClientProperty("y"));
		synchronized(this){
			this.notify();
		}
	}

}
