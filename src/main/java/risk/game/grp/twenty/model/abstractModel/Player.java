package risk.game.grp.twenty.model.abstractModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import org.apache.commons.lang3.StringUtils;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.play.phase.impl.play.phase.AttackPhase;
import risk.game.grp.twenty.play.phase.impl.play.phase.FortificationPhase;
import risk.game.grp.twenty.play.phase.impl.play.phase.ReinforcementPhase;

/**
 * This class is the Model of <em>Player</em> Object in Game.
 *
 * @author Team 20
 */
public abstract class Player extends Observable implements Serializable {

  protected static final long serialVersionUID = 1L;

  protected static int playersCount = 0;
  static protected int timesAlreadyExchangedCardsForArmy = 0;
  protected boolean firstVictory;
  protected ReinforcementPhase reinforcementPhase;
  protected AttackPhase attackPhase;
  protected FortificationPhase fortificationPhase;
  protected String playerName;
  protected int playerId;
  protected int army = 0;
  protected int card = 0;
  protected List<Country> countries = new ArrayList<>();
  protected PlayerStatus playerStatus = PlayerStatus.Waiting;
  protected StringBuilder playerLog = new StringBuilder();
  protected PlayerType playerType;

  public Player(String playerName) {
    this.playerName = playerName;
    this.playerId = ++playersCount;
  }

  public static int getPlayersCount() {
    return playersCount;
  }

  public static void setPlayersCount(int playersCount) {
    Player.playersCount = playersCount;
  }

  public static void resetPlayerCounter() {
    playersCount = 0;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public int getArmy() {
    return army;
  }

  public void setArmy(int army) {
    this.army = army;
  }

  public int getCard() {
    return card;
  }

  public void setCard(int card) {
    this.card = card;
  }

  public int getTimesAlreadyExchangedCardsForArmy() {
    return timesAlreadyExchangedCardsForArmy;
  }

  public void setTimesAlreadyExchangedCardsForArmy(int timesAlreadyExchangedCardsForArmy) {
    this.timesAlreadyExchangedCardsForArmy = timesAlreadyExchangedCardsForArmy;
  }

  public List<Country> getCountries() {
    return countries;
  }

  public void setCountries(List<Country> countries) {
    this.countries = countries;
  }

  public PlayerStatus getPlayerStatus() {
    return playerStatus;
  }

  public void setPlayerStatus(PlayerStatus playerStatus) {
    this.playerStatus = playerStatus;
    this.firstVictory = true;

    // reset the log for Phase view Observers
    this.playerLog = new StringBuilder();
    notifyPhaseViewObservers();
  }

  public StringBuilder getPlayerLog() {
    return playerLog;
  }

  public void setPlayerLog(String newLog) {
    this.playerLog.append(newLog);
    this.playerLog.append("\n");
    notifyPhaseViewObservers();
  }

  public PlayerType getPlayerType() {
    return playerType;
  }

  public void setPlayerType(PlayerType playerType) {
    this.playerType = playerType;
  }

  public void setReinforcementPhase(
      ReinforcementPhase reinforcementPhase) {
    this.reinforcementPhase = reinforcementPhase;
  }

  public void setAttackPhase(AttackPhase attackPhase) {
    this.attackPhase = attackPhase;
  }
  
  public void setFortificationPhase(
      FortificationPhase fortificationPhase) {
    this.fortificationPhase = fortificationPhase;
  }

  /**
   * Notify the observers register for player
   */
  private void notifyPhaseViewObservers() {
    setChanged();
    notifyObservers(this);
  }

  /**
   * method for exchaing the cards player has
   *
   * @param playerNumber number of player
   * @throws GameException in case any game rules violated
   */
  public void exchangeCard(String playerNumber) throws GameException {
    Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
    reinforcementPhase.exchangeCard(playerNumber);
    player.setPlayerLog(String.format("exchangeCard for: %s", playerNumber));
  }

  /**
   * this method is for doing the positioning armies
   *
   * @param playerNumber number of player
   * @param countryName destination country name
   * @param numberOfArmies number of armies to position in country
   * @throws GameException in case any game rules violated
   */
  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException {
    Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
    reinforcementPhase.positioningArmies(playerNumber, countryName, numberOfArmies);
    player.setPlayerLog(String
        .format("%s Positioned %s armies in %s", player.getPlayerName(), numberOfArmies,
            countryName));
  }

  /**
   * Method to perform attack
   *
   * @param playerNumber number of player
   * @param attackingCountryName name of the attacking country
   * @param defendingCountryName name of the defending country
   * @param numberOfArmies number of attacking armies
   * @return result of the attack
   * @throws GameException in case any game rules violated
   */
  public AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException {
    Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
    player.setPlayerLog(String
        .format("%s attacks with %s armies from %s to %s", player.getPlayerName(),
            numberOfArmies, attackingCountryName, defendingCountryName));

    AttackResult result = attackPhase
        .attack(playerNumber, attackingCountryName, defendingCountryName, numberOfArmies);
    evaluateAttackForLog(player, result);
    return result;
  }

  /**
   * Method to perform attack all-out
   *
   * @param playerNumber number of player
   * @param attackingCountryName name of the attacking country
   * @param defendingCountryName name of the defending country
   * @return result of the attack
   * @throws GameException in case any game rules violated
   */
  public AttackResult attackAllOut(String playerNumber, String attackingCountryName,
      String defendingCountryName) throws GameException {
    Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
    player.setPlayerLog(String
        .format("%s attacks all-out from %s to %s", player.getPlayerName(),
            attackingCountryName, defendingCountryName));

    AttackResult result = attackPhase
        .attackAllOut(playerNumber, attackingCountryName, defendingCountryName);
    evaluateAttackForLog(player, result);
    return result;
  }

  /**
   * this method perform fortification
   *
   * @param playerNumber number of player
   * @param sourceCountry the source country for for fortification
   * @param destinationCountry the destination country for fortification
   * @param numberOfArmies number of armies for fortification
   * @throws GameException in case any game rules violated
   */
  public void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException {
    Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
    fortificationPhase
        .fortification(playerNumber, sourceCountry, destinationCountry, numberOfArmies);
    player.setPlayerLog(String
        .format("%s fortified with %s armies from %s to %s", player.getPlayerName(), numberOfArmies,
            sourceCountry, destinationCountry));
  }

  /**
   * log the attack result for attacking player
   *
   * @param player the player who is in attack
   * @param result attack result which based on that proper log generated
   */
  private void evaluateAttackForLog(Player player, AttackResult result) {
    if (StringUtils.isBlank(result.getConqueredCountry())) {
      player.setPlayerLog("Attack failed");
    } else {
      player.setPlayerLog(
          "Attack is successful, country " + result.getConqueredCountry() + " is conquered");
    }
  }

  /**
   * Method to add card in case of first victory
   */
  public void addCard() {
    if (firstVictory) {
      firstVictory = false;
      card++;
    }
  }

  /**
   * Method to automatically trade in cards
   */
  public void tradeExtraCards() {
    while (card > 4) {
      card -= 3;
      timesAlreadyExchangedCardsForArmy++;
      army += timesAlreadyExchangedCardsForArmy * 5;
    }
  }

  /**
   * Method to check if the player has won
   *
   * @return true if the player has won, false otherwise
   */
  public boolean checkWinner() {
    for (Player p : GameMap.getPlayers().values()) {
      if ((p != this) && (p.getPlayerStatus() != PlayerStatus.Eliminated)) {
        return false;
      }
    }
    setPlayerStatus(PlayerStatus.Winner);
    return true;
  }

  /**
   * Method to check if player has more than 1 army in any country (player can attack)
   *
   * @return true if player can attack, false otherwise
   */
  public boolean canAttack() {
    for (Country c : countries) {
      if (c.getArmies() > 1) {
        return true;
      }
    }
    return false;
  }

}
