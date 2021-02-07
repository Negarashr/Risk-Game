package risk.game.grp.twenty.model.player.behavior;

import org.apache.commons.lang3.StringUtils;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.attack.AttackPhaseCheaterImpl;
import risk.game.grp.twenty.play.phase.impl.fortification.FortificationPhaseCheaterImpl;
import risk.game.grp.twenty.play.phase.impl.reinforcement.ReinforcementPhaseCheaterImpl;

/**
 * This class is the Model of <em>Player Cheater</em> Object in Game.
 *
 * @author Team 20
 */
public class PlayerCheater extends Player {

  public PlayerCheater(String playerName) {
    super("Cheater-" + playerName);
    this.reinforcementPhase = new ReinforcementPhaseCheaterImpl();
    this.attackPhase = new AttackPhaseCheaterImpl();
    this.fortificationPhase = new FortificationPhaseCheaterImpl();
    this.playerType = PlayerType.Cheater;
  }

  /**
   * doubles the number of armies on all its countries
   *
   * {@inheritDoc}
   */
  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException {
    //calculation
    if (StringUtils.isBlank(playerNumber)) {
      playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);
    }

    reinforcementPhase.positioningArmies(playerNumber, countryName, numberOfArmies);
    this.setPlayerLog(String
        .format("%s Positioned %s armies in %s", this.getPlayerName(), numberOfArmies,
            countryName));
  }

  /**
   * automatically conquers all the neighbors of all its countries
   *
   * {@inheritDoc}
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    //calculation
    if (StringUtils.isBlank(playerNumber)) {
      playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);
    }

    return attackPhase.attack(playerNumber, attackingCountryName,
        defendingCountryName,
        numberOfArmies);

  }

  /**
   * automatically conquers all the neighbors of all its countries
   *
   * {@inheritDoc}
   */
  public AttackResult attackAllOut(String playerNumber, String attackingCountryName,
      String defendingCountryName) throws GameException {

    //calculation
    if (StringUtils.isBlank(playerNumber)) {
      playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);
    }

    AttackResult result = attackPhase
        .attackAllOut(playerNumber, attackingCountryName, defendingCountryName);
    return result;
  }

  /**
   * doubles the number of armies on its countries that have neighbors that belong to other players
   *
   * {@inheritDoc}
   */
  public void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {
    //calculation

    if (StringUtils.isBlank(playerNumber)) {
      playerNumber = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(this);
    }

    fortificationPhase
        .fortification(playerNumber, sourceCountry, destinationCountry, numberOfArmies);

  }

}
