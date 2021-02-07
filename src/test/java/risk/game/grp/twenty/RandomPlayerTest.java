package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRandom;

/**
 * Unit test for Fortification Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RandomPlayerTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  Player p1, p2, p3, p4;

  @Before
  public void setup() {
    GameMap.resetGameMap();
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    Country usa = new Country("usa");
    usa.setArmies(4);
    usa.addLink("canada");

    Country eu = new Country("eu");
    eu.setArmies(6);
    eu.addLink("canada");

    Country canada = new Country("canada");
    canada.setArmies(5);
    canada.addLink("usa");
    canada.addLink("mexico");
    canada.addLink("eu");

    Country mexico = new Country("mexico");
    mexico.setArmies(3);
    mexico.addLink("canada");
    mexico.addLink("brazil");

    Country brazil = new Country("brazil");
    brazil.setArmies(2);
    brazil.addLink("mexico");

    Country china = new Country("china");
    china.setArmies(4);
    china.addLink("eu");
    china.addLink("india");

    Country india = new Country("india");
    india.setArmies(10);
    india.addLink("china");
    india.addLink("pakistan");

    Country pakistan = new Country("pakistan");
    pakistan.setArmies(10);
    pakistan.addLink("india");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("eu", eu);
    countries.put("brazil", brazil);
    countries.put("china", china);
    countries.put("india", india);
    countries.put("pakistan", pakistan);

    p1 = new PlayerRandom("p1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(eu);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    p2 = new PlayerRandom("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2Countries.add(china);
    p2.setPlayerId(2);
    p2.setPlayerName("p2");
    p2.setPlayerStatus(PlayerStatus.Reinforcement);
    p2.setCountries(p2Countries);
    p2.setArmy(6);

    p3 = new PlayerRandom("p3");
    List<Country> p3Countries = new ArrayList<>();
    p3Countries.add(pakistan);
    p3.setPlayerId(3);
    p3.setPlayerName("p3");
    p3.setPlayerStatus(PlayerStatus.Attack);
    p3.setCountries(p3Countries);

    p4 = new PlayerRandom("p4");
    List<Country> p4Countries = new ArrayList<>();
    p4Countries.add(india);
    p4.setPlayerId(4);
    p4.setPlayerName("p4");
    p4.setPlayerStatus(PlayerStatus.Waiting);
    p4.setCountries(p4Countries);

    players.put(1, p1);
    players.put(2, p2);
    players.put(3, p3);
    players.put(4, p4);
  }


  @Test
  public void testSuccessFortification() throws GameException {
    int previousArmyUSA = GameMap.getCountries().get("usa").getArmies();
    int previousArmyEU = GameMap.getCountries().get("eu").getArmies();
    int previousArmyCanada = GameMap.getCountries().get("canada").getArmies();
    p1.fortification("1", null, null, null);
    int actualArmyUSA = GameMap.getCountries().get("usa").getArmies();
    int actualArmyEU = GameMap.getCountries().get("eu").getArmies();
    int actualArmyCanada = GameMap.getCountries().get("canada").getArmies();
    int oldArmy = 0, newArmy = 0;
    if (previousArmyUSA == actualArmyUSA) {
      oldArmy = previousArmyEU + previousArmyCanada;
      newArmy = actualArmyEU + actualArmyCanada;
    }
    if (previousArmyEU == actualArmyEU) {
      oldArmy = previousArmyUSA + previousArmyCanada;
      newArmy = actualArmyUSA + actualArmyCanada;
    }
    if (previousArmyCanada == actualArmyCanada) {
      oldArmy = previousArmyEU + previousArmyUSA;
      newArmy = actualArmyEU + actualArmyUSA;
    }
    assertEquals(oldArmy, newArmy);
    assertEquals(previousArmyUSA + previousArmyEU + previousArmyCanada,
        actualArmyUSA + actualArmyEU + actualArmyCanada);
  }

  @Test
  public void testSuccessPositioningArmies() throws GameException {
    int oldArmy = p2.getArmy();
    int previousArmyMexico = GameMap.getCountries().get("mexico").getArmies();
    int previousArmyBrazil = GameMap.getCountries().get("brazil").getArmies();
    int previousArmyChina = GameMap.getCountries().get("china").getArmies();
    p2.positioningArmies("2", null, null);
    int actualArmyMexico = GameMap.getCountries().get("mexico").getArmies();
    int actualArmyBrazil = GameMap.getCountries().get("brazil").getArmies();
    int actualArmyChina = GameMap.getCountries().get("china").getArmies();
    int newArmy = 0;
    if (previousArmyMexico != actualArmyMexico) {
      newArmy = actualArmyMexico - previousArmyMexico;
    } else {
      if (previousArmyBrazil != actualArmyBrazil) {
        newArmy = actualArmyBrazil - previousArmyBrazil;
      } else {
        if (previousArmyChina != actualArmyChina) {
          newArmy = actualArmyChina - previousArmyChina;
        }
      }
    }
    assertEquals(p2.getArmy(), 0);
    assertEquals(newArmy, oldArmy);
  }

  @Test
  public void testSuccessfulAttack() throws GameException {
    p3.attack("3", null, null, null);
    int actualArmyPakistan = GameMap.getCountries().get("pakistan").getArmies();
    int actualArmyIndia = GameMap.getCountries().get("india").getArmies();
    assertTrue(actualArmyPakistan > 0);
    assertTrue(actualArmyIndia > 0);
  }
}
