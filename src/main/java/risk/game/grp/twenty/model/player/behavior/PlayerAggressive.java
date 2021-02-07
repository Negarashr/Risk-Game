package risk.game.grp.twenty.model.player.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.attack.AttackPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.fortification.FortificationPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.reinforcement.ReinforcementPhaseImpl;

/**
 * This class is the Model of <em>Player Aggressive</em> Object in Game.
 *
 * @author Team 20
 */
public class PlayerAggressive extends Player {


  private final static Logger LOGGER = LoggerFactory.getLogger(PlayerAggressive.class);

  public PlayerAggressive(String playerName) {
    super("Aggressive-" + playerName);
    this.reinforcementPhase = new ReinforcementPhaseImpl();
    this.attackPhase = new AttackPhaseImpl();
    this.fortificationPhase = new FortificationPhaseImpl();
    this.playerType = PlayerType.Aggressive;
  }

  /**
   * reinforces its strongest country
   *
   * {@inheritDoc}
   */
  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException {
    //calculation
    playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);

    List<Country> countriesHasLinkedEnemy = getCountriesHasLinkedEnemy();
    Collections.sort(countriesHasLinkedEnemy, (c1, c2) -> c2.getArmies() - c1.getArmies());

    if (countriesHasLinkedEnemy.size() > 0 && countriesHasLinkedEnemy.get(0) != null) {
      super.positioningArmies(playerNumber, countriesHasLinkedEnemy.get(0).getName(), "" + army);
    } else {
      this.setPlayerLog(String.format("%s has NO more countries!", this.getPlayerName()));
      LOGGER
          .error("positioningArmies Aggressive:: {} has NO more countries!", this.getPlayerName());
    }

  }

  /**
   * always attack with the strongest until it cannot attack anymore
   *
   * {@inheritDoc}
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    //calculation
    playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);

    List<Country> countriesHasLinkedEnemy = getCountriesHasLinkedEnemy();
    Collections.sort(countriesHasLinkedEnemy, (c1, c2) -> c2.getArmies() - c1.getArmies());

    String defendingCountry = "";
    Iterator<Country> countryIterator = countriesHasLinkedEnemy.iterator();

    while (countryIterator.hasNext()) {
      Country attackingCountry = countryIterator.next();

      for (String s : attackingCountry.getLinkedCountries()) {
        if (GameMap.getPlayer(GameMap.getCountries().get(s).getName()) != this) {
          defendingCountry = s;
          break;
        }
      }

      try {
        return super.attackAllOut(playerNumber, attackingCountry.getName(), defendingCountry);
      } catch (Exception e) {
        LOGGER.error("Attack Aggressive:: {}", e.getMessage());
      }
    }

    LOGGER.info("Attack Aggressive All Failed! {}", this.getPlayerName());
    return null;
  }

  /**
   * fortifies in order to maximize aggregation of forces in one country
   *
   * {@inheritDoc}
   */
  public void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {

    playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);

    List<Country> countriesHasLinkedEnemy = getCountriesHasLinkedEnemy();
    Collections.sort(countriesHasLinkedEnemy, (c1, c2) -> c2.getArmies() - c1.getArmies());
    Collections.sort(countries, (c1, c2) -> c2.getArmies() - c1.getArmies());
    boolean successfulFortification = false;

    Iterator<Country> countryIterator = countries.iterator();
    while (!successfulFortification && countryIterator.hasNext()) {
      Country fromCountry = countryIterator.next();

      Iterator<Country> countriesHasLinkedEnemyIterator = countriesHasLinkedEnemy.iterator();
      while (!successfulFortification && countriesHasLinkedEnemyIterator.hasNext()) {
        Country toCountry = countriesHasLinkedEnemyIterator.next();

        try {
          int fromCountryArmies = fromCountry.getArmies() - 1;
          super.fortification(playerNumber, fromCountry.getName(), toCountry.getName(),
              "" + fromCountryArmies);
          successfulFortification = true;

        } catch (Exception e) {
          LOGGER.error("fortification Aggressive failed:: {}", e.getMessage());
        }
      }
    }

    if (!successfulFortification) {
      LOGGER.error("fortification Aggressive All failed:: {}", this.getPlayerName());
    }
  }

  /**
   * calculate countries that has neighbours and not belong to current player
   *
   * @return list of countries that has neighbours and not belong to current player
   */
  private List<Country> getCountriesHasLinkedEnemy() {
    List<Country> countriesHasLinkedEnemy = new ArrayList<>();
    for (Country country : countries) {
      for (String s : country.getLinkedCountries()) {
        if (!countriesHasLinkedEnemy.contains(country)
            && GameMap.getPlayer(GameMap.getCountries().get(s).getName()) != this) {
          countriesHasLinkedEnemy.add(country);
        }
      }
    }
    return countriesHasLinkedEnemy;
  }

}
