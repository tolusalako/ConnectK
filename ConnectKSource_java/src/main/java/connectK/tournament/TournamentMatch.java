package connectK.tournament;

import java.rmi.UnexpectedException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import connectK.BoardModel;
import connectK.CKPlayer;
import connectK.ConnectK;
import connectK.utils.LogUtils;

class TournamentMatch implements Callable<TournamentGroup> {
	Logger LOG = LoggerFactory.getLogger(TournamentMatch.class);
	TournamentGroup groupA;
	TournamentGroup groupB;
	final int destGroupId;

	/**
	 * @param groupA
	 * @param groupB
	 * @param destGroupId
	 *            - New group ID when both groups merge
	 * @return
	 * @throws UnexpectedException
	 */
	public TournamentMatch(TournamentGroup groupA, TournamentGroup groupB, final int destGroupId) {
		this.groupA = groupA;
		this.groupB = groupB;
		this.destGroupId = destGroupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public TournamentGroup call() throws Exception {
		if (groupA == null || groupA.isEmpty())
			return groupB;
		else if (groupB == null || groupB.isEmpty())
			return groupA;

		// int gameRound = 1;
		TournamentGroup result = new TournamentGroup(groupA.size() + groupB.size(), destGroupId,
				(char) (groupA.getRound() + 1));
		TournamentPlayer p1 = groupA.poll();
		TournamentPlayer p2 = groupB.poll();
		TournamentGame game;
		while (!groupA.isEmpty() || !groupB.isEmpty() || null != p1 || null != p2) {
			// If any of the players equal null, add the last player to the
			// end
			// of list;
			if (null == p1 && !groupA.isEmpty()) 
					p1 = groupA.poll();
			if (null == p2 && !groupB.isEmpty())
					p2 = groupB.poll();
			
			
			// After first game, loser plays next player from opposing
			// group.
			int winner, oldGroupId;
			try {
				if (p1 != null && p2 != null){
					game = new TournamentGame(p1, p2);
					winner = game.start(1);
				}else if (p1 == null)
					winner = 2;
				else
					winner = 1;
				
				if (winner == 1) {
					// Set player 1 as winner, and match player 2 against
					// next
					// player on the opposing group.
					p1.setRank(result.size());
					oldGroupId = p1.getGroupId();
					p1.setGroupId(destGroupId);
					result.add(p1);
					if (oldGroupId == groupA.getGroupId())
						p1 = groupA.poll();
					else
						p1 = groupB.poll();
				} else if (winner == 2) {
					// Set player 2 as winner, and match player 1 against
					// next
					// player on the opposing group.
					p2.setRank(result.size());
					oldGroupId = p2.getGroupId();
					p2.setGroupId(destGroupId);
					result.add(p2);
					if (oldGroupId == groupA.getGroupId()) //TODO: Should they poll from other group?
						p2 = groupA.poll();
					else
						p2 = groupB.poll();
				} else
					throw new UnexpectedException(
							"A game came back as a draw. There should always be a declared winner.");
			} catch (InterruptedException | ExecutionException e) {
				// TODO If a game doesn't finish, what do we do?
				LOG.error("Game didnt finish.", e);
			}

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\n[TournamentMatch] \n\tgroupA=" + groupA + ", \n\tgroupB=" + groupB;
	}

	public static class TournamentGame {
		static Logger LOG = LoggerFactory.getLogger(TournamentGame.class);
		TournamentPlayer p1;
		TournamentPlayer p2;
		 final String playersPlaying;
//		final ExecutorService gameExecutors; 
			int p1score = 0;
			int p2score = 0;

		public TournamentGame(TournamentPlayer p1, TournamentPlayer p2) {
			this.p1 = p1;
			this.p2 = p2;
			playersPlaying = String.format("Group: [%s vs %s] Players: [%s vs %s];", p1.getGroupId(), p2.getGroupId(), p1.getName(), p2.getName());
//			gameExecutors = Executors.newFixedThreadPool(3);
		}
		

		public Integer start(int round) throws Exception {
			// Play p1 vs p2 (n * 2 times, with each player going 1st) and
			// return
			// winner id;
			int h, w, k;
			boolean g;
			BoardConfig config[] = TournamentProperties.getBoardConfig();
			// TODO: Spawn different threads to play each game?
			for (int i = 0; i < config.length; i++) {
				h = config[i].getHeight();
				w = config[i].getWidth();
				k = config[i].getK();
				g = config[i].getGravity();
				LogUtils.logAIs(LOG, Level.INFO, playersPlaying + String.format("Game %s. H: %s, W: %s, K: %s, G: %s", i, h, w, k, g), null, p1.getName(), p2.getName());

				BoardModel model = new BoardModel(w, h, k, g);
				CKPlayer player1 = p1.getPlayerFactory().getPlayer((byte) 1, model);
				CKPlayer player2 = p2.getPlayerFactory().getPlayer((byte) 2, model);

				LogUtils.logAIs(LOG, Level.INFO, String.format("Player 1: %s, Player 2: %s", p1.getName(), p2.getName()), null, p1.getName(), p2.getName());

//				gameExecutors.submit(task) //TODO use executors
				ConnectK game = new ConnectK(model, player1, player2); // New
																		// NoGui
				byte winner = game.play();

				// Assign scores based on winner
				if (winner == 1){
					p1score++;
					LOG.info("Game Ended [{}]. Winner: " + p1.getName(), playersPlaying);
					LogUtils.logAIs(LOG, Level.INFO, String.format("Game Ended. Winner: %s.",	p1.getName()), null, p1.getName(), p2.getName());
				}
				else if (winner == 2){
					p2score++;
					LOG.info("Game Ended [{}]. Winner: " + p2.getName(), playersPlaying);
					LogUtils.logAIs(LOG, Level.INFO, String.format("Game Ended. Winner: %s.",	p2.getName()), null, p1.getName(), p2.getName());
				}else{
					LOG.info("Game Ended [{}]. Draw!", playersPlaying);
					LogUtils.logAIs(LOG, Level.INFO, "Game Ended. DRAW!", null, p1.getName(), p2.getName());
				}
				LOG.info("Round: {} | Score P1: {}, P2: {}", round, p1score, p2score);
				LogUtils.logAIs(LOG, Level.INFO, String.format("Round: %s | Score P1: %s, P2: %s", round, p1score, p2score), null, p1.getName(), p2.getName());
				
				// TODO: this can be more efficient. What about ending the match
				// if there is a significant lead. I.e 2/3
			}

			// After the game return the winner of 3 rounds. If draw, #TODO
			// random
			// game?
			if (p1score > p2score)
				return 1;
			else if (p2score > p1score)
				return 2;
			else {
//				if (config > 2)
					return new Random().nextInt((2 - 1) + 1) + 1;
//				else
//					return start(round++);
			}
		}

	}

}