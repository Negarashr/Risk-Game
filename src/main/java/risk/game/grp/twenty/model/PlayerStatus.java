package risk.game.grp.twenty.model;

import java.io.Serializable;

/**
 * This class is the Model of <em>PlayerStatus</em> Object in Game.
 *
 * @author Team 20
 */
public enum PlayerStatus implements Serializable {

  Waiting, Placing, Reinforcement, Attack, Fortification, Eliminated, Winner
}
