package risk.game.grp.twenty.play.phase.impl;

import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_SAVED_GAMES_BASE_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.observers.ViewPhase;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * handel save load of game map
 *
 * @author ali
 */
@Service
public class SaveLoadPhase {

  private final static Logger LOGGER = LoggerFactory.getLogger(SaveLoadPhase.class);
  private static int savedFileCounter = 1;

  @Autowired
  ViewPhase viewPhaseObserver;


  /**
   * Saves the current Game to disk as file
   *
   * @return name of the saved game
   * @throws Exception if file not found or I/O issues happens
   */
  public String save() throws Exception {
    LOGGER.info("SAVE process started");

    if (savedFileCounter > 5) {
      savedFileCounter = 1;
    }

    String savePrefix = String.valueOf(savedFileCounter++);
    Map<Integer, Player> players = GameMap.getPlayers();
    Map<String, Continent> continents = GameMap.getContinents();

    InternalTools
        .writeObjectInFile(
            new File(DEFAULT_SAVED_GAMES_BASE_PATH + savePrefix + "_" + "players.save"), players);
    InternalTools
        .writeObjectInFile(
            new File(DEFAULT_SAVED_GAMES_BASE_PATH + savePrefix + "_" + "continents.save"),
            continents);

    return savePrefix + "_" + "players.save";

  }

  /**
   * loads the requested game from file from disk.
   *
   * @param savePrefix name of teh file to load game from.
   * @throws Exception if file not found or I/O issues happens
   */
  public void load(String savePrefix) throws Exception {
    LOGGER.info("load process started");

    GameMap.resetGameMap();

    String prefixToLoad = String.valueOf(savePrefix.charAt(savePrefix.indexOf("/") + 1));

    Map<Integer, Player> players = (Map<Integer, Player>) InternalTools
        .readObjectsFromFile(
            new File(DEFAULT_SAVED_GAMES_BASE_PATH + prefixToLoad + "_" + "players.save"));
    Map<String, Continent> continents = (Map<String, Continent>) InternalTools
        .readObjectsFromFile(
            new File(DEFAULT_SAVED_GAMES_BASE_PATH + prefixToLoad + "_" + "continents.save"));

    extractCountries(players);
    extractContinents(continents);

    GameMap.setContinents(continents);
    GameMap.setPlayers(players);

    registerPhaseViewObservers();
  }

  /**
   * extract countries data from loaded player object
   *
   * @param players loaded players from deserializer
   */
  private void extractCountries(Map<Integer, Player> players) {
    for (Player player : players.values()) {
      for (Country country : player.getCountries()) {
        GameMap.getCountries().put(country.getName(), country);
      }
    }
  }

  /**
   * extract continents data from loaded player object and countries objects
   *
   * @param continents loaded continents from deserializer
   */
  private void extractContinents(Map<String, Continent> continents) {
    for (Continent continent : continents.values()) {
      List<Country> countryList = new ArrayList<>();
      for (Country c2 : continent.getCountries()) {
        for (Country country : GameMap.getCountries().values()) {
          if (StringUtils.equalsIgnoreCase(c2.getName(), country.getName())) {
            countryList.add(country);
            continue;
          }
        }
      }
      Continent c = new Continent();
      c.setControlValue(continent.getControlValue());
      c.setName(continent.getName());
      c.setCountries(countryList);
      GameMap.getContinents().put(c.getName(), c);
    }
  }

  /**
   * register the observers to loaded players from deserializer
   */
  public void registerPhaseViewObservers() {
    GameMap.getPlayers().values().stream().forEach(player -> player.addObserver(viewPhaseObserver));
  }

  /**
   * @param path to read saved games from
   * @return list of saved games
   * @throws IOException if file not found
   */
  public List<String> getExistingSavedGames(String path) throws IOException {
    List<String> games = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get(path))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(f -> {
            if (StringUtils.containsIgnoreCase(f.toString(), "players.save")) {
              games.add(f.toString());
            }
          });
    }
    return games;
  }

}
