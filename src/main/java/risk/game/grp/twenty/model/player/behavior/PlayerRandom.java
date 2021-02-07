package risk.game.grp.twenty.model.player.behavior;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.attack.AttackPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.fortification.FortificationPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.reinforcement.ReinforcementPhaseImpl;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * This class is the Model of <em>Player Random</em> Object in Game.
 *
 * @author Team 20
 */
public class PlayerRandom extends Player {

  private final static Logger LOGGER = LoggerFactory.getLogger(PlayerRandom.class);

  /**
   * Default constructor
   *
   * @param playerName name of the player
   */
  public PlayerRandom(String playerName) {
    super("Random-" + playerName);
    this.reinforcementPhase = new ReinforcementPhaseImpl();
    this.attackPhase = new AttackPhaseImpl();
    this.fortificationPhase = new FortificationPhaseImpl();
    this.playerType = PlayerType.Random;
  }

  /**
   * Method to move armies to the country
   *
   * @param playerNumber number of the player
   * @param countryName name of the country
   * @param numberOfArmies number of armies to move
   */
  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException {
    //calculation
    playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);

    try {
      int randomCountry = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
      super.positioningArmies(playerNumber, countries.get(randomCountry).getName(), "" + army);
    } catch (Exception ge) {
      LOGGER.error("Position Random failed:: {}", ge.getMessage());
    }
  }

  /**
   * Method to attack country
   *
   * @param playerNumber number of the player
   * @param attackingCountryName name of the attacking country
   * @param defendingCountryName name of the defending country
   * @param numberOfArmies number of armies to move
   * @return result of the attack
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    //calculation
    int attackNumber = InternalTools.getRandomNumberInRange(1, countries.size());
    for (int i = 0; i < attackNumber; i++) {
      int randomCountrySource = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
      String randomCountryName = "";
      int maxArmies = countries.get(randomCountrySource).getArmies() - 1;
      if (maxArmies > 0) {
        List<String> linkedCountries = countries.get(randomCountrySource).getLinkedCountries();
        List<String> linkedEnemyCountries = new ArrayList<String>();
        for (String s : linkedCountries) {
          if (GameMap.getPlayer(GameMap.getCountries().get(s).getName()) != this) {
            linkedEnemyCountries.add(s);
          }
        }
        int maxLinkedCountry = linkedEnemyCountries.size();
        if (maxLinkedCountry > 0) {
          int randomCountryDestination = InternalTools
              .getRandomNumberInRange(0, maxLinkedCountry - 1);
          randomCountryName = linkedEnemyCountries.get(randomCountryDestination);
          int attackArmies = InternalTools.getRandomNumberInRange(1, maxArmies);
          try {
            super.attack(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this),
                countries.get(randomCountrySource).getName(),
                randomCountryName, "" + attackArmies);
          } catch (GameException ge) {
            LOGGER.error("Attack Random" + ge.getMessage());
          }
        }
      }
    }
    return null;
  }

  /**
   * Method to perform fortification of the country
   *
   * @param playerNumber number of the player
   * @param sourceCountry name of the source country
   * @param destinationCountry name of the destination country
   * @param numberOfArmies number of armies to move
   */
  public void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {
    //calculation

    int randomCountrySource = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
    int randomCountryDestination = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
    if (randomCountrySource != randomCountryDestination) {
      int maxArmies = countries.get(randomCountrySource).getArmies() - 1;
      if (maxArmies > 0) {
        int moveArmies = InternalTools.getRandomNumberInRange(1, maxArmies);
        try {
          super.fortification(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this),
              countries.get(randomCountrySource).getName(),
              countries.get(randomCountryDestination).getName(), "" + moveArmies);
        } catch (GameException ge) {
          LOGGER.error("fortification Random" + ge.getMessage());
        }

      }
    }
  }

}
