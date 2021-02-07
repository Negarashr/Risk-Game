package risk.game.grp.twenty.endpoint;

import java.util.ArrayList;
import java.util.List;
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
import risk.game.grp.twenty.model.GameWinnerPair;
import risk.game.grp.twenty.model.Tournament;
import risk.game.grp.twenty.model.TournamentAggRes;
import risk.game.grp.twenty.model.TournamentResult;
import risk.game.grp.twenty.play.phase.impl.TournamentPhase;

/**
 * <p>
 * This class is responsible for getting the requests related to <em>Tournament and Single Mode</em>
 * phase, and assign them to the correspondence method to handel it in model, and provide
 * appropriate response for UI section.
 * </p>
 *
 * @author Team 20
 */

@RestController
public class TournamentEndpoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(TournamentEndpoint.class);

  @Autowired
  private TournamentPhase tournamentPhase;

  /**
   * get the request for starting a tournament phase
   *
   * @param gameMap1 first map that user wants to play games on
   * @param gameMap2 second map that user wants to play games on
   * @param gameMap3 third map that user wants to play games on
   * @param gameMap4 fourth map that user wants to play games on
   * @param gameMap5 fifth map that user wants to play games on
   * @param playerType1 first player type that user wants to play games with
   * @param playerType2 second player type that user wants to play games with
   * @param playerType3 third  player type that user wants to play games with
   * @param playerType4 fourth player type that user wants to play games with
   * @param numberOfGames number of games user wants to run the tournament
   * @param numberOfTurns number of turn user wants to execute tournament
   * @return <em>HttpStatus.OK</em> the grid report about each execution result,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/tournament")
  public ResponseEntity startTournament(@RequestParam String gameMap1,
      @RequestParam String gameMap2, @RequestParam String gameMap3, @RequestParam String gameMap4,
      @RequestParam String gameMap5,
      @RequestParam String playerType1, @RequestParam String playerType2,
      @RequestParam String playerType3, @RequestParam String playerType4,
      @RequestParam String numberOfGames, @RequestParam String numberOfTurns) {

    LOGGER.info("tournament started.");
    try {
      Tournament tournament = new Tournament();
      tournament.getTournamentMaps().add(gameMap1);
      tournament.getTournamentMaps().add(gameMap2);
      tournament.getTournamentMaps().add(gameMap3);
      tournament.getTournamentMaps().add(gameMap4);
      tournament.getTournamentMaps().add(gameMap5);
      tournament.getPlayerTypes().add(playerType1);
      tournament.getPlayerTypes().add(playerType2);
      tournament.getPlayerTypes().add(playerType3);
      tournament.getPlayerTypes().add(playerType4);
      tournament.setNumberOfGames(Integer.parseInt(numberOfGames));
      tournament.setNumberOfTurns(Integer.parseInt(numberOfTurns));

      List<TournamentResult> tournamentResults = tournamentPhase.start(tournament);

      return new ResponseEntity(generateGridViewTournamentReport(tournamentResults), HttpStatus.OK);
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
   * generates the grid report view for showing the tournament result
   *
   * @param tournamentResults list of tournaments
   * @return the grid view of tournament phase
   */
  private List<TournamentAggRes> generateGridViewTournamentReport(
      List<TournamentResult> tournamentResults) {
    List<TournamentAggRes> tournamentResultList = new ArrayList<>();
    if (tournamentResults != null) {
      for (TournamentResult result : tournamentResults) {
        TournamentAggRes aggRes = tournamentResultList.stream().filter(
            tournamentAggRes -> result.getMapName().equalsIgnoreCase(tournamentAggRes.getMapName()))
            .findAny().orElse(null);
        if (aggRes == null) {
          int index = tournamentResultList.size();
          tournamentResultList.add(new TournamentAggRes(result.getMapName()));
          tournamentResultList.get(index).getGameWinnerPairs()
              .add(new GameWinnerPair(convertGameNumber(result.getGameNumber()),
                  result.getWinner()));
        } else {
          aggRes.getGameWinnerPairs()
              .add(new GameWinnerPair(convertGameNumber(result.getGameNumber()),
                  result.getWinner()));
        }

      }
    }
    return tournamentResultList;
  }

  /**
   * convert game number add 1 unit to show start from 1
   *
   * @param gameNumber : get the number of game
   * @return the format needed for grid report
   */
  private String convertGameNumber(String gameNumber) {

    return " Game " + (Integer.parseInt(gameNumber) + 1);
  }

  /**
   * get the request for starting a tournament phase
   *
   * @param gameMap1 the map that user wants to play games on
   * @param playerType1 first player type that user wants to play games with
   * @param playerType2 second player type that user wants to play games with
   * @param playerType3 third  player type that user wants to play games with
   * @param playerType4 fourth player type that user wants to play games with
   * @return <em>HttpStatus.OK</em> players to show in UI,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/singleMode")
  public ResponseEntity startSingleMode(@RequestParam String gameMap1,
      @RequestParam String playerType1, @RequestParam String playerType2,
      @RequestParam String playerType3, @RequestParam String playerType4) {

    LOGGER.info("singleMode started.");
    try {
      Tournament tournament = new Tournament();
      tournament.getTournamentMaps().add(gameMap1);
      tournament.getPlayerTypes().add(playerType1);
      tournament.getPlayerTypes().add(playerType2);
      tournament.getPlayerTypes().add(playerType3);
      tournament.getPlayerTypes().add(playerType4);

      tournamentPhase.startSingleMode(tournament);

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

  /**
   * automatically do the phases for a single player
   *
   * @param playerNumber player number which needs to do auto move
   * @return <em>HttpStatus.OK</em> players to show in UI,
   * <em>HttpStatus.BAD_REQUEST</em> if any rules of game being violated, or any exception happens
   * during the process with error message that guide the user and handled in UI
   */
  @RequestMapping("/autoMove")
  public ResponseEntity autoMove(@RequestParam String playerNumber) {

    LOGGER.info("autoMove started : {}", playerNumber);
    try {

      tournamentPhase.autoMove(playerNumber);

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
