package risk.game.grp.twenty.play.phase.impl.reinforcement;

import static risk.game.grp.twenty.constant.GameConstant.NUMBER_OF_CARD_TO_EXCHANGE;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.play.phase.ReinforcementPhase;

/**
 * <p>This class is responsible for giving the armies to player depends on the
 * his own counties. Whenever the player owns all the countries of an entire continent, the player
 * is given an amount of armies depend on  the continent’s control value. At the end, if the player
 * owns three cards, the exchange has been done for armies. The number of armies a player will get
 * for cards is first 5, then increases by 5 every time any player does.</p>
 *
 * @author Konstantin
 * @author Farzaneh
 */

@Component
public class ReinforcementPhaseImpl implements ReinforcementPhase, Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(ReinforcementPhaseImpl.class);
  private static final long serialVersionUID = 1L;

  /**
   * Method exchangeCard until the card number is not more than 2 set card for the player, also add
   * armies for that
   *
   * @param playerNumber Player Number equivalent of the <em>Player Turn</em> in the game
   * @throws GameException if there is not any card
   */

  public void exchangeCard(String playerNumber) throws GameException {
    LOGGER.info("exchangeCard started");
    Map<Integer, Player> players = GameMap.getPlayers();

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Player player = players.get(playerTurnNumber);

    if (player == null) {
      throw new GameException("Player '" + playerTurnNumber + "' Not found!");
    }

    if (player.getCard() < NUMBER_OF_CARD_TO_EXCHANGE) {
      throw new GameException(
          " Not enough cards for '" + player.getPlayerName());
    }

    if (player.getPlayerStatus() != PlayerStatus.Reinforcement) {
      throw new GameException(
          "Player: " + player.getPlayerName() + " is in " + player.getPlayerStatus()
              + ", player should be in reinforcement phase.");
    }
    // Logic for exchangeCard
    player.setCard(player.getCard() - NUMBER_OF_CARD_TO_EXCHANGE);
    player
        .setTimesAlreadyExchangedCardsForArmy(player.getTimesAlreadyExchangedCardsForArmy() + 1);
    player.setArmy(player.getArmy() + player.getTimesAlreadyExchangedCardsForArmy() * 5);
  }

  /**
   * Method positioningArmies for adding armies for destination country and remove from player   *
   *
   * @param playerNumber Player Number equivalent of the <em>Player Turn</em> in the game
   * @param countryName country Name equivalent of the <em>countryName</em> in the game
   * @param numberOfArmies number of armies equivalent of the <em>NumberOfArmies</em> in the game
   * @throws GameException if the status of player is not reinforcement And placing
   */

  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException {
    LOGGER.info("positioningArmies started for player: {}, countryName: {}, numberOfArmies: {}",
        playerNumber, countryName, numberOfArmies);

    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    final int playerTurnNumber = Integer.parseInt(playerNumber);
    Player player = players.get(playerTurnNumber);
    Country destCountry = countries.get(countryName);
    final int army = Integer.parseInt(numberOfArmies);

    if (player == null) {
      throw new GameException("Player '" + playerTurnNumber + "' Not found!");
    }

    if (destCountry == null) {
      throw new GameException("Country '" + countryName + "' is not found");
    }

    if (army < 0) {
      throw new GameException("Negative number of armies: " + numberOfArmies);
    }

    if (player.getArmy() < army) {
      throw new GameException(
          " Not enough armies for '" + player.getPlayerName());
    }
    // if a player conquer a country status is in attack but player must reinforce new country
    if ((player.getPlayerStatus() != PlayerStatus.Reinforcement) && (player.getPlayerStatus()
        != PlayerStatus.Placing) && (player.getPlayerStatus() != PlayerStatus.Attack)) {

      throw new GameException(
          "Player: " + player.getPlayerName() + " is in " + player.getPlayerStatus()
              + ", player should be in reinforcement, placing or attack phase.");
    }
    verifyPlayerOwnsCountry(player, countryName);

    // Logic for positioningArmies
    destCountry.setArmies(destCountry.getArmies() + army);
    player.setArmy(player.getArmy() - army);
  }

  /**
   * Method verifyPlayerOwnsCountry perform verification whether player owns this country or not?
   *
   * @param player Player Number equivalent of the <em>Player Turn</em> in the game
   * @param countryName country Name equivalent of the <em>countryName</em> in the game
   * @throws GameException when player is not own the country
   */

  private void verifyPlayerOwnsCountry(Player player,
      String countryName) throws GameException {
    List<Country> playerCountries = player.getCountries();
    boolean ownsCountry = playerCountries.stream()
        .anyMatch(c -> c.getName().equalsIgnoreCase(countryName));
    if (!ownsCountry) {
      throw new GameException("Player does not own: " + countryName);
    }
  }

}
