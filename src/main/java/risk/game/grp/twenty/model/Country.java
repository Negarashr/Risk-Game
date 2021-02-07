package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is the Model of <em>Country</em> Object in Game.
 *
 * @author Team 20
 */
public class Country implements Serializable {

  private static final long serialVersionUID = 1L;

  private static int countriesCounter = 0;
  private int countryId;
  private int armies = 0;
  private String name;
  private Set<String> linkedCountries = new HashSet<>();

  private Country() {

  }

  public Country(String name) {
    this.countryId = ++countriesCounter;
    this.name = name;
  }

  public static void resetCountryCounter() {
    countriesCounter = 0;
  }

  public int getCountryId() {
    return countryId;
  }

  public void setCountryId(int countryId) {
    this.countryId = countryId;
  }

  public int getArmies() {
    return armies;
  }

  public void setArmies(int armies) {
    this.armies = armies;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addLink(String countryName) {
    linkedCountries.add(countryName);
  }

  public List<String> getLinkedCountries() {
    List<String> linkedCountriesList = new ArrayList<>(linkedCountries);

    return linkedCountriesList;
  }
}