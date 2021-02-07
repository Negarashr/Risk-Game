package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * <p>This class is responsible for taking care of countries, continents and players. all the data
 * which has been read from file in the startup phase populates objects in this class.</p>
 *
 * <p>After creation of objects in this class they will be the source for <em>all the phases</em>
 * in game, means in any phase the data needed to fulfill a task or providing a service should be
 * get from this class objects</p>
 *
 * @author Konstantin
 * @author Farzaneh
 * @author Ali
 */
@Component
public class GameMap implements Serializable {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameMap.class);
  private static final long serialVersionUID = 1L;

  private static Map<String, Country> countries = new TreeMap<String, Country>(
      String.CASE_INSENSITIVE_ORDER);
  private static Map<String, Continent> continents = new TreeMap<String, Continent>(
      String.CASE_INSENSITIVE_ORDER);
  private static Map<Integer, Player> players = new HashMap<Integer, Player>();

  public static Map<String, Country> getCountries() {

    return countries;
  }

  public static void setCountries(Map<String, Country> countries) {
    GameMap.countries = countries;
  }

  public static Map<String, Continent> getContinents() {
    return continents;
  }

  public static void setContinents(Map<String, Continent> continents) {
    GameMap.continents = continents;
  }

  public static Map<Integer, Player> getPlayers() {
    return players;
  }

  public static void setPlayers(Map<Integer, Player> players) {
    GameMap.players = players;
  }

  /**
   * This method extract Country objects from Continents.
   */
  public static void extractCountriesFromContinents() {

    Set<String> continentSet = continents.keySet();
    for (String continentItem : continentSet) {
      List<Country> countryList = continents.get(continentItem).getCountries();
      for (Country country : countryList) {
        countries.put(country.getName(), country);
      }
    }

    LOGGER.info("extractCountriesFromContinents finished countries size: {}", countries.size());
  }

  /**
   * get all paths from 'sourceCountry' to 'destinationCountry'
   *
   * @param sourceCountry country to start
   * @param destinationCountry country to end
   * @return list of all paths between two countries
   */
  public static List<List<String>> getAllPaths(String sourceCountry, String destinationCountry) {
    List<String> visited = new ArrayList<>();
    List<String> pathList = new ArrayList<>();
    List<List<String>> foundedPaths = new ArrayList<>();

    pathList.add(sourceCountry);

    getAllPathsUtil(sourceCountry, destinationCountry, visited, pathList, foundedPaths);

    return foundedPaths;
  }

  /**
   * A recursive function to get all paths from 'sourceCountry' to 'destinationCountry' visited
   * keeps track of vertices in current path.localPathList<> stores actual vertices in the current
   * path
   *
   * @param sourceCountry source country
   * @param destinationCountry destination country
   * @param visited contains the countries visited in path
   * @param localPathList contains the paths founded starting with the source country
   * @param foundedPaths if any path founded maintains in this object
   */
  private static void getAllPathsUtil(String sourceCountry, String destinationCountry,
      List<String> visited,
      List<String> localPathList, List<List<String>> foundedPaths) {

    visited.add(sourceCountry);

    if (sourceCountry.equals(destinationCountry)) {
      LOGGER.info("Path found between : {}", localPathList);
      foundedPaths.add(new ArrayList<>(localPathList));
      // if match found then no need to traverse more till depth
      visited.remove(sourceCountry);
      return;
    }

    // Recur for all the vertices adjacent to current vertex
    for (String country : GameMap.getCountries().get(sourceCountry).getLinkedCountries()) {

      if (!visited.contains(country)) {
        localPathList.add(country);
        getAllPathsUtil(country, destinationCountry, visited, localPathList, foundedPaths);
        localPathList.remove(country);
      }
    }

    visited.remove(sourceCountry);
  }

  /**
   * Reset All contents in Game Map
   */
  public static void resetGameMap() {

    GameMap.getCountries().clear();
    GameMap.getContinents().clear();
    GameMap.getPlayers().clear();

    Country.resetCountryCounter();
    Player.resetPlayerCounter();
  }

  /**
   * Method to find player owning the country
   *
   * @param countryName name of the country
   * @return player owner of the country
   */
  public static Player getPlayer(String countryName) {
    for (Player p : players.values()) {
      for (Country c : p.getCountries()) {
        if (c.getName().equals(countryName)) {
          return p;
        }
      }
    }
    return null;
  }

  /**
   * @return Domination view of game based on phase two definitions
   */
  public static DominationView getDominationView() {
    DominationView dominationView = new DominationView();

    calculatePlayerCountryOwnershipPercentage(dominationView);
    getPlayersContinentOwnership(dominationView);
    calculatePlayersTotalArmy(dominationView);

    return dominationView;
  }

  /**
   * based on domination view calculates total number of armies for each player
   *
   * @param dominationView domination view instance
   */
  private static void calculatePlayersTotalArmy(DominationView dominationView) {

    GameMap.getPlayers().values().stream().forEach(player -> {
      int totalArmies = player.getArmy();
      for (Country country : player.getCountries()) {
        totalArmies += country.getArmies();
      }
      dominationView.getPlayersTotalArmy().put(player.getPlayerName(), String.valueOf(totalArmies));
    });
  }

  /**
   * based on domination view calculates percentage of ownership for each player
   *
   * @param dominationView domination view instance
   */
  private static void calculatePlayerCountryOwnershipPercentage(DominationView dominationView) {

    int totalNumberOfCountries = GameMap.getCountries().size();
    float oneCountryPercentage = (100f) / totalNumberOfCountries;

    GameMap.getPlayers().values().stream().forEach(player -> {
      float percentage = player.getCountries().size() * oneCountryPercentage;
      dominationView.getPlayersMapOwnership()
          .put(player.getPlayerName(), String.format("%.2f", percentage) + "%");
    });
  }

  /**
   * checks whether given player owns all country in a continent and if so plus control value to the
   * players armies.
   *
   * @param dominationView to check if owns all countries in a continent
   * @return 0 if not owning else control value
   */
  private static void getPlayersContinentOwnership(DominationView dominationView) {

    GameMap.getPlayers().values().stream().forEach(player -> {
      List<String> continents = new ArrayList<>();
      for (Continent continent : GameMap.getContinents().values()) {
        int countriesOwnFromContinent = 0;
        for (Country country : continent.getCountries()) {
          boolean ownsCountry = player.getCountries().stream()
              .anyMatch(c -> c.getName().equalsIgnoreCase(country.getName()));
          if (ownsCountry) {
            countriesOwnFromContinent++;
          }
        }
        if (countriesOwnFromContinent == continent.getCountries().size()) {
          continents.add(continent.getName());

        }
      }
      dominationView.getPlayersContinentOwnership().put(player.getPlayerName(), continents);
    });
  }

  /**
   * calculates player Turn Number Based On Player Regular Number
   *
   * @param p player which we want to determine its turn in game
   * @return player turn in game
   */
  public static String playerTurnNumberBasedOnPlayerReqularNumber(Player p) {
    for (Integer index : players.keySet()) {
      if (players.get(index).equals(p)) {
        return String.valueOf(index);
      }
    }
    return null;
  }
}