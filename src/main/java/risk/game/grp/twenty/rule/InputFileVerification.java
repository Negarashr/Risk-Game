package risk.game.grp.twenty.rule;

import static risk.game.grp.twenty.tools.InternalTools.listEqualsIgnoreOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;

/**
 * This class is responsible of checking the map connect be connected graph for countries
 *
 * @author ali
 */
public class InputFileVerification {

  private final static Logger LOGGER = LoggerFactory.getLogger(InputFileVerification.class);

  /**
   * traverse all countries in GameMap recursively
   *
   * @param visited visited country
   * @param currentVertex current country
   * @return list of all visited countries
   */
  private static List<String> traverseCountries(List<String> visited, String currentVertex) {

    if (visited != null && visited.contains(currentVertex)) {
      return visited;
    }
    visited.add(currentVertex);
    List<Country> countries = new ArrayList<>(GameMap.getCountries().values());
    for (Country country : countries) {
      List<String> list = country.getLinkedCountries();
      for (String vertex : list) {
        traverseCountries(visited, vertex);
      }


    }
    System.out.print(currentVertex + "->");
    return visited;
  }

  /**
   * verify the game map is connected graph.
   *
   * @throws GameException if visited countries and countries in GameMap object not same
   */
  public static void verifyGameMap() throws GameException {
    Map.Entry<String, Country> entry = GameMap.getCountries().entrySet().iterator().next();
    String key = entry.getKey();
    List<String> countriesInMap = new ArrayList<String>(GameMap.getCountries().keySet());
    List<String> traverseCountries = traverseCountries(new ArrayList<String>(), key);

    if (!listEqualsIgnoreOrder(traverseCountries, countriesInMap)) {
      throw new GameException(
          "The map file is NOT connected graph please check your map and retry.");
    }

    LOGGER.info("Map verified Successfully");
  }

}
