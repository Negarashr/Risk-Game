package risk.game.grp.twenty.play.phase.impl.play.phase;

import org.springframework.stereotype.Component;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;

@Component
public interface AttackPhase {

  AttackResult attack(String playerNumber, String attackingCountryName,
      String defendingCountryName,
      String numberOfArmies) throws GameException;


  AttackResult attackAllOut(String playerNumber, String attackingCountryName,
      String defendingCountryName) throws GameException;

}
