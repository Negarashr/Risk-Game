package risk.game.grp.twenty.play.phase.impl.fortification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.SkipPhase;
import risk.game.grp.twenty.play.phase.impl.play.phase.FortificationPhase;

/**
 * this class represents logic for <em>Cheater Fortification</em> phase, in game.
 *
 * @author ali
 */

@Component
public class FortificationPhaseCheaterImpl implements FortificationPhase, Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(FortificationPhaseCheaterImpl.class);
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
    LOGGER.info("fortification Cheater started");

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Map<Integer, Player> players = GameMap.getPlayers();

    Player cheaterPlayer = GameMap.getPlayers().get(playerTurnNumber);
    List<Country> cheaterCountries = cheaterPlayer.getCountries();

    for (Country country : cheaterCountries) {
      List<String> neighbours = country.getLinkedCountries();
      for (String neighbour : neighbours) {
        boolean isOwner = cheaterCountries.stream()
            .anyMatch(country1 -> StringUtils.equalsIgnoreCase(neighbour, country1.getName()));
        if (!isOwner) {
          int previousArmies = country.getArmies();
          country.setArmies(previousArmies * 2);
          break;
        }
      }
    }

    updatePlayersStatus(players, playerTurnNumber);

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
