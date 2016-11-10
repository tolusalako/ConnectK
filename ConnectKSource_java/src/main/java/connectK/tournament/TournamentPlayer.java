package connectK.tournament;

import java.util.Comparator;

import connectK.CKPlayerFactory;

class TournamentPlayer implements Comparator<TournamentPlayer> {
	private int rank;
	private int groupId;
	private CKPlayerFactory player;

	/**
	 * Create a new tournament player with rank 0;
	 * 
	 * @param player
	 */
	public TournamentPlayer(CKPlayerFactory player, int groupId) {
		this.player = player;
		this.groupId = groupId;
		rank = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TournamentPlayer o1, TournamentPlayer o2) {
		return o1.rank - o2.rank;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the id
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the player
	 */
	public CKPlayerFactory getPlayerFactory() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayerFactory(CKPlayerFactory player) {
		this.player = player;
	}

	public String getName() {
		return player.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + groupId;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
//		result = prime * result + rank;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TournamentPlayer other = (TournamentPlayer) obj;
		if (groupId != other.groupId)
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (rank != other.rank)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return player.toString() + "[" + rank + "]";
	}
}
