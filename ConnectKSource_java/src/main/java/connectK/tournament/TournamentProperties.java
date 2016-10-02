/** Copyright (c) 2016 SalakoTech.
 * This file and all of its contents belong to SalakoTech and should not be shared.
 * Created on: Jul 2, 2016
 * @author Toluwanimi Salako
 * Last edited: Jul 2, 2016
 */
package connectK.tournament;

//TODO: Load from properties file
public class TournamentProperties {
	private static BoardConfig boardConfig[];

	public TournamentProperties() {
		boardConfig = new BoardConfig[] { new BoardConfig(5, 9, 5, 1), new BoardConfig(5, 9, 5, 0),

				new BoardConfig(8, 8, 4, 1) };
	}

	/**
	 * @return the boardConfig
	 */
	public static BoardConfig[] getBoardConfig() {
		return boardConfig;
	}

	/**
	 * @param boardConfig
	 *            the boardConfig to set
	 */
	public static void setBoardConfig(BoardConfig[] boardConfig) {
		TournamentProperties.boardConfig = boardConfig;
	}

}
