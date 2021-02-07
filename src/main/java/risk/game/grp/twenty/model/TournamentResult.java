package risk.game.grp.twenty.model;

public class TournamentResult {

  private String mapName;
  private String gameNumber;
  private String winner;

  /**
   * This constructor  create TournamentResult
   *
   * @param mapName : Name of the map
   * @param gameNumber : Number of the game
   * @param winner: Name of the player or 'Draw' in case of draw
   */
  public TournamentResult(String mapName, String gameNumber, String winner) {
    this.mapName = mapName;
    this.gameNumber = gameNumber;
    this.winner = winner;
  }

  /**
   * Method returning name of the map
   *
   * @return Name of the map
   */
  public String getMapName() {
    return mapName;
  }

  /**
   * Method setting name of the map
   *
   * @param mapName Name of the map
   */
  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  /**
   * Method getting name of the map
   *
   * @return Number of the game
   */
  public String getGameNumber() {
    return gameNumber;
  }

  /**
   * Method setting name of the game
   *
   * @param gameNumber Number of the game
   */
  public void setGameNumber(String gameNumber) {
    this.gameNumber = gameNumber;
  }

  /**
   * Method returning winner or 'Draw'
   *
   * @return the name of the winner
   */
  public String getWinner() {
    return winner;
  }

  /**
   * Method set the name of the winner
   *
   * @param winner set the name of the winner
   */
  public void setWinner(String winner) {
    this.winner = winner;
  }

  /**
   * Method to convert to string
   *
   * @return All the values as string
   */
  @Override
  public String toString() {
    return "TournamentResult{" +
        "mapName='" + mapName + '\'' +
        ", gameNumber='" + gameNumber + '\'' +
        ", winner='" + winner + '\'' +
        '}';
  }
}
