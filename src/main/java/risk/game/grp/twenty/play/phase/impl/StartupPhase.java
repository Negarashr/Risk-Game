package risk.game.grp.twenty.play.phase.impl;

import static risk.game.grp.twenty.constant.GameConstant.FIRST_PLAYER_IN_LIST;
import static risk.game.grp.twenty.constant.GameConstant.VALID_MAP_EXTENSION;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.constant.GameConstant;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.player.behavior.PlayerAggressive;
import risk.game.grp.twenty.model.player.behavior.PlayerBenevolent;
import risk.game.grp.twenty.model.player.behavior.PlayerCheater;
import risk.game.grp.twenty.model.player.behavior.PlayerRandom;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.observers.ViewPhase;
import risk.game.grp.twenty.rule.InputFileVerification;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * This Class is Responsible for Starting up the Game
 *
 * @author ali
 * @see Continent
 * @see Country
 * @see Player
 * @see GameMap
 */
@Component
public class StartupPhase {

  private final static Logger LOGGER = LoggerFactory.getLogger(StartupPhase.class);
  @Autowired
  ViewPhase viewPhaseObserver;
  //map of all sessions
  private Map<String, GameMap> gameMap = null;

  /**
   * Default constructor
   */
  public StartupPhase() {
    gameMap = new HashMap<String, GameMap>();
  }

  /**
   * This Method calculates initial number of armies based on number of players
   */
  public static void calculateInitialArmies() {
    final int playerNumbers = GameMap.getPlayers().size();
    if (playerNumbers == 2) {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(40));
    } else if (playerNumbers == 3) {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(35));
    } else if (playerNumbers == 4) {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(30));
    } else if (playerNumbers == 5) {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(25));
    } else if (playerNumbers == 6) {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(20));
    } else {
      GameMap.getPlayers().values().stream().forEach(p -> p.setArmy(15));
    }
  }

  /**
   * This method assigns minimum number of armies to each country in the game
   */
  public static void assignMinimumNumberOfArmiesToCountries() {
    GameMap.getPlayers().values().stream().forEach(p -> {
      int initialArmy = p.getArmy();
      p.setArmy(initialArmy - p.getCountries().size());
      p.getCountries().forEach(c -> c.setArmies(1));
    });
  }

  /**
   * Method to create a new game session
   *
   * @return String unique ID
   */
  public String createGameMap() {
    String uuid = UUID.randomUUID().toString();
    GameMap newGame = new GameMap();
    gameMap.put(uuid, newGame);
    return uuid;
  }

  /**
   * Method to check if game session with given uuid exists
   *
   * @param uuid ID of game session
   * @return true if such session exists, false otherwise
   */
  public boolean checkGame(String uuid) {
    if (gameMap.containsKey(uuid)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method returning game session
   *
   * @param uuid ID of game session
   * @return game session if such ID exists, null otherwise
   */
  public GameMap getGame(String uuid) {
    if (checkGame(uuid)) {
      return gameMap.get(uuid);
    } else {
      return null;
    }
  }

  /**
   * first assign minimum number of country to each player randomly, then assign rest of countries
   * to players randomly
   *
   * @param playersNumber number of players to start the game
   * @throws GameException if number of countries is less than number of players
   */
  public void assignCountriesToPlayers(int playersNumber) throws GameException {

    // this part should be changed if multiple game want to take place at same time
    Map<Integer, Player> players = GameMap.getPlayers();

    final List<Country> countries = new ArrayList<>(GameMap.getCountries().values());

    if (countries.size() < playersNumber) {
      throw new GameException(
          "Number of countries '" + countries.size() + "' is less than number of players '"
              + playersNumber + "', Game can NOT start!");
    }

    for (int i = 0; i < playersNumber; i++) {
      assignMinNumberOfCountryToPlayer(countries, i);
    }

    randomlyAssignRestOfCountries(players, countries);

    LOGGER.info("assignCountriesToPlayers Finished.");
  }

  public void assignCountriesToTournamentPlayers(List<String> playerTypes) throws GameException {

    // this part should be changed if multiple game want to take place at same time
    Map<Integer, Player> players = GameMap.getPlayers();

    final List<Country> countries = new ArrayList<>(GameMap.getCountries().values());

    if (countries.size() < playerTypes.size()) {
      throw new GameException(
          "Number of countries '" + countries.size() + "' is less than number of players '"
              + playerTypes.size() + "', Game can NOT start!");
    }

    for (int i = 0; i < playerTypes.size(); i++) {
      assignMinNumberOfCountryToTournamentPlayer(countries, i, playerTypes.get(i));
    }

    randomlyAssignRestOfCountries(players, countries);

    LOGGER.info("assignCountriesToTournamentPlayers Finished.");
  }

  private void randomlyAssignRestOfCountries(Map<Integer, Player> players,
      List<Country> countries) {
    while (countries.size() > 0) {
      final int randomPlayer = InternalTools.getRandomNumberInRange(1, players.size());
      final int randomCountry = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
      Player player = players.get(randomPlayer);
      player.getCountries().add(countries.get(randomCountry));
      countries.remove(randomCountry);
    }
  }

  /**
   * based on the map content it gets, extracts information for <em>Country</em> and
   * <em>Continent</em> objects.
   *
   * @param mapContent map content to create the game based on it
   * @throws Exception if content is not based on the expected format
   */
  public void extractDataFromProvidedMapData(String mapContent) throws Exception {

    final String[] fileContent = mapContent.split("\n");
    String currentLine = StringUtils.EMPTY;
    try {
      for (String line : fileContent) {
        currentLine = line;
        LOGGER.info(line);
        if (line.contains("=")) {
          createContinentsFromFile(line);
        } else if (line.contains(",")) {
          createCountriesFromFile(line);
        }
      }
    } catch (Exception e) {
      throw new Exception(String.format("Error happened in line: %s", currentLine));
    }

    GameMap.extractCountriesFromContinents();
    InputFileVerification.verifyGameMap();

    LOGGER.info("extractDataFromProvidedMapData Finished.");
  }

  /**
   * create <em>Country</em> object based on map content lines
   *
   * @param line line of map content which contains Country Data
   * @throws Exception if line content is not correct
   */
  private void createCountriesFromFile(String line) throws Exception {

    try {
      String[] countryLine = line.split(",");

      if (countryLine.length < GameConstant.MIN_NUMBER_OF_STRINGS_IN_COUNTRY_LINE) {
        throw new GameException(
            "The line: '" + line + "' , is NOT correct please modify and retry.");
      }

      String countryName = countryLine[0].toLowerCase().trim();
      int x = Integer.parseInt(countryLine[1]);
      int y = Integer.parseInt(countryLine[2]);
      String continentName = countryLine[3].toLowerCase().trim();
      Continent continent = GameMap.getContinents().get(continentName);
      Country country = new Country(countryName);

      // extract countries linked to the current line country
      for (int i = 4; i < countryLine.length; i++) {
        LOGGER
            .info("country: {}, continent name: {}, linked country: {}", countryName, continentName,
                countryLine[i]);

        country.addLink(countryLine[i].toLowerCase().trim());
      }

      continent.getCountries().add(country);
    } catch (Exception e) {
      throw new Exception(String.format("Error happened in line: %s", line));
    }

  }

  /**
   * create <em>Continent</em> object based on map content lines
   *
   * @param line line of map content which contains Continent Data
   * @throws Exception if line content is not correct
   */
  private void createContinentsFromFile(String line) throws Exception {
    try {
      String[] continentLine = line.split("=");
      if (continentLine.length < GameConstant.MIN_NUMBER_OF_STRINGS_IN_CONTINENT_LINE) {
        throw new GameException(
            "The line: '" + line + "' , is NOT correct please modify and retry.");
      }
      String continentName = continentLine[0].toLowerCase().trim();
      int controlValue = Integer.parseInt(continentLine[1]);
      Continent continent = new Continent(continentName, controlValue);
      GameMap.getContinents().put(continentName, continent);
    } catch (Exception e) {
      throw new Exception(String.format("Error happened in line: %s", line));
    }
  }

  /**
   * gives turn to players randomly and in round robin fashion.
   */
  public void orderPlayersRandomly() {
    Map<Integer, Player> playersTurn = new HashMap<>();
    List<Player> players = new ArrayList<>(GameMap.getPlayers().values());

    int turn = 1;
    while (players.size() > 0) {
      final int randomPlayer = InternalTools.getRandomNumberInRange(0, players.size() - 1);
      Player playerToGetTurn = players.get(randomPlayer);
      playersTurn.put(turn, playerToGetTurn);
      GameMap.getPlayers().remove(playerToGetTurn.getPlayerId());
      players.remove(playerToGetTurn);
      turn++;
    }
    playersTurn.get(FIRST_PLAYER_IN_LIST).setPlayerStatus(PlayerStatus.Placing);
    GameMap.setPlayers(playersTurn);
  }

  /**
   * assign minimum number of countries to each player
   *
   * @param countries list of all countries
   * @param i player counter
   */
  private void assignMinNumberOfCountryToPlayer(List<Country> countries, int i) {
    final int playerNameCounter = i + 1;
    Player player = new PlayerRegular(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    int counter = 0;
    while (counter < GameConstant.MIN_NUMBER_OF_COUNTRIES_TO_HAVE_ONE_ARMY) {
      final int randomNumber = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
      player.getCountries().add(countries.get(randomNumber));
      countries.remove(randomNumber);
      counter++;
    }
    GameMap.getPlayers().put(player.getPlayerId(), player);
  }

  private void assignMinNumberOfCountryToTournamentPlayer(List<Country> countries, int i, String playerType) {
    final int playerNameCounter = i + 1;
    Player player = null;

    if (StringUtils.equalsIgnoreCase(playerType, PlayerType.Aggressive.name())) {
      player = new PlayerAggressive(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    } else if (StringUtils.equalsIgnoreCase(playerType, PlayerType.Benevolent.name())) {
      player = new PlayerBenevolent(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    } else if (StringUtils.equalsIgnoreCase(playerType, PlayerType.Random.name())) {
      player = new PlayerRandom(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    } else if (StringUtils.equalsIgnoreCase(playerType, PlayerType.Cheater.name())) {
      player = new PlayerCheater(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    } else if (StringUtils.equalsIgnoreCase(playerType, PlayerType.Human.name())) {
      player = new PlayerRegular(GameConstant.PLAYER_PREFIX_DEFAULT_NAME + playerNameCounter);
    }

    int counter = 0;
    while (counter < GameConstant.MIN_NUMBER_OF_COUNTRIES_TO_HAVE_ONE_ARMY) {
      final int randomNumber = InternalTools.getRandomNumberInRange(0, countries.size() - 1);
      player.getCountries().add(countries.get(randomNumber));
      countries.remove(randomNumber);
      counter++;
    }
    GameMap.getPlayers().put(player.getPlayerId(), player);
  }

  /**
   * get path of all maps in map directory
   *
   * @param path path of map directory
   * @return list of maps paths
   * @throws IOException if path not found or any other IO related exception occurs
   */
  public List<String> getExistingMaps(String path) throws IOException {
    List<String> mapNames = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get(path))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(f -> {
            if (f.toString().endsWith(VALID_MAP_EXTENSION)) {
              mapNames.add(f.toString());
            }
          });
    }
    return mapNames;
  }

  public void registerPhaseViewObservers() {
    GameMap.getPlayers().values().stream().forEach(player -> player.addObserver(viewPhaseObserver));
  }
}
