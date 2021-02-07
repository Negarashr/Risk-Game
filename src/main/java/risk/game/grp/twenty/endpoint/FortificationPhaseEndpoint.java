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
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Fortification</em> phase, and
 * assign them to the correspondence method to handel it in model, and provide appropriate response
 * for UI section.
 * </p>
 *
 * @author ali
 */
@RestController
public class FortificationPhaseEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(FortificationPhaseEndpoint.class);

  /**
   * this method handel the fortification request from UI and return the JSON response back
   *
   * @param playerNumber Player Number equivalent of the <em>Player Turn</em> in the game
   * @param sourceCountry Source Country
   * @param destinationCountry Destination Country
   * @param numberOfArmies Number of Armies for Fortification
   * @return <em>HttpStatus.OK</em> and list of Players in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/fortification")
  public ResponseEntity fortification(@RequestParam String playerNumber,
      @RequestParam String sourceCountry, @RequestParam String destinationCountry,
      @RequestParam String numberOfArmies) {

    LOGGER.info(
        "fortification started for player: {}, sourceCountry: {}, destinationCountry: {}, numberOfArmies: {}",
        playerNumber, sourceCountry, destinationCountry, numberOfArmies);
    try {
      Player player = GameMap.getPlayers().get(Integer.parseInt(playerNumber));
      player
          .fortification(playerNumber, sourceCountry, destinationCountry, numberOfArmies);

      return new ResponseEntity(GameMap.getPlayers(), HttpStatus.OK);
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
