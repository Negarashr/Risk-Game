package risk.game.grp.twenty.play.phase.impl.attack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.SkipPhase;
import risk.game.grp.twenty.play.phase.impl.play.phase.AttackPhase;

/**
 * this class represents logic for <em>Cheater Attack</em> phase, in game.
 *
 */

@Component
public class AttackPhaseCheaterImpl implements AttackPhase, Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(AttackPhaseCheaterImpl.class);
  private static final long serialVersionUID = 1L;

  @Autowired
  SkipPhase skipPhase;

  /**
   * Method to perform attack
   *
   * @param playerNumber attacking player number
   * @param attackingCountryName attacking country name
   * @param defendingCountryName defending country name
   * @param numberOfArmies number of attacking armies
   * @return result of the attack
   * @throws GameException in case any game rules violated
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    LOGGER.info("attack Cheater started");

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Player cheaterPlayer = GameMap.getPlayers().get(playerTurnNumber);
    List<Country> cheaterCountries = cheaterPlayer.getCountries();
    List<String> cheaterConqueredCountries = new ArrayList<>();

    for (Country country : cheaterCountries) {
      List<String> neighbours = country.getLinkedCountries();
      for (String neighbour : neighbours) {
        boolean isOwner = cheaterCountries.stream()
            .anyMatch(country1 -> StringUtils.equalsIgnoreCase(neighbour, country1.getName()));
        if (!isOwner) {
          cheaterConqueredCountries.add(neighbour);
        }
      }
    }

    for (String conqueredCountryName : cheaterConqueredCountries) {
      Country conqueredCountry = GameMap.getCountries().get(conqueredCountryName);
      Player defender = GameMap.getPlayer(conqueredCountryName);
      defender.getCountries().remove(conqueredCountry);
      cheaterPlayer.getCountries().add(conqueredCountry);

      // Check if defending player has lost last country
      checkPlayerEliminated(cheaterPlayer, defender);

      //Check if cards should be traded in
      cheaterPlayer.tradeExtraCards();

    }

    return null;
  }

  public AttackResult attackAllOut(String playerNumber, String attackingCountryName,
      String defendingCountryName) throws GameException {
    LOGGER.info("attackAllOut started");
    return attack(playerNumber, attackingCountryName, defendingCountryName, "0");
  }

  /**
   * Method to check if defending player has no countries and move cards to the attacking player if
   * so
   *
   * @param attacker attacking player
   * @param defender defending player
   */
  private void checkPlayerEliminated(Player attacker, Player defender) {
    if (defender.getCountries().isEmpty()) {
      defender.setPlayerStatus(PlayerStatus.Eliminated);
      int cardsCount = defender.getCard();
      defender.setCard(0);
      attacker.setCard(attacker.getCard() + cardsCount);
    }
  }

}
