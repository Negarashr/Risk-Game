package risk.game.grp.twenty.play.phase.impl.attack;

import java.io.Serializable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * this class represents logic for <em>Attack</em> phase, in game.
 */
@Component
public class AttackPhaseImpl implements AttackPhase, Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(AttackPhaseImpl.class);
  private static final long serialVersionUID = 1L;

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
    LOGGER.info(
        "attack started for player: {}, attackingCountry: {}, defendingCountry: {}, numberOfArmies: {}",
        playerNumber, attackingCountryName, defendingCountryName, numberOfArmies);

    Map<String, Country> countries = GameMap.getCountries();
    boolean allout = false;
    boolean attackMore = true;

    Country attackingCountry = countries.get(attackingCountryName);
    if (attackingCountry == null) {
      throw new GameException("Country " + attackingCountryName + " is not found");
    }

    Country defendingCountry = countries.get(defendingCountryName);
    if (defendingCountry == null) {
      throw new GameException("Country " + defendingCountryName + " is not found");
    }

    if (!attackingCountry.getLinkedCountries().contains(defendingCountryName)) {
      throw new GameException(
          "Attacking country " + attackingCountryName + " is not linked to defending country "
              + defendingCountryName);
    }

    Player attacker = GameMap.getPlayer(attackingCountryName);
    if (attacker == null) {
      throw new GameException("Owner of country " + attackingCountryName + " is not found");
    }

    if (attacker.getPlayerStatus() != PlayerStatus.Attack) {
      throw new GameException(
          "Player " + attacker.getPlayerName() + " is in " + attacker.getPlayerStatus()
              + " phase, player must be in attacking phase");
    }

    Player defender = GameMap.getPlayer(defendingCountryName);
    if (defender == null) {
      throw new GameException("Owner of country " + defendingCountryName + " is not found");
    }

    if (attacker == defender) {
      throw new GameException("Player cannot attack his own country");
    }

    if (attackingCountry.getArmies() == 1) {
      throw new GameException(
          "Only 1 army in the attacking country, not enough for attack");
    }

    int attackingArmy = Integer.parseInt(numberOfArmies);
    if (attackingArmy < 1) {
      allout = true;
      attackingArmy = attackingCountry.getArmies() - 1;
    }

    if (attackingArmy >= attackingCountry.getArmies()) {
      throw new GameException(
          "Not enough armies in the attacking country, should be less than " + attackingCountry
              .getArmies());
    }

    int defendingArmy = defendingCountry.getArmies();

    AttackResult attackResult = null;
    while (attackMore) {
      attackResult = new AttackResult(attackingArmy, defendingArmy);
      if (!allout) {
        attackMore = false;
      }

      if (attackResult.getSurvivedDefenderArmy() > 0) {
        // Failed attack
        attackingCountry
            .setArmies(attackingCountry.getArmies() - attackResult.getDeadAttackerArmy());
        defendingCountry.setArmies(attackResult.getSurvivedDefenderArmy());
        attackingArmy = attackingCountry.getArmies() - 1;
        defendingArmy = defendingCountry.getArmies();
        if (attackingArmy == 0) {
          attackMore = false;
        }
      } else {
        // Successful attack, country captured
        attackMore = false;
        int movingArmy = attackResult.getAttackerDice().getDiceCount();
        attackingCountry
            .setArmies(attackingCountry.getArmies() - movingArmy);
        defendingCountry.setArmies(movingArmy);
        defender.getCountries().remove(defendingCountry);
        attacker.getCountries().add(defendingCountry);
        attacker.addCard();
        attackResult.setAttackingCountry(attackingCountryName);
        attackResult.setConqueredCountry(defendingCountryName);
        // Check if defending player has lost last country
        checkPlayerEliminated(attacker, defender);
        //Check if cards should be traded in
        attacker.tradeExtraCards();
      }
      if (!attacker.checkWinner()) {
        if (!attacker.canAttack()) {
          SkipPhase skipPhase = new SkipPhase();
          skipPhase.skip(playerNumber);
        }
      }
      LOGGER.info("Result of attack: " + attackResult.getSurvivedAttackerArmy()
          + " survived attacking armies, " + attackResult.getSurvivedDefenderArmy()
          + " survived defending armies");
    }
    return attackResult;
  }

  public AttackResult attackAllOut(String playerNumber, String attackingCountryName,
      String defendingCountryName) throws GameException {
    LOGGER.info(
        "attack All out started for player: {}, attackingCountry: {}, defendingCountry: {}",
        playerNumber, attackingCountryName, defendingCountryName);
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
