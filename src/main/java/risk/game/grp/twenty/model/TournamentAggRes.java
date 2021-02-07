package risk.game.grp.twenty.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Model of <em>TournamentAggRes</em> Object in Game.
 *
 * @author Team 20
 */
public class TournamentAggRes {

  List<GameWinnerPair> gameWinnerPairs = new ArrayList<>();
  private String mapName;

  /**
   * This Method set the name of map in TournamentAgg
   *
   * @param mapName set the name of map
   */
  public TournamentAggRes(String mapName) {
    this.mapName = mapName;
  }

  /**
   * This Method get the name of map
   *
   * @return mapName set the name of map
   */
  public String getMapName() {
    return mapName;
  }

  /**
   * This Method set the name of map
   *
   * @param mapName set the name of map
   */

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  /**
   * This method return the list of winners in Tournament
   *
   * @return list of game winners
   */
  public List<GameWinnerPair> getGameWinnerPairs() {
    return gameWinnerPairs;
  }

  /**
   * This Method set the winners in Tournament
   *
   * @param gameWinnerPairs set the winners
   */
  public void setGameWinnerPairs(
      List<GameWinnerPair> gameWinnerPairs) {
    this.gameWinnerPairs = gameWinnerPairs;
  }
}
