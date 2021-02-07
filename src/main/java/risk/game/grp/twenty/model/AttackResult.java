package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.Map;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * Class to simulate result of the attack. Initialized with number of armies from attacking and
 * defending countries it creates 2 set of dice, calculate dead and survived armies.
 *
 * @author Team 20
 */
public class AttackResult implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<Integer, Player> Players = GameMap.getPlayers();
  private Dice attackerDice;
  private Dice defenderDice;
  private int initialAttackerArmy = 0;
  private int deadAttackerArmy = 0;
  private int survivedAttackerArmy = 0;
  private int initialDefenderArmy = 0;
  private int deadDefenderArmy = 0;
  private int survivedDefenderArmy = 0;
  private String attackingCountry;
  private String conqueredCountry;

  /**
   * Default constructor
   *
   * @param attackingArmy number of armies in attacking country
   * @param defendingArmy number of armies in defending country
   */
  public AttackResult(int attackingArmy, int defendingArmy) {
    initialAttackerArmy = attackingArmy;
    initialDefenderArmy = defendingArmy;
    int attackingDiceCount = (attackingArmy > 3) ? 3 : attackingArmy;
    int defendingDiceCount = (defendingArmy > 2) ? 2 : defendingArmy;
    attackerDice = new Dice(attackingDiceCount);
    defenderDice = new Dice(defendingDiceCount);
    if ((attackingDiceCount > 0) && (defendingDiceCount > 0)) {
      if (attackerDice.getResults().get(0) > defenderDice.getResults().get(0)) {
        deadDefenderArmy++;
      } else {
        deadAttackerArmy++;
      }
      if ((defendingDiceCount == 2) && (attackingDiceCount > 1)) {
        if (attackerDice.getResults().get(1) > defenderDice.getResults().get(1)) {
          deadDefenderArmy++;
        } else {
          deadAttackerArmy++;
        }
      }
    }
    survivedAttackerArmy = initialAttackerArmy - deadAttackerArmy;
    survivedDefenderArmy = initialDefenderArmy - deadDefenderArmy;
  }

  /**
   * Method to get players
   *
   * @return hash map of players
   */
  public Map<Integer, Player> getPlayers() {
    return Players;
  }

  /**
   * Method to set players
   *
   * @param players hash map of players to set
   */
  public void setPlayers(Map<Integer, Player> players) {
    Players = players;
  }

  /**
   * Method to get attacker dice
   *
   * @return attacker dice
   */
  public Dice getAttackerDice() {
    return attackerDice;
  }

  /**
   * Method to get defender dice
   *
   * @return defender dice
   */
  public Dice getDefenderDice() {
    return defenderDice;
  }

  /**
   * Method to get initial number of armies in attacking country
   *
   * @return initial number of armies in attacking country
   */
  public int getInitialAttackerArmy() {
    return initialAttackerArmy;
  }

  /**
   * Method to get number of dead armies in attacking country
   *
   * @return number of dead armies in attacking country
   */
  public int getDeadAttackerArmy() {
    return deadAttackerArmy;
  }

  /**
   * Method to get number of survived armies in attacking country
   *
   * @return number of survived armies in attacking country
   */
  public int getSurvivedAttackerArmy() {
    return survivedAttackerArmy;
  }

  /**
   * Method to get initial number of armies in defending country
   *
   * @return initial number of armies in defending country
   */
  public int getInitialDefenderArmy() {
    return initialDefenderArmy;
  }

  /**
   * Method to get number of dead armies in defending country
   *
   * @return number of dead armies in defending country
   */
  public int getDeadDefenderArmy() {
    return deadDefenderArmy;
  }

  /**
   * Method to get number of survived armies in defending country
   *
   * @return number of survived armies in defending country
   */
  public int getSurvivedDefenderArmy() {
    return survivedDefenderArmy;
  }

  public String getConqueredCountry() {
    return conqueredCountry;
  }

  public void setConqueredCountry(String conqueredCountry) {
    this.conqueredCountry = conqueredCountry;
  }

  public String getAttackingCountry() {
    return attackingCountry;
  }

  public void setAttackingCountry(String attackingCountry) {
    this.attackingCountry = attackingCountry;
  }
}
