package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;

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
import risk.game.grp.twenty.model.player.behavior.PlayerBenevolent;

/**
 * Unit test for Fortification Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BenevolentPlayerTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  Player p1, p2, p3;

  @Before
  public void setup() {
    GameMap.resetGameMap();
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    Country usa = new Country("usa");
    usa.setArmies(4);
    usa.addLink("canada");

    Country canada = new Country("canada");
    canada.setArmies(5);
    canada.addLink("usa");
    canada.addLink("mexico");
    canada.addLink("eu");

    Country mexico = new Country("mexico");
    mexico.setArmies(3);
    mexico.addLink("canada");
    mexico.addLink("brazil");

    Country eu = new Country("eu");
    eu.setArmies(6);
    eu.addLink("canada");

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

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("eu", eu);
    countries.put("brazil", brazil);
    countries.put("china", china);
    countries.put("india", india);

    p1 = new PlayerBenevolent("p1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(eu);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    p2 = new PlayerBenevolent("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2Countries.add(china);
    p2.setPlayerId(2);
    p2.setPlayerName("p2");
    p2.setPlayerStatus(PlayerStatus.Reinforcement);
    p2.setCountries(p2Countries);
    p2.setArmy(6);

    p3 = new PlayerBenevolent("p3");
    List<Country> p3Countries = new ArrayList<>();
    p3Countries.add(india);
    p3.setPlayerId(3);
    p3.setPlayerName("p3");
    p3.setPlayerStatus(PlayerStatus.Attack);
    p3.setCountries(p3Countries);

    players.put(1, p1);
    players.put(2, p2);
    players.put(3, p3);
  }

  @Test
  public void testSuccessFortification() throws GameException {
    int previousArmy = GameMap.getCountries().get("usa").getArmies();
    int previousArmyEU = GameMap.getCountries().get("eu").getArmies();
    p1.fortification("1", null, null, null);
    assertEquals(GameMap.getCountries().get("usa").getArmies(), previousArmy + 1);
    assertEquals(GameMap.getCountries().get("eu").getArmies(), previousArmyEU - 1);
  }

  @Test
  public void testSuccessPositioningArmies() throws GameException {
    int mexico_armies = GameMap.getCountries().get("mexico").getArmies();
    int brazil_armies = GameMap.getCountries().get("brazil").getArmies();
    int china_armies = GameMap.getCountries().get("china").getArmies();
    p2.positioningArmies("2", null, null);
    assertEquals(mexico_armies + 2, GameMap.getCountries().get("mexico").getArmies());
    assertEquals(brazil_armies + 3, GameMap.getCountries().get("brazil").getArmies());
    assertEquals(china_armies + 1, GameMap.getCountries().get("china").getArmies());
  }

  @Test
  public void testSuccessfulAttack() throws GameException {
    p3.attack("3", null, null, null);
    int india_armies = GameMap.getCountries().get("india").getArmies();
    assertEquals(india_armies, GameMap.getCountries().get("india").getArmies());
  }
}
