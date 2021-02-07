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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.endpoint.FortificationPhaseEndpoint;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;

/**
 * Unit test for Fortification Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FortificationPhaseTest {


  @Rule
  public ExpectedException exc = ExpectedException.none();

  @Autowired
  private FortificationPhaseEndpoint fortificationPhaseEndpoint;

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
    china.setArmies(9);
    china.addLink("eu");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("eu", eu);
    countries.put("brazil", brazil);
    countries.put("china", china);

    Player p1 = new PlayerRegular("p1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(eu);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    Player p2 = new PlayerRegular("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2Countries.add(china);
    p2.setPlayerId(2);
    p2.setPlayerName("p2");
    p2.setPlayerStatus(PlayerStatus.Waiting);
    p2.setCountries(p2Countries);

    players.put(1, p2);
    players.put(2, p1);
  }

  @Test
  public void testSuccessFortification() {

    fortificationPhaseEndpoint.fortification("2", "usa", "eu", "2");
    assertEquals(GameMap.getCountries().get("eu").getArmies(), 8);

    List<Country> playerCountries = GameMap.getPlayers().get(2).getCountries();
    for (Country c : playerCountries) {
      if (c.getName().equalsIgnoreCase("eu")) {
        assertEquals(c.getArmies(), 8);
      }
    }

  }

  @Test
  public void testSuccessFortificationWithOneBadPath() {

    GameMap.resetGameMap();
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    Country usa = new Country("usa");
    usa.setArmies(4);
    usa.addLink("mexico");
    usa.addLink("canada");

    Country canada = new Country("canada");
    canada.setArmies(5);
    canada.addLink("usa");
    canada.addLink("brazil");

    Country mexico = new Country("mexico");
    mexico.setArmies(3);
    mexico.addLink("brazil");

    Country brazil = new Country("brazil");
    brazil.setArmies(2);
    brazil.addLink("mexico");
    brazil.addLink("canada");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("brazil", brazil);

    Player p1 = new PlayerRegular("p1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(brazil);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    Player p2 = new PlayerRegular("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2.setPlayerId(2);
    p2.setPlayerStatus(PlayerStatus.Waiting);
    p2.setCountries(p2Countries);

    int brazilArmiesBefore = GameMap.getCountries().get("brazil").getArmies();

    players.put(1, p1);
    players.put(2, p2);
    fortificationPhaseEndpoint.fortification("1", "usa", "brazil", "2");
    assertEquals(GameMap.getCountries().get("brazil").getArmies(), brazilArmiesBefore + 2);

  }

  @Test
  public void testUnSuccessFortification() {

    GameMap.resetGameMap();
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    Country usa = new Country("usa");
    usa.setArmies(4);
    usa.addLink("mexico");
    usa.addLink("canada");

    Country canada = new Country("canada");
    canada.setArmies(5);
    canada.addLink("usa");
    canada.addLink("brazil");

    Country mexico = new Country("mexico");
    mexico.setArmies(3);
    mexico.addLink("brazil");

    Country brazil = new Country("brazil");
    brazil.setArmies(2);
    brazil.addLink("mexico");
    brazil.addLink("canada");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("brazil", brazil);

    Player p1 = new PlayerRegular("p1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(brazil);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    Player p2 = new PlayerRegular("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(canada);
    p2.setPlayerId(2);
    p2.setPlayerStatus(PlayerStatus.Waiting);
    p2.setCountries(p2Countries);

    int brazilArmiesBefore = GameMap.getCountries().get("brazil").getArmies();

    players.put(1, p1);
    players.put(2, p2);
    fortificationPhaseEndpoint.fortification("1", "usa", "brazil", "2");
    assertEquals(GameMap.getCountries().get("brazil").getArmies(), brazilArmiesBefore);

  }

  @Test
  public void testPlayerNotInFortificationPhase() {

    ResponseEntity<Object> responseEntity = fortificationPhaseEndpoint
        .fortification("1", "usa", "eu", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testValidateUserInput() {

    ResponseEntity<Object> responseEntity = fortificationPhaseEndpoint
        .fortification("6", "usa", "eu", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "QQW", "eu", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "usa", "eu11", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "usa", "usa", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "usa", "eu", "200");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "usa", "eu", "-1");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Fortification);
    GameMap.getPlayers().get(2).setPlayerStatus(PlayerStatus.Waiting);
    responseEntity = fortificationPhaseEndpoint
        .fortification("1", "china", "brazil", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

}
