package connectK.tournament;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class TournamentGroup extends PriorityQueue<TournamentPlayer> {

	private static final long serialVersionUID = 3078508462572927681L;
	final int groupId;
	final char round;
	public TournamentGroup(int capacity, int groupId, char round) {
		super(capacity, new Comparator<TournamentPlayer>() {
			@Override
			public int compare(TournamentPlayer o1, TournamentPlayer o2) {
				return o1.getRank() - o2.getRank();
			}
		});
		this.groupId = groupId;
		this.round = round;
	}

	public TournamentGroup(int capacity, int groupId, char round, TournamentPlayer... players) {
		this(capacity, groupId, round);
		this.addAll(Arrays.asList(players));
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}
	
	public char getRound() {
		return round;
	}

	@Override
	public String toString() {
		return "TournamentGroup [groupId=" + groupId + ", round=" + round + "], AIs: " + super.toString();
	}

}
