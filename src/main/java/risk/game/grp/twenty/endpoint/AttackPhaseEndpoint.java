package risk.game.grp.twenty.endpoint;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Attack</em> phase, and assign
 * them to the correspondence method to handel it in model, and provide appropriate response for UI
 * section.
 * </p>
 *
 * @author Farzaneh
 */

@RestController
public class AttackPhaseEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(AttackPhaseEndpoint.class);

  /**
   * this method handel the attack request from UI and return the JSON response back
   *
   * @param playerNumber is equivalent of the <em>PlayerNumber</em> in the game
   * @param attackingCountry is equivalent of the <em>AttackingCountry</em> in the game
   * @param defendingCountry is equivalent of the <em>DefendingCountry</em> in the game
   * @param numberOfArmies is equivalent of the <em>numberOfArmies</em> in the game
   * @return <em>HttpStatus.OK</em> and list of Players in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/attack")
  public ResponseEntity attack(@RequestParam String playerNumber,
      @RequestParam String attackingCountry, @RequestParam String defendingCountry,
      @RequestParam String numberOfArmies) {

    LOGGER.info(
        "attack started for player: {}, attackingCountry: {}, defendingCountry: {}, numberOfArmies: {}",
        playerNumber, attackingCountry, defendingCountry, numberOfArmies);
    try {
      Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
      AttackResult attackResult = player
          .attack(playerNumber, attackingCountry, defendingCountry, numberOfArmies);

      return new ResponseEntity(attackResult, HttpStatus.OK);
    } catch (GameException e) {
      LOGGER.error(e.getMessage());
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }

  /**
   * this method handel the <em>Attack All Out</em> request from UI and return the JSON response
   * back
   *
   * @param playerNumber is equivalent of the <em>PlayerNumber</em> in the game
   * @param attackingCountry is equivalent of the <em>AttackingCountry</em> in the game
   * @param defendingCountry is equivalent of the <em>DefendingCountry</em> in the game
   * @return <em>HttpStatus.OK</em> and list of Players in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/attackAllOut")
  public ResponseEntity attackAllOut(@RequestParam String playerNumber,
      @RequestParam String attackingCountry, @RequestParam String defendingCountry) {

    LOGGER.info(
        "attack started for player: {}, attackingCountry: {}, defendingCountry: {}",
        playerNumber, attackingCountry, defendingCountry);
    try {
      Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
      AttackResult attackResult = player
          .attackAllOut(playerNumber, attackingCountry, defendingCountry);

      return new ResponseEntity(attackResult, HttpStatus.OK);
    } catch (GameException e) {
      LOGGER.error(e.getMessage());
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }
}
