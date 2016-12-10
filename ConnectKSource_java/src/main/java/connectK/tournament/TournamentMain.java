package connectK.tournament;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import connectK.CKPlayerFactory;
import connectK.tournament.TournamentMatch.TournamentGame;
import connectK.utils.LogUtils;

public class TournamentMain {
	private static Logger LOG = LoggerFactory.getLogger(TournamentMain.class);

	private static String EXTRACTED_AI_DIR = "AssignmentSubmission";
	private final static String TEST_AIS[] = { "GoodAI", "AverageAI", "PoorAI" };
	private final static String TEST_AI_DIR = "../ConnectKSource_java/SampleAI";
	private static List<TournamentPlayer> playerList;
	private static List<String> loadedPlayers;

	public static void main(String[] args) {

		if (args.length < 1)
			System.out.println("No arg found. Assuming default location ["+EXTRACTED_AI_DIR+"]");
		else
			EXTRACTED_AI_DIR = args[0];

		File extractedAis = new File(EXTRACTED_AI_DIR);
		try {
			if (!extractedAis.exists())
				throw new FileNotFoundException(EXTRACTED_AI_DIR + " was not found.");

			playerList = new ArrayList<>();
			loadedPlayers = new ArrayList<>();
			
			// Populate list with player factories
			loadPlayers(extractedAis);
//			 Include GOOD and AVG ais
			loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[0]).toFile());
			loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[1]).toFile());
		

			// Make sure player list is even, if it's not, balance it with poorAI
			if (playerList.size() % 2 != 0) {
				LOG.info("Found uneven player list. Balancing it with test AI: {}", TEST_AIS[2]);
				loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[2]).toFile());
			}

			LOG.info("Successfully loaded " + playerList.size() + " AIs.");
			if (playerList.isEmpty())
				exit(0);
			
			vsPoorAI(playerList);
			exit(0);

			// testPlayers(playerList);

			// Load players into tournament
			Tournament tournament = new Tournament(playerList);
			// Start the tournament!
			tournament.begin();
			exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			exit(1);
		}

	}

	private static void loadPlayers(File pathname) {
		if (!pathname.isDirectory())
			return;
		for (File f : pathname.listFiles()) {
			if (f.isDirectory()) {
				loadPlayers(f);
				continue;
			}
			TournamentPlayer player = getPlayer(f.getName(), f.getAbsolutePath());
			if (null != player) {
				playerList.add(player);
				LOG.info("Successfully loaded {}", player.getName());
				LogUtils.logAI(LOG, Level.INFO, player.getName(), "Successfully loaded " + player.getName(), null);
			} else {
				LOG.error("Could not load file: {}", f.getName());
			}
		}

	}

	private static TournamentPlayer getPlayer(String name, String path) {
		CKPlayerFactory factory;
		String prefix = "";
		String playerName = name;
		if (name.endsWith("AI." + CKPlayerFactory.javaPostfix) && !name.equals("AverageAI.class")){
			// JAVA AIs
			prefix = "";
			playerName = name.substring(0, name.indexOf('.'));
		}else if (name.endsWith("AI." + CKPlayerFactory.pythonPostfix) && !name.startsWith(".")){
			// Python AIs
			prefix = CKPlayerFactory.pythonPrefix;
			playerName = name.substring(0, name.indexOf('.'));
		}else if (name.endsWith("AI." + CKPlayerFactory.cppPostfixWindows) || name.endsWith("AI")){
				prefix = CKPlayerFactory.cppPrefix;
				playerName = (name.endsWith("AI")) ? playerName : name.substring(0, name.indexOf('.'));
		}else if (name.startsWith(".")){
			LOG.error("Could not load AI: {}", playerName);
			return null;
		}else // Invalid files
			return null;
		
		try {
			factory = new CKPlayerFactory(prefix + path);
			if (loadedPlayers.contains(playerName))
			{
				LOG.error("Skipping duplicate AI {} in {}", playerName, path);
				return null;
			}else
			{
				LOG.debug("Loading player {}", playerName);
				LogUtils.logAI(LOG, Level.DEBUG, playerName, "Loading player " + playerName, null);
				loadedPlayers.add(playerName);
			}
			return new TournamentPlayer(factory, playerList.size());
		} catch (IllegalArgumentException | NoClassDefFoundError | ClassFormatError e) {
			LOG.error("Could not load AI", playerName, e);
			LogUtils.logAI(LOG, Level.ERROR, playerName, "Could not load AI", e);
		}
		return null;
	}

	private static void vsPoorAI(List<TournamentPlayer> players) {
		File poorAIFile = Paths.get(TEST_AI_DIR, TEST_AIS[2], TEST_AIS[2] + ".class").toFile();
		TournamentPlayer poorAI = getPlayer(poorAIFile.getName(), poorAIFile.getAbsolutePath());
		if (poorAI == null){
			for (TournamentPlayer player : players)
				if (player.getName().equals("PoorAI")){
					poorAI = player;
					break;
				}			
			players.remove(poorAI);
		}
		LOG.info("Playing {} AIs against poorAI", players.size());
		ExecutorService executors = Executors.newFixedThreadPool(200);
		Iterator<TournamentPlayer> playerIter = players.iterator();
		Queue<Future<TournamentMatch>> futures = new ConcurrentLinkedQueue<Future<TournamentMatch>>();
		Map<String, Integer> vsPoor = new ConcurrentSkipListMap<String, Integer>();
		while (playerIter.hasNext()){
			final TournamentPlayer poorAICopy = poorAI;
			Future f = executors.submit(() -> {
				TournamentPlayer player = playerIter.next();
				try {
					TournamentGame game =  new TournamentGame(player, poorAICopy);
					int result = game.start(1);
					vsPoor.put(player.getName(), result == 1? 1 : 0);
					return result;
				} catch (Exception e) {
					LOG.error("Player {} Crash", player.getName(), e);
					LogUtils.logAI(LOG, Level.ERROR, player.getName(), String.format("Player %s Crash", player.getName()), e);
				} //vs PoorAI
				return 2; //PoorAI wins
			});
			futures.add(f);
		}
		while (!isDone(futures)) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				LOG.error("Could not sleep thread", e);
			}
		}
		LOG.debug("Games complete");
		System.out.println("Results vs PoorAI\nAI\tWon");
		for (Entry<String, Integer> entry : vsPoor.entrySet()){
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		exit(0);
	}

	private static boolean isDone(Queue<Future<TournamentMatch>> futures) {
		Predicate<Future<TournamentMatch>> notDone = f -> f.isDone() == false;
		return futures.stream().noneMatch(notDone);
	}
	
	private static void exit(int code) {
		System.exit(code);
	}

}
