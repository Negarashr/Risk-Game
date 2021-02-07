package risk.game.grp.twenty.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Tournament</em> phase, and
 * assign them to the correspondence method to handel it in model, and provide appropriate response
 * for UI section.
 * </p>
 *
 * @author Team 20
 */
public class Tournament {

  private List<String> tournamentMaps = new ArrayList<>();
  private List<String> playerTypes = new ArrayList<>();
  private int numberOfTurns;
  private int numberOfGames;

  public List<String> getTournamentMaps() {
    return tournamentMaps;
  }

  public void setTournamentMaps(List<String> tournamentMaps) {
    this.tournamentMaps = tournamentMaps;
  }

  public List<String> getPlayerTypes() {
    return playerTypes;
  }

  public void setPlayerTypes(List<String> playerTypes) {
    this.playerTypes = playerTypes;
  }

  public int getNumberOfTurns() {
    return numberOfTurns;
  }

  public void setNumberOfTurns(int numberOfTurns) {
    this.numberOfTurns = numberOfTurns;
  }

  public int getNumberOfGames() {
    return numberOfGames;
  }

  public void setNumberOfGames(int numberOfGames) {
    this.numberOfGames = numberOfGames;
  }
}
