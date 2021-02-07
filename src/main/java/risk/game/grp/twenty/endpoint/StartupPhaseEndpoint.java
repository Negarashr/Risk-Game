package risk.game.grp.twenty.endpoint;

import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_MAPS_BASE_PATH;
import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_MAP_PATH;
import static risk.game.grp.twenty.constant.GameConstant.SAVE_MAPS_BASE_PATH;
import static risk.game.grp.twenty.constant.GameConstant.VALID_MAP_EXTENSION;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.LoadMapResult;
import risk.game.grp.twenty.play.phase.impl.StartupPhase;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * <p>This class is responsible for getting the request from user and call <em>StartupPhase</em>
 * methods to start the game, in this class based on the players has been requested, it calls method
 * from StartupPhase to read a Map, verify map, assign players to countries, and determine the
 * players turn </p>
 *
 * <p>then feed the objects in GameMap class and return the players information for UI
 * consumption</p>
 *
 * @author Ali
 * @see GameMap
 * @see StartupPhase
 */

@RestController
public class StartupPhaseEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(StartupPhaseEndpoint.class);

  @Autowired
  private StartupPhase startupPhase;

  /**
   * this method load the map based on player request, at any of time one of <em>mapContent</em> or
   * <em>filePath</em> has value, and request handel based on this, and if the map is new or
   * modified and it is valid will be saved on disk and its path will be send to user.
   *
   * @param mapContent new or modified map created by user
   * @param filePath path of one the map files available in system and showed in UI to load and show
   * to user
   * @return <em>HttpStatus.OK</em> and and instance of LoadMapResult in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/loadMap")
  public ResponseEntity loadMap(@RequestParam String mapContent, @RequestParam String filePath) {

    LOGGER.info("loadMap started for Content: {}, filePath: {}", mapContent, filePath);
    try {
      LoadMapResult result = new LoadMapResult();
      // this part should be changed if multiple game want to take place at same time
      if (GameMap.getCountries().size() > 0 || GameMap.getContinents().size() > 0) {
        // in case we want to preform saving mechanism below code should be used.
        // return new ResponseEntity(GameMap.getPlayers(), HttpStatus.OK);
        GameMap.resetGameMap();
      }

      String contentToLoad;
      if (StringUtils.isNotBlank(filePath)) {
        if (StringUtils.equalsIgnoreCase(filePath, "DEFAULT_MAP_PATH")) {
          filePath = DEFAULT_MAP_PATH;
        }
        contentToLoad = InternalTools.readFile(filePath);
      } else {
        contentToLoad = mapContent;
      }
      startupPhase.extractDataFromProvidedMapData(contentToLoad);

      String mapToUsePath = null;
      if (StringUtils.isNotBlank(mapContent) && !StringUtils
          .equalsIgnoreCase(filePath, DEFAULT_MAP_PATH)) {
        mapToUsePath = InternalTools.writeFile(mapContent,
            SAVE_MAPS_BASE_PATH + InternalTools.getDateFormatForFileName()
                + VALID_MAP_EXTENSION);
      }

      LOGGER.info("loadMap finished file is: ", mapToUsePath);
      result.setFileName(mapToUsePath);
      result.setRequestedMapContent(contentToLoad);

      return new ResponseEntity(result, HttpStatus.OK);
    } catch (GameException e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * This method get all the maps already saved in system and show them as file path in UI
   *
   * @return <em>HttpStatus.OK</em> and and instance of LoadMapResult in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/getExistingMaps")
  public ResponseEntity getExistingMaps() {

    LOGGER.info("getExistingMaps");
    try {

      LoadMapResult result = new LoadMapResult();
      result.setExistingMaps(startupPhase.getExistingMaps(DEFAULT_MAPS_BASE_PATH));

      LOGGER.info("getExistingMaps finished");
      return new ResponseEntity(result, HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }

  /**
   * this method is responsible to assign players to countries, give them order randomly, calculate
   * initial number of armies based on players, and assign minimum number of armies to each players'
   * country
   *
   * @param playersNumber number of players to start the game
   * @return <em>HttpStatus.OK</em> and list of Players in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/assignCountries")
  public ResponseEntity assignCountries(@RequestParam String playersNumber) {

    LOGGER.info("assignCountries started for {} players", playersNumber);
    try {

      startupPhase.assignCountriesToPlayers(Integer.parseInt(playersNumber));
      startupPhase.orderPlayersRandomly();
      startupPhase.calculateInitialArmies();
      startupPhase.assignMinimumNumberOfArmiesToCountries();
      startupPhase.registerPhaseViewObservers();

      return new ResponseEntity(GameMap.getPlayers(), HttpStatus.OK);
    } catch (GameException e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }
}
