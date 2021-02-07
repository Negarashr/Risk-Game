package risk.game.grp.twenty.game.exception;

/**
 * This class uses when an error happens in Game rules
 *
 * @author Team 20
 */
public class GameException extends Exception {

  private int errorId;
  private String errorMessage;


  public GameException(String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }
}
