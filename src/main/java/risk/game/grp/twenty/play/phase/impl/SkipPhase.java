package risk.game.grp.twenty.play.phase.impl;

import static risk.game.grp.twenty.constant.GameConstant.FIRST_PLAYER_IN_LIST;
import static risk.game.grp.twenty.constant.GameConstant.MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES;

import java.io.Serializable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * This Class is Responsible for Skip in the Game
 *
 * @author ali
 */
@Component
public class SkipPhase implements Serializable {


  private final static Logger LOGGER = LoggerFactory.getLogger(SkipPhase.class);
  private static final long serialVersionUID = 1L;

  /**
   * recalculates number of armies for a given player
   *
   * @param playerNumber player number to recalculate armies
   */
  public static void recalculateArmies(int playerNumber) {
    Player player = GameMap.getPlayers().get(playerNumber);
    if (player != null && player.getPlayerStatus() == PlayerStatus.Reinforcement) {
      int currentArmiesNumber = player.getArmy();
      int countriesAlreadyHas = player.getCountries().size();
      int countryBasedArmies = Math.round(countriesAlreadyHas / 3);
      int armiesFromContinents = calculateArmiesFromContinentControlValue(player);
      int totalNewArmies = countryBasedArmies + armiesFromContinents;

      if (totalNewArmies < MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES) {
        totalNewArmies = MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES;
      }

      player.setArmy(currentArmiesNumber + totalNewArmies);
    }
  }

  /**
   * checks whether given player owns all country in a continent and if so plus control value to the
   * players armies.
   *
   * @param player to check if owns all countries in a continent
   * @return 0 if not owning else control value
   */
  public static int calculateArmiesFromContinentControlValue(Player player) {
    int armiesFromContinents = 0;
    for (Continent continent : GameMap.getContinents().values()) {
      int countriesOwnFromContinent = 0;
      for (Country country : continent.getCountries()) {
        boolean ownsCountry = player.getCountries().stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(country.getName()));
        if (ownsCountry) {
          countriesOwnFromContinent++;
        }
      }
      if (countriesOwnFromContinent == continent.getCountries().size()) {
        armiesFromContinents += continent.getControlValue();
      }
    }
    return armiesFromContinents;
  }

  /**
   * change the current player status from <em>Fortification</em> to waiting and find next not
   * Eliminated Player and change the status to Reinforcement
   *
   * @param players list of all the players
   * @param playerTurnNumber player who is turn to play
   * @return number of next player in turn
   */
  public static int skipFortification(Map<Integer, Player> players, int playerTurnNumber) {
    int nextPlayerNumber = playerTurnNumber + 1;
    Player player = players.get(playerTurnNumber);

    while (nextPlayerNumber != playerTurnNumber) {

      if (players.get(nextPlayerNumber) != null) {
        if (players.get(nextPlayerNumber).getPlayerStatus() != PlayerStatus.Eliminated) {
          player.setPlayerStatus(PlayerStatus.Waiting);
          players.get(nextPlayerNumber).setPlayerStatus(PlayerStatus.Reinforcement);
          break;
        } else {
          nextPlayerNumber++;
        }

      } else {
        nextPlayerNumber = FIRST_PLAYER_IN_LIST; // reset to first player
        player.setPlayerStatus(PlayerStatus.Reinforcement);
      }
    }

    return nextPlayerNumber;
  }

  /**
   * shift players' status and if needed change next player in turn status
   *
   * @param playerNumber player to skip his/her current phase.
   */
  public static void skip(String playerNumber) {

    LOGGER.info("SkipPhase started");
    Map<Integer, Player> players = GameMap.getPlayers();
    int playerTurnNumber = Integer.parseInt(playerNumber);
    int nextPlayerNumber = playerTurnNumber + 1;
    Player player = players.get(playerTurnNumber);

    if (player.getPlayerStatus() == PlayerStatus.Reinforcement) {
      player.setPlayerStatus(PlayerStatus.Attack);
    } else if (player.getPlayerStatus() == PlayerStatus.Attack) {
      player.setPlayerStatus(PlayerStatus.Fortification);
    } else if (player.getPlayerStatus() == PlayerStatus.Fortification) {

      nextPlayerNumber = skipFortification(players, playerTurnNumber);

    }
    if (player.getPlayerStatus() == PlayerStatus.Placing) {
      player.setPlayerStatus(PlayerStatus.Waiting);
      if (players.get(nextPlayerNumber) != null) {
        players.get(nextPlayerNumber).setPlayerStatus(PlayerStatus.Placing);
      } else {
        nextPlayerNumber = FIRST_PLAYER_IN_LIST;
        players.get(nextPlayerNumber)
            .setPlayerStatus(PlayerStatus.Reinforcement); // reset to first player
      }
    }

    recalculateArmies(nextPlayerNumber);
    GameMap.setPlayers(players);
  }
}
