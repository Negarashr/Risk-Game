package risk.game.grp.twenty.model;

/**
 * Class for each Game Winner pair
 *
 * @author Team 20
 */
public class GameWinnerPair {

  private String gameNumber;
  private String winner;

  public GameWinnerPair(String gameNumber, String winner) {

    this.gameNumber = gameNumber;
    this.winner = winner;
  }

  public String getGameNumber() {
    return gameNumber;
  }

  public void setGameNumber(String gameNumber) {
    this.gameNumber = gameNumber;
  }

  public String getWinner() {
    return winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  @Override
  public String toString() {
    return "GameWinnerPair{" +
        ", gameNumber='" + gameNumber + '\'' +
        ", winner='" + winner + '\'' +
        '}';
  }
}
