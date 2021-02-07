package risk.game.grp.twenty.play.phase.impl;

import static risk.game.grp.twenty.constant.GameConstant.FIRST_PLAYER_IN_LIST;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.Tournament;
import risk.game.grp.twenty.model.TournamentResult;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * class for Tournament Phase and Single Mode phase.
 *
 * @author team 20
 */
@Service
public class TournamentPhase implements Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(SkipPhase.class);
  private static final long serialVersionUID = 1L;

  @Autowired
  private StartupPhase startupPhase;

  @Autowired
  private SkipPhase skipPhase;

  /**
   * starts a tournament
   *
   * @param tournament object that shows maps, players, number of games, number of turns
   * @return the tournament result
   * @throws Exception if file not found for maps, or arrays being null, or objects being null
   * @throws GameException if any of game rules being violated
   */
  public List<TournamentResult> start(Tournament tournament) throws Exception, GameException {
    List<TournamentResult> tournamentResults = new ArrayList<>();
    LOGGER.info("TournamentPhase started: {}", tournament.toString());

    removeNulls(tournament.getTournamentMaps());
    removeNulls(tournament.getPlayerTypes());
    validateNumbersRange(tournament);
    validateMapsDifferent(tournament.getTournamentMaps());

    for (String gameMapT : tournament.getTournamentMaps()) {

      for (int i = 0; i < tournament.getNumberOfGames(); i++) {

        GameMap.resetGameMap();
        startupTournament(tournament, gameMapT);
        firstRoundOfPositioningArmies();

        for (int j = 0; j < tournament.getNumberOfTurns(); j++) {

          for (Player player : GameMap.getPlayers().values()) {
            if (player.getPlayerStatus() != PlayerStatus.Eliminated) {
              String playerTurn = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(player);

              player.positioningArmies(null, null, null);
              skipPhase.skip(playerTurn);
              player.attack(null, null, null, null);

              if (player.getPlayerStatus() == PlayerStatus.Attack) {
                skipPhase.skip(playerTurn);
              }

              if (player.checkWinner()) {
                tournamentResults
                    .add(new TournamentResult(gameMapT, String.valueOf(i), player.getPlayerName()));
                j = tournament.getNumberOfTurns() + 1;
              } else {
                player.fortification(null, null, null, null);

                if (player.getPlayerStatus() == PlayerStatus.Fortification) {
                  skipPhase.skip(playerTurn);
                }
              }
            }
          }

          // Check if it is last round if yes announce Draw as winner
          if (j == tournament.getNumberOfTurns() - 1) {
            tournamentResults.add(new TournamentResult(gameMapT, String.valueOf(i), "Draw"));
          }
        }
      }
    }
    return tournamentResults;
  }

  /**
   * validate Maps for tournament are Different
   *
   * @param tournamentMaps tournament Maps
   * @throws GameException if maps duplicated
   */
  private void validateMapsDifferent(List<String> tournamentMaps) throws GameException {
    if (tournamentMaps != null) {
      Set<String> mapsSet = new HashSet<>(tournamentMaps);
      if (tournamentMaps.size() > mapsSet.size()) {
        throw new GameException("Maps Should be Different!");
      }
    }
  }

  /**
   * first Round Of Positioning Armies for players in single or tournament mode
   *
   * @throws GameException if any of game rules violated
   */
  private void firstRoundOfPositioningArmies() throws GameException {
    for (Player player : GameMap.getPlayers().values()) {
      if (player.getPlayerType() != PlayerType.Human) {
        String playerTurn = GameMap.playerTurnNumberBasedOnPlayerReqularNumber(player);
        try {
          player.positioningArmies(playerTurn, null, null);
        } catch (Exception e) {
          LOGGER.error("firstRoundOfPositioningArmies :: {}", e.getMessage());
        } finally {
          if (player.getPlayerStatus() == PlayerStatus.Placing) {
            skipPhase.skip(playerTurn);
          }
        }
      }
    }
  }

  /**
   * startup Tournament phases to prepare needed objects
   *
   * @param tournament object to show players, games and turns
   * @param gameMapT game map that currently will be played
   * @throws Exception if maps not found or any other exception happens
   */
  private void startupTournament(Tournament tournament, String gameMapT) throws Exception {
    String contentToLoad = InternalTools.readFile(gameMapT);
    startupPhase.extractDataFromProvidedMapData(contentToLoad);
    startupPhase.assignCountriesToTournamentPlayers(tournament.getPlayerTypes());
    startupPhase.orderPlayersRandomly();
    startupPhase.calculateInitialArmies();
    startupPhase.assignMinimumNumberOfArmiesToCountries();
    startupPhase.registerPhaseViewObservers();
  }

  /**
   * validate Numbers Range for number of games and number of turns
   *
   * @param tournament object to show players, games and turns
   * @throws GameException if game rules being violated
   */
  private void validateNumbersRange(Tournament tournament) throws GameException {
    if (tournament.getNumberOfGames() < 1 || tournament.getNumberOfGames() > 5) {
      throw new GameException(String.format("Number of games must be between 1 to 5"));
    }
    if (tournament.getNumberOfTurns() < 10 || tournament.getNumberOfTurns() > 50) {
      throw new GameException(String.format("Number of turns must be between 10 to 50"));
    }
  }

  /**
   * remove null or undefined obejetcs from a list
   *
   * @param strings list string objects
   */
  private void removeNulls(List<String> strings) {
    List<String> removeStrings = new ArrayList<>();
    for (String s : strings) {
      if (StringUtils.isBlank(s) || StringUtils.equalsIgnoreCase("undefined", s)) {
        removeStrings.add(s);
      }
    }
    strings.removeAll(removeStrings);
  }

  /**
   * startup Single mode phases to prepare needed objects
   *
   * @param singleMode object to show players
   * @throws Exception if maps not found or any other exception happens
   */
  public void startSingleMode(Tournament singleMode) throws Exception {
    final int maxTry = 50;

    LOGGER.info("singleMode started: {}", singleMode.toString());

    removeNulls(singleMode.getTournamentMaps());
    removeNulls(singleMode.getPlayerTypes());
    GameMap.resetGameMap();
    startupTournament(singleMode, singleMode.getTournamentMaps().get(0));

    if (!isHumanIncluded()) {
      firstRoundOfPositioningArmies();

      int counter = 0;
      while (counter < maxTry && GameMap.getPlayers().values().stream()
          .anyMatch(player -> player.getPlayerStatus() != PlayerStatus.Winner)) {
        for (Integer turnNumber : GameMap.getPlayers().keySet()) {
          autoMove(String.valueOf(turnNumber));
          counter++;
        }
      }
    } else {
      positionHumanComputerPlayers();
    }
  }

  /**
   * check all players to verify Human existence
   *
   * @return true if Human is in the game
   */
  private boolean isHumanIncluded() {
    return GameMap.getPlayers().values().stream()
        .anyMatch(player -> player.getPlayerType() == PlayerType.Human);
  }

  /**
   * auto move of all phases for non-human players
   *
   * @param playerNumber player number to play the auto move
   * @throws Exception if any java operation throws exception
   * @throws GameException if any game rules violated
   */
  public void autoMove(String playerNumber) throws Exception, GameException {

    LOGGER.info("autoMove started: {}", playerNumber);
    int playerTurn = Integer.parseInt(playerNumber);
    Player player = GameMap.getPlayers().get(playerTurn);

    if (player.getPlayerStatus() != PlayerStatus.Eliminated) {

      player.positioningArmies(playerNumber, "", "");
      skipPhase.skip(playerNumber);
      player.attack(playerNumber, "", "", "");
      skipPhase.skip(playerNumber);
      player.fortification(playerNumber, "", "", "");

      if (player.getPlayerStatus() == PlayerStatus.Fortification) {
        skipPhase.skip(playerNumber);
      }

      player.checkWinner();
    }
  }

  /**
   * position human and non-human players turns
   *
   * @throws GameException if any game rules violated
   */
  private void positionHumanComputerPlayers() throws GameException {
    List<Player> humans = new ArrayList<>();
    List<Player> computers = new ArrayList<>();
    for (Player player : GameMap.getPlayers().values()) {
      if (player.getPlayerType() == PlayerType.Human) {
        humans.add(player);
      } else {
        computers.add(player);
      }

    }
    int index = 1;
    for (Player computer : computers) {
      GameMap.getPlayers().put(index, computer);
      index++;
    }
    for (Player human : humans) {
      GameMap.getPlayers().put(index, human);
      index++;
    }

    for (Integer playerIndex : GameMap.getPlayers().keySet()) {
      if (playerIndex == FIRST_PLAYER_IN_LIST) {
        GameMap.getPlayers().get(playerIndex).setPlayerStatus(PlayerStatus.Placing);
      } else {
        GameMap.getPlayers().get(playerIndex).setPlayerStatus(PlayerStatus.Waiting);
      }
    }

    firstRoundOfPositioningArmies();
  }

}
