package connectK.tournament;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import connectK.CKPlayerFactory;

public class TournamentMain {
	private static Logger LOG = LoggerFactory.getLogger(TournamentMain.class);

	private static String ZIPPED_AI_DIR = "AssignmentSubmission";
	private final static String EXTRACTED_AI_DIR = "extracted_ais";
	private final static String TEST_AIS[] = {"GoodAI", "AverageAI", "PoorAI"}; 
	private final static String TEST_AI_DIR = "../ConnectKSource_java";
	private static List<TournamentPlayer> playerList;

	public static void main(String[] args) {

		if (args.length < 1)
			System.out.println("Assignment Submission Arg not found. Assuming default location");
		else
			ZIPPED_AI_DIR = args[0];

		File zippedAis = new File(ZIPPED_AI_DIR);
		File extractedAis = Paths.get(ZIPPED_AI_DIR, EXTRACTED_AI_DIR).toFile();
		try {
			if (!zippedAis.exists())
				throw new FileNotFoundException(ZIPPED_AI_DIR + " was not found in working directory.");

			if (!extractedAis.exists()) {
				// TODO: re extract and overwrite all AIs
			}
			
			playerList = new ArrayList<>();
			// Populate list with player factories
			loadPlayers(extractedAis);
			// Include GOOD and AVG ais
			loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[0]).toFile());
			loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[1]).toFile());
			
			// Make sure player list is even, if it's not, balance it with poor AI
			if (playerList.size() % 2 != 0){
				LOG.info("Found uneven player list. Balancing it with test AI: {}", TEST_AIS[2]);
				loadPlayers(Paths.get(TEST_AI_DIR, TEST_AIS[2]).toFile());
			}
			
			System.out.println("Successfully loaded " + playerList.size() + " AIs.");
			if (!(playerList.size() > 0))
				exit(0);

			// Load players into tournament
			Tournament tournament = new Tournament(playerList);//.subList(0, 4));
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
				System.out.println("Successfully loaded: " + f.getName());
			}else{
				LOG.error("Could not load file: {}", f.getName());
			}
		}

	}

	private static TournamentPlayer getPlayer(String name, String path) {
		CKPlayerFactory factory;
		String prefix = "";
		if (name.endsWith(CKPlayerFactory.cppPostfix))
			prefix = CKPlayerFactory.cppPrefix;
		else if (name.endsWith(CKPlayerFactory.javaPostfix) && !name.equals("AverageAI.class"))
		    prefix = "";
		else if (name.endsWith(CKPlayerFactory.pythonPostfix))
			prefix = CKPlayerFactory.pythonPrefix;
		try {
			factory = new CKPlayerFactory(prefix + path);
			return new TournamentPlayer(factory, playerList.size());
		} catch (IllegalArgumentException | NoClassDefFoundError | ClassFormatError e) {
			LOG.error("Could not load AI: {}", name, e);
		}
		return null;
	}

	private static void exit(int code) {
		System.exit(code);
	}

}
