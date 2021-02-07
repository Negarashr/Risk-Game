package risk.game.grp.twenty.endpoint;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.play.phase.impl.SkipPhase;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Skip</em> phase, and assign
 * them to the correspondence method to handel it in model, and provide appropriate response for UI
 * section.
 * </p>
 *
 * @author Team 20
 */

@RestController
public class SkipEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(FortificationPhaseEndpoint.class);

  @Autowired
  private SkipPhase SkipPhase;

  /**
   * @param playerNumber Player Number equivalent of the <em>Player Turn</em> in the game
   * @return <em>HttpStatus.OK</em> and list of Players in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/skip")
  public ResponseEntity skip(@RequestParam String playerNumber) {

    LOGGER.info("skip started for player: {} ", playerNumber);
    try {

      SkipPhase.skip(playerNumber);

      return new ResponseEntity(GameMap.getPlayers(), HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }
}
