package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * Class for throwing n dice, simulating cube die (numbers are from 1 to 6)
 *
 * @author Team 20
 */
public class Dice implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Integer> results = new ArrayList<>();
  private int diceCount = 0;

  /**
   * Default constructor
   *
   * @param n number of dice
   */
  public Dice(int n) {
    diceCount = n;
    for (int i = 0; i < n; i++) {
      results.add(InternalTools.getRandomNumberInRange(1, 6));
    }
    Collections.sort(results);
    Collections.reverse(results);
  }

  /**
   * Method to get results
   *
   * @return list of integer sorted in decreasing order
   */
  public List<Integer> getResults() {
    return results;
  }

  /**
   * Method to get number of dice
   *
   * @return number of dice
   */
  public int getDiceCount() {
    return diceCount;
  }
}
