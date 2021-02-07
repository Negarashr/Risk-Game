package risk.game.grp.twenty.model.player.behavior;

import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.play.phase.impl.attack.AttackPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.fortification.FortificationPhaseImpl;
import risk.game.grp.twenty.play.phase.impl.reinforcement.ReinforcementPhaseImpl;

/**
 * This class is the Model of <em>Player Human</em> Object in Game.
 *
 * @author Team 20
 */
public class PlayerRegular extends Player {

  public PlayerRegular(String playerName) {
    super(playerName);
    this.reinforcementPhase = new ReinforcementPhaseImpl();
    this.attackPhase = new AttackPhaseImpl();
    this.fortificationPhase = new FortificationPhaseImpl();
    this.playerType = PlayerType.Human;
  }
}
