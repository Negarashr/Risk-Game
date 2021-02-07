package risk.game.grp.twenty.play.phase.impl.fortification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.SkipPhase;
import risk.game.grp.twenty.play.phase.impl.play.phase.FortificationPhase;

/**
 * this class represents logic for <em>Fortification</em> phase, in game.
 *
 * @author ali
 */

@Component
public class FortificationPhaseImpl implements FortificationPhase, Serializable {


  private final static Logger LOGGER = LoggerFactory.getLogger(FortificationPhaseImpl.class);
  private static final long serialVersionUID = 1L;

  /**
   * Stating point in <em>Fortification Model</em> for handling request from Controller.
   *
   * @param playerNumber Player turn who is playing and is in fortification phase
   * @param sourceCountry source country to move armies from
   * @param destinationCountry destination country to move armies to
   * @param numberOfArmies number of armies to move
   * @throws GameException if any rules of game for this phase being violated this exception throws
   */
  public void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {
    LOGGER.info("fortification started for: {}, from: {}, to: {}", playerNumber, sourceCountry,
        destinationCountry);

    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Player player = players.get(playerTurnNumber);
    Country srcCountry = countries.get(sourceCountry);
    Country destCountry = countries.get(destinationCountry);
    final int army = Integer.parseInt(numberOfArmies);

    validateUserInput(countries, players, playerNumber, sourceCountry, destinationCountry,
        numberOfArmies);

    verifyPlayerIsInFortificationPhase(player);

    doesPlayerOwnCountry(player, srcCountry.getName());

    doesPlayerOwnCountry(player, destCountry.getName());

    List<List<String>> allPaths = GameMap.getAllPaths(sourceCountry, destinationCountry);

    verifyPathExist(allPaths, sourceCountry, destinationCountry);

    verifyPlayerOwensAllCountriesInPath(player, allPaths);

    updateArmies(srcCountry, destCountry, army);

    updatePlayersStatus(players, playerTurnNumber);

  }

  /**
   * This method validates the user input for this phase.
   *
   * @param countries list of all countries in the game.
   * @param players list of all players in the game.
   * @param playerNumber Player turn who is playing and is in fortification phase
   * @param sourceCountry source country to move armies from
   * @param destinationCountry destination country to move armies to
   * @param numberOfArmies number of armies to move
   * @throws GameException if any rules of game for this phase being violated this exception throws
   */
  private void validateUserInput(Map<String, Country> countries, Map<Integer, Player> players,
      String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Player player = players.get(playerTurnNumber);
    Country srcCountry = countries.get(sourceCountry);
    Country destCountry = countries.get(destinationCountry);
    final int army = Integer.parseInt(numberOfArmies);

    if (player == null) {
      throw new GameException("Player '" + playerTurnNumber + "' Not found!");
    }

    if (srcCountry == null) {
      throw new GameException("Country '" + sourceCountry + "' is not found");
    }

    if (destCountry == null) {
      throw new GameException("Country '" + destinationCountry + "' is not found");
    }

    if (destCountry.getName().equalsIgnoreCase(srcCountry.getName())) {
      throw new GameException("Source and destination countries can not be same.");
    }

    if (army < 1) {
      throw new GameException("Zero or negative number of armies: " + numberOfArmies);
    }

    if (srcCountry.getArmies() - army < 1) {
      throw new GameException(
          "Not enough Armies!, After this move " + sourceCountry + " armies will be less than 1!");
    }
  }

  /**
   * This method checks if player is in fortification phase
   *
   * @param player list of all players in the game.
   * @throws GameException if player is not in <em>Fortification</em> phase
   */
  private void verifyPlayerIsInFortificationPhase(Player player) throws GameException {
    if (player.getPlayerStatus() != PlayerStatus.Fortification) {
      throw new GameException(
          "Player: " + player.getPlayerName() + " is in " + player.getPlayerStatus()
              + ", player should be in fortification phase.");
    }
  }

  /**
   * this method checks whether a player owns a country or not
   *
   * @param player list of all players in the game.
   * @param countryName name of country to check
   * @return true if the player owns the country else false
   */
  private boolean doesPlayerOwnCountry(Player player,
      String countryName) {
    List<Country> playerCountries = player.getCountries();

    boolean ownsCountry = playerCountries.stream()
        .anyMatch(c -> c.getName().equalsIgnoreCase(countryName));

    return ownsCountry;
  }

  /**
   * this method generates all available paths if any between 2 countries
   *
   * @param allPaths list of all paths between 2 countries
   * @param sourceCountry source country for checking path
   * @param destinationCountry destination country for checking path
   * @throws GameException if no path found between 2 countries
   */
  private void verifyPathExist(List<List<String>> allPaths, String sourceCountry,
      String destinationCountry)
      throws GameException {
    if (allPaths == null || allPaths.size() == 0) {
      throw new GameException(
          "There is no Path between " + sourceCountry + " and " + destinationCountry);
    }
  }

  /**
   * verifies there is at least one path between 2 countries which the player owns all country in
   * it
   *
   * @param player the player in fortification phase
   * @param allPaths list of all paths between 2 countries
   * @throws GameException if the player not owns all countries found in the path
   */
  private void verifyPlayerOwensAllCountriesInPath(Player player, List<List<String>> allPaths)
      throws GameException {
    int unsuccessfulPathCount = 0;
    for (int i = 0; i < allPaths.size(); i++) {
      for (String country : allPaths.get(i)) {
        if (!doesPlayerOwnCountry(player, country)) {
          unsuccessfulPathCount++;
          break;
        }
      }
    }
    if (unsuccessfulPathCount == allPaths.size()) {
      throw new GameException("Player Not own all countries found in paths.");
    }
  }

  /**
   * this method updates the source and destination country
   *
   * @param srcCountry source country
   * @param destCountry destination country
   * @param army number of armies to move
   */
  private void updateArmies(Country srcCountry, Country destCountry, int army) {
    final int sourceArmies = srcCountry.getArmies();
    final int destArmies = destCountry.getArmies();
    srcCountry.setArmies(sourceArmies - army);
    destCountry.setArmies(destArmies + army);
  }

  /**
   * this method updates the players status after successful move
   *
   * @param players list of all players in game
   * @param playerTurnNumber player who is playing now
   */
  private void updatePlayersStatus(Map<Integer, Player> players, int playerTurnNumber) {

    SkipPhase.skip(String.valueOf(playerTurnNumber));

  }
}
