package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
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
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;
import risk.game.grp.twenty.play.phase.impl.SaveLoadPhase;

/**
 * Unit test for Save load Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SaveLoadPhaseTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  @Autowired
  private SaveLoadPhase saveLoadPhase;

  private static void prepareGameMapMock() {
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

  }

  @Before
  public void setup() {
    GameMap.resetGameMap();

    prepareGameMapMock();
  }

  @Test
  public void testSaveGame() throws Exception {
    saveLoadPhase.save();
    assertTrue(saveLoadPhase.getExistingSavedGames("saved_games/").size() > 0);
  }

  @Test
  public void testLoadGame() throws Exception {
    Map<Integer, Player> playersBeforeSave = new HashMap<>(GameMap.getPlayers());
    String savePath = saveLoadPhase.save();
    saveLoadPhase.load(savePath);
    Map<Integer, Player> playersAfterSave = new HashMap<>(GameMap.getPlayers());

    assertEquals(playersBeforeSave.size(), playersAfterSave.size());
  }

}
