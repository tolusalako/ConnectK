package connectK.tournament;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes in a list of all players, and matches them against each other TODO:
 * Change the boardConfig, and make sure the games are timed.
 */
public class Tournament {
	Logger LOG = LoggerFactory.getLogger(Tournament.class);
	private static final String NO_GROUP = "No Group";
	final long AVG_GAME_TIME = 5000;
	final TournamentProperties config;

	ExecutorService executor;
	// Holds the results of our game along with a boolean thats true if we've
	// used the result;
	Queue<Future<TournamentGroup>> resultGroups;
	final int MAX_THREADS = 100;
	List<TournamentPlayer> playerList;
	final int playerCount;

	public Tournament(List<TournamentPlayer> playerList) {
		// Setup and shuffle the player list
		playerCount = playerList.size();
		resultGroups = new ConcurrentLinkedQueue<Future<TournamentGroup>>();
		executor = Executors.newFixedThreadPool(MAX_THREADS);

		long seed = System.nanoTime();
		this.playerList = playerList;
		Collections.shuffle(this.playerList, new Random(seed));

		// Load in configurations
		config = new TournamentProperties();
	}

	// Starts the tournament
	public void begin() {
		// List of groups who played/ Each group is ranked by each players
		// performance.

		TournamentPlayer p1 = null, p2 = null;
		Future<TournamentGroup> result;
		LOG.info("AIs participating in the tournament:");
		playerList.stream().map(p -> p.getName()).forEach(LOG::info);

		Date start = new Date();
		LOG.info("Tournament starting at {} with {} players", start.toString(), playerCount);
		// First Round
		char round = 'A';
		for (int i = 1; i <= playerCount; i += 2) {
			try {
				p1 = playerList.get(i - 1);
				p2 = playerList.get(i);
				LOG.info("Playing {} vs {}.", p1, p2, round);
			} catch (IndexOutOfBoundsException e) {
				LOG.error("Couldn't start tournament", e);
			} finally {
				// Even if they are null, add them. The algorithm will sort them
				// out.
				result = executor.submit(
						new TournamentMatch((p1 != null) ? new TournamentGroup(1, p1.getGroupId(), round, p1) : null,
								(p2 != null) ? new TournamentGroup(1, p2.getGroupId(), round, p2) : null, i));
				resultGroups.add(result);
			}
			p1 = null;
			p2 = null;
		}

		// Remaining rounds Rounds
		continueTournament();

		Date end = new Date();
		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long diff = Math.abs(end.getTime() - start.getTime());
		LOG.info(("Tournament ended at " + end.toString()));
		LOG.info(String.format("Total time: %s hours %s mins %s seconds", diff / (60 * 60 * 1000) % 24,
				diff / (60 * 1000) % 60, diff / 1000 % 60));
		try {
			Future<TournamentGroup> finalResult = resultGroups.poll();
			if (!finalResult.isDone()) {
				LOG.info("Waiting for final games to finish.");
				sleep(AVG_GAME_TIME);
			}

			LOG.info("Rankings:");
			TournamentGroup finalGroup = finalResult.get();
			while (!finalGroup.isEmpty()) {
				TournamentPlayer team = finalGroup.poll();
				LOG.info(String.format("[%s]: %s", team.getRank(), team.getName()));
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void continueTournament() {
		// For now the game is run on a round by round basis
		// Can be improved where 'ready' groups start the next round;
		// When size == 1 That's our final ranks
		while (resultGroups.size() != 1) {
			LOG.debug("Continuing tournament. # of Groups = {}", resultGroups.size());
			while (!readyForNextRound()) {
				sleep(10);//sleep(AVG_GAME_TIME);
			}
			LinkedList<Future<TournamentGroup>> newGroups = new LinkedList<>();
			int id = 1;
			TournamentGroup g1 = null, g2 = null;
			while (!resultGroups.isEmpty()) {
				// Poll a pair of groups from newGroups and create a match
				try {
					final Future<TournamentGroup> g1Future = resultGroups.poll();
					final Future<TournamentGroup> g2Future = resultGroups.poll();
					Queue<Future<TournamentGroup>> futures = new ConcurrentLinkedQueue<Future<TournamentGroup>>() {
						{
							if (g1Future != null)
								add(g1Future);
							if (g2Future != null)
								add(g2Future);
						}
					};
					while (!isDone(futures)) {
						LOG.debug("A group is still playing. G1.isDone(): {}, G2.isDone(): {}",
								(g1Future != null) ? g1Future.isDone() : NO_GROUP,
								(g2Future != null) ? g2Future.isDone() : NO_GROUP);
						Thread.sleep(100);
					}
					if (g1Future != null)
						g1 = g1Future.get();
					if (g1Future != null)
						g2 = g2Future.get();
				} catch (InterruptedException | NullPointerException | ExecutionException e) {
					LOG.warn("Could not get a group ", e);
				} finally {
					LOG.info("Group (G1): {} \n\t\t\t\t\t\t\t=========vs (round {})===========\n\t\t\t\t\tGroup (G2): {}.",
							(g1 != null) ? g1.toArray() : NO_GROUP, id, (g2 != null) ? g2.toArray() : NO_GROUP);
					// Add match to new groups
					newGroups.add(executor.submit(new TournamentMatch(g1, g2, id++)));
				}
			}
			
			resultGroups = newGroups;

		}
		// Tournament over. Only 1 group left with final ranks
	}

	private boolean readyForNextRound() {
		return isDone(resultGroups);
	}

	private boolean isDone(Queue<Future<TournamentGroup>> futures) {
		Predicate<Future<TournamentGroup>> notDone = f -> f.isDone() == false;
		return futures.stream().noneMatch(notDone);
	}

	private void sleep(long sleepTime) {
		try {
			Thread.sleep(AVG_GAME_TIME);
		} catch (InterruptedException e) {
			LOG.error("Could not sleep...", e);
		}
	}

}
