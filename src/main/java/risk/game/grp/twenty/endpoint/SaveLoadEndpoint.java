package risk.game.grp.twenty.endpoint;

import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_SAVED_GAMES_BASE_PATH;

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
import risk.game.grp.twenty.model.LoadMapResult;
import risk.game.grp.twenty.play.phase.impl.SaveLoadPhase;

/**
 * <p>This class is responsible for getting the request from user and call <em>SaveLoadPhase</em>
 * methods to save/load the game, in this class based on the information provided by user game loads
 * or saved.
 *
 * <p>then feed the objects in GameMap class and return the players information for UI
 * consumption</p>
 *
 * @author Ali
 * @see GameMap
 */

@RestController
public class SaveLoadEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(SaveLoadEndpoint.class);

  @Autowired
  private SaveLoadPhase saveLoadPhase;

  /**
   * this method save the GAME based on user request, Game saved on disk and data of saved file
   * shows to player.
   *
   * @return <em>HttpStatus.OK</em> and and instance of SaveGame in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/saveGame")
  public ResponseEntity saveGame() {

    LOGGER.info("saveGame Started.");
    try {

      String savedFile = saveLoadPhase.save();
      LoadMapResult result = new LoadMapResult();
      result.setFileName(savedFile);

      return new ResponseEntity(result, HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * this method returns list of all already saved games to player.
   *
   * @return <em>HttpStatus.OK</em> and and instance of LoadMapResult in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/getExistingSavedGames")
  public ResponseEntity getExistingSavedGames() {

    LOGGER.info("getExistingSavedGames");
    try {

      LoadMapResult result = new LoadMapResult();
      result.setExistingMaps(saveLoadPhase.getExistingSavedGames(DEFAULT_SAVED_GAMES_BASE_PATH));

      LOGGER.info("getExistingSavedGames finished");
      return new ResponseEntity(result, HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }

  }

  /**
   * this method loads the requested saved Game data to GameMap class
   *
   * @param gameToLoad the game that player choose to load to play
   * @return <em>HttpStatus.OK</em> and and instance of LoadMapResult in case of successful event,
   * <em>HttpStatus.BAD_REQUEST</em> if any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/loadGame")
  public ResponseEntity loadGame(@RequestParam String gameToLoad) {

    LOGGER.info("loadGame Started.");
    try {
      saveLoadPhase.load(gameToLoad);
      return new ResponseEntity(GameMap.getPlayers(), HttpStatus.OK);
    } catch (Exception e) {
      final String stackTrace = ExceptionUtils.getStackTrace(e);
      LOGGER.error(stackTrace);
      return new ResponseEntity(stackTrace.split("\n")[0], HttpStatus.BAD_REQUEST);
    }
  }

}
