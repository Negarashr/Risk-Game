package risk.game.grp.twenty.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import risk.game.grp.twenty.observers.ViewPhase;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Player phase view and Domination
 * view</em> phase, and assign them to the correspondence method to handel it in model, and provide
 * appropriate response for UI section.
 * </p>
 *
 * @author Team 20
 */

@RestController
public class ViewPhaseEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(ViewPhaseEndpoint.class);

  /**
   * this method is responsible to get current player logs
   *
   * @param callTime shows is it initial call or already called
   * @return <em>HttpStatus.OK</em> and current player status in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/viewPhasePlayer")
  public ResponseEntity viewPhasePlayer(@RequestParam String callTime) {

    try {

      if (StringUtils.equalsIgnoreCase(callTime, "initCall")) {
        LOGGER.info("viewPhasePlayer Initial call.");
        ViewPhase.resetViews();
      }
      return new ResponseEntity(ViewPhase.getCurrentPlayer(), HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }

  /**
   * get request and send back the domination view result
   *
   * @param callTime shows is it initial call or already called
   * @return <em>HttpStatus.OK</em> and Domination view status in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/viewPhaseDomination")
  public ResponseEntity viewPhaseDomination(@RequestParam String callTime) {

    try {

      if (StringUtils.equalsIgnoreCase(callTime, "initCall")) {
        LOGGER.info("viewPhaseDomination Initial call.");
        ViewPhase.resetViews();
      }

      return new ResponseEntity(ViewPhase.getCurrentDominationView(), HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }

}
