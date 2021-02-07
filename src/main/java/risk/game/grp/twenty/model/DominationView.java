package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * model for displaying (1) the percentage of the map controlled by every player (2) the continents
 * controlled by every player (3) the total number of armies owned by every player
 *
 * @author ali
 */
public class DominationView implements Serializable {

  private static final long serialVersionUID = 1L;
  Map<String, String> playersMapOwnership = new HashMap<String, String>();
  Map<String, List<String>> playersContinentOwnership = new HashMap<String, List<String>>();
  Map<String, String> playersTotalArmy = new HashMap<String, String>();

  public Map<String, String> getPlayersMapOwnership() {
    return playersMapOwnership;
  }

  public void setPlayersMapOwnership(Map<String, String> playersMapOwnership) {
    this.playersMapOwnership = playersMapOwnership;
  }

  public Map<String, List<String>> getPlayersContinentOwnership() {
    return playersContinentOwnership;
  }

  public void setPlayersContinentOwnership(
      Map<String, List<String>> playersContinentOwnership) {
    this.playersContinentOwnership = playersContinentOwnership;
  }

  public Map<String, String> getPlayersTotalArmy() {
    return playersTotalArmy;
  }

  public void setPlayersTotalArmy(Map<String, String> playersTotalArmy) {
    this.playersTotalArmy = playersTotalArmy;
  }

  @Override
  public String toString() {
    return "DominationView{" +
        "playersMapOwnership=" + playersMapOwnership +
        ", playersContinentOwnership=" + playersContinentOwnership +
        ", playersTotalArmy=" + playersTotalArmy +
        '}';
  }
}
