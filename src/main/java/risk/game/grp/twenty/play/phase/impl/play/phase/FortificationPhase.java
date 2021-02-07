package risk.game.grp.twenty.play.phase.impl.play.phase;

import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;

/**
 * this class represents logic for <em>Fortification</em> phase, in game.
 *
 * @author ali
 */

@Component
public interface FortificationPhase {

  void fortification(String playerNumber, String sourceCountry, String destinationCountry,
      String numberOfArmies) throws GameException;
}
