package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static risk.game.grp.twenty.constant.GameConstant.MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.endpoint.SkipEndpoint;
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;
import risk.game.grp.twenty.play.phase.impl.StartupPhase;

/**
 * Unit test for Skip Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SkipPhaseTest {


  @Autowired
  private SkipEndpoint skip;

  @Before
  public void setup() {
    GameMap.resetGameMap();

    prepareGameMapMock();
  }

  @Test
  public void testSuccessSkip() {

    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(2).getArmy(), 113);

  }

  @Test
  public void testNotDoRecalculation() {
    int preArmies = GameMap.getPlayers().get(2).getArmy();
    skip.skip("2");
    assertEquals(GameMap.getPlayers().get(2).getArmy(), preArmies);

  }

  @Test
  public void testSuccessRecalculation() {
    int preArmies = GameMap.getPlayers().get(2).getArmy();
    GameMap.getPlayers().get(2).setPlayerStatus(PlayerStatus.Reinforcement);
    skip.skip("1");
    int addedArmy = GameMap.getPlayers().get(2).getArmy() - preArmies;
    assertTrue(addedArmy >= MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES);

  }

  @Test
  public void testSkipScenarios() {
    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Waiting);
    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(1).getPlayerStatus(), PlayerStatus.Waiting);

    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Reinforcement);
    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(1).getPlayerStatus(), PlayerStatus.Attack);

    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Attack);
    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(1).getPlayerStatus(), PlayerStatus.Fortification);

    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Fortification);
    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(1).getPlayerStatus(), PlayerStatus.Waiting);

    GameMap.getPlayers().get(1).setPlayerStatus(PlayerStatus.Placing);
    skip.skip("1");
    assertEquals(GameMap.getPlayers().get(1).getPlayerStatus(), PlayerStatus.Waiting);

    GameMap.getPlayers().get(2).setPlayerStatus(PlayerStatus.Placing);
    skip.skip("2");
    assertEquals(GameMap.getPlayers().get(2).getPlayerStatus(), PlayerStatus.Waiting);

  }

  private void prepareGameMapMock() {
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();
    Map<String, Continent> continents = GameMap.getContinents();

    Continent southAmerica = new Continent();
    southAmerica.setName("South America");
    southAmerica.setControlValue(20);

    Continent northAmerica = new Continent();
    northAmerica.setName("North America");
    northAmerica.setControlValue(40);

    Continent europe = new Continent();
    europe.setName("europe");
    europe.setControlValue(35);

    Country usa = new Country("usa");
    usa.addLink("canada");

    Country canada = new Country("canada");
    canada.addLink("usa");
    canada.addLink("mexico");
    canada.addLink("eu");

    Country mexico = new Country("mexico");
    mexico.addLink("canada");
    mexico.addLink("brazil");

    Country eu = new Country("eu");
    eu.addLink("canada");

    Country brazil = new Country("brazil");
    brazil.addLink("mexico");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("eu", eu);
    countries.put("brazil", brazil);

    List<Country> southAmericaCountries = new ArrayList<>();
    southAmericaCountries.add(mexico);
    southAmericaCountries.add(brazil);
    southAmerica.setCountries(southAmericaCountries);

    List<Country> northAmericaCountries = new ArrayList<>();
    northAmericaCountries.add(usa);
    northAmericaCountries.add(canada);
    northAmerica.setCountries(northAmericaCountries);

    List<Country> europeCountries = new ArrayList<>();
    europeCountries.add(eu);
    europe.setCountries(europeCountries);

    continents.put("South America", southAmerica);
    continents.put("North America", northAmerica);
    continents.put("europe", europe);

    Player p1 = new PlayerRegular("Player-Test-1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(eu);
    p1.setPlayerId(1);
    p1.setPlayerName("p1");
    p1.setPlayerStatus(PlayerStatus.Waiting);
    p1.setCountries(p1Countries);

    Player p2 = new PlayerRegular("Player-Test-2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2.setPlayerId(2);
    p2.setPlayerName("p2");
    p2.setPlayerStatus(PlayerStatus.Fortification);
    p2.setCountries(p2Countries);

    players.put(1, p2);
    players.put(2, p1);

    StartupPhase.calculateInitialArmies();
    StartupPhase.assignMinimumNumberOfArmiesToCountries();
  }
}
