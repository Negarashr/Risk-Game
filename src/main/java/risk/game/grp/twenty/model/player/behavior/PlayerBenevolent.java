package risk.game.grp.twenty.model.player.behavior;

import java.util.Collections;
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

/**
 * This class is the Model of <em>Player Benevolent</em> Object in Game.
 *
 * @author Team 20
 */
public class PlayerBenevolent extends Player {

  private final static Logger LOGGER = LoggerFactory.getLogger(PlayerBenevolent.class);

  /**
   * Default constructor
   *
   * @param playerName name of the player
   */
  public PlayerBenevolent(String playerName) {
    super("Benevolent-" + playerName);
    this.reinforcementPhase = new ReinforcementPhaseImpl();
    this.attackPhase = new AttackPhaseImpl();
    this.fortificationPhase = new FortificationPhaseImpl();
    this.playerType = PlayerType.Benevolent;
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

    while (army > 0) {
      Collections.sort(countries, (c1, c2) -> c1.getArmies() - c2.getArmies());
      if (countries.size() > 0 && countries.get(0) != null) {
        super.positioningArmies(playerNumber, countries.get(0).getName(), "1");
      } else {
        this.setPlayerLog(String.format("positioningArmies Benevolent:: %s has NO more countries!",
            this.getPlayerName()));
        LOGGER.error("positioningArmies Benevolent:: {} has NO more countries!",
            this.getPlayerName());
        break;
      }
    }
  }

  /**
   * Method to attack country
   *
   * @param playerNumber number of the player
   * @param attackingCountryName name of the attacking country
   * @param defendingCountryName name of the defending country
   * @param numberOfArmies number of armies to move
   * @return null since Benevolent player doesn't attack
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    //calculation

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
    playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);
    int strongest = countries.size() - 1;
    int weakest = 0;
    boolean successfulFortification = false;
    Collections.sort(countries, (c1, c2) -> c1.getArmies() - c2.getArmies());

    while (strongest > 0 && !successfulFortification) {

      if (countries.get(strongest) != null && countries.get(weakest) != null
          && (countries.get(strongest).getArmies() - 1) > 1) {
        try {
          super.fortification(playerNumber,
              countries.get(strongest).getName(), countries.get(weakest).getName(), "1");
          successfulFortification = true;
        } catch (GameException ge) {
          LOGGER.error("fortification Benevolent:: {}", ge.getMessage());
          if (strongest < ++weakest) {
            strongest--;
            weakest = 0;
          }
        }
      }
    }

    if (!successfulFortification) {
      LOGGER.error("fortification Benevolent All failed:: {}", this.getPlayerName());
    }
  }
}
