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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.endpoint.ReinforcementPhaseEndpoint;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;

/**
 * Unit test for Reinforcement Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReinforcementPhaseTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  @Autowired
  private ReinforcementPhaseEndpoint reinforcementPhaseEndpoint;

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
    p1.setPlayerStatus(PlayerStatus.Waiting);
    p1.setCountries(p1Countries);

    Player p2 = new PlayerRegular("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2Countries.add(china);
    p2.setPlayerId(2);
    p2.setPlayerStatus(PlayerStatus.Reinforcement);
    p2.setCountries(p2Countries);
    p2.setArmy(12);
    p2.setCard(6);

    players.put(1, p2);
    players.put(2, p1);
  }

  @Test
  public void testSuccessPositioningArmies() {
    int p_armies = GameMap.getCountries().get("Brazil").getArmies();
    reinforcementPhaseEndpoint.positioningArmies("1", "Brazil", "8");
    assertEquals(p_armies + 8, GameMap.getCountries().get("Brazil").getArmies());
  }

  @Test
  public void testSuccessPositioningArmiesWithOneBadPath() {
    int p_armies = GameMap.getCountries().get("Brazil").getArmies();
    reinforcementPhaseEndpoint.positioningArmies("1", "Brazil", "90");
    assertEquals(p_armies, GameMap.getCountries().get("Brazil").getArmies());
  }

  @Test
  public void testSuccessExchangeCard() {
    int previousArmy = GameMap.getPlayers().get(1).getArmy();
    int previousExchange = GameMap.getPlayers().get(1).getTimesAlreadyExchangedCardsForArmy();
    reinforcementPhaseEndpoint.exchangeCard("1");
    reinforcementPhaseEndpoint.exchangeCard("1");
    int addedArmy = 15 + previousExchange * 10;
    assertEquals(previousArmy + addedArmy, GameMap.getPlayers().get(1).getArmy());

  }

  @Test
  public void testSuccessExchangeCardWithOneBadPath() {
    GameMap.getPlayers().get(1).setCard(0);
    int previousArmy = GameMap.getPlayers().get(1).getArmy();
    reinforcementPhaseEndpoint.exchangeCard("1");
    assertEquals(previousArmy, GameMap.getPlayers().get(1).getArmy());
  }
}
