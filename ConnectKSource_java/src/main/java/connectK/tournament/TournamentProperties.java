package connectK.tournament;

//TODO: Load from properties file
public final class TournamentProperties {
	private static BoardConfig boardConfig[];
	private static BoardConfig testConfig[];

	static{
		boardConfig = new BoardConfig[] { new BoardConfig(5, 9, 5, 1), new BoardConfig(5, 9, 5, 0),

				new BoardConfig(8, 8, 4, 1) };
		testConfig = new BoardConfig[] { new BoardConfig(5, 9, 5, 0) };
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
