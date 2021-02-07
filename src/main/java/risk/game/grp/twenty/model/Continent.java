package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Model of <em>Continent</em> Object in Game.
 *
 * @author Team 20
 */
public class Continent implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name;
  private List<Country> countries = new ArrayList<>();
  private int controlValue = 0;

  public Continent() {

  }

  public Continent(String name, int controlValue) {
    this.name = name;
    this.controlValue = controlValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getControlValue() {
    return controlValue;
  }

  public void setControlValue(int controlValue) {
    this.controlValue = controlValue;
  }

  public List<Country> getCountries() {
    return countries;
  }

  public void setCountries(List<Country> countries) {
    this.countries = countries;
  }
}
