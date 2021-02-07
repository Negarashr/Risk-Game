package risk.game.grp.twenty.play.phase.impl.play.phase;

import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;

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
public interface ReinforcementPhase {

  public void exchangeCard(String playerNumber) throws GameException;

  public void positioningArmies(String playerNumber, String countryName, String numberOfArmies)
      throws GameException;

}
