package risk.game.grp.twenty.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;
import risk.game.grp.twenty.play.phase.impl.StartupPhase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameMapTest {

  @Before
  public void setUp() throws Exception {
    GameMap.resetGameMap();
    prepareGameMapMock();
  }

  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void testGetDominationView() {
    DominationView dominationView = GameMap.getDominationView();

    assertTrue(dominationView.getPlayersContinentOwnership().size() > 0);
    assertTrue(dominationView.getPlayersMapOwnership().size() > 0);
    assertTrue(dominationView.getPlayersTotalArmy().size() > 0);
  }

  @Test
  public void testResetGetDominationView() {
    DominationView dominationView = GameMap.getDominationView();
    dominationView.setPlayersContinentOwnership(new HashMap<>());
    dominationView.setPlayersMapOwnership(new HashMap<>());
    dominationView.setPlayersTotalArmy(new HashMap<>());

    assertEquals(dominationView.getPlayersContinentOwnership().size(), 0);
    assertEquals(dominationView.getPlayersMapOwnership().size(), 0);
    assertEquals(dominationView.getPlayersTotalArmy().size(), 0);
  }

  @Test
  public void testWriteReadObjectGameMap() throws IOException, ClassNotFoundException {

    Map<Integer, Player> players = GameMap.getPlayers();
    Map<String, Country> countries = GameMap.getCountries();
    Map<String, Continent> continents = GameMap.getContinents();

    //create a temp file
    File filePlayers = File.createTempFile("temp-file-players", ".tmp");
    File fileCountries = File.createTempFile("temp-file-countries", ".tmp");
    File fileContinents = File.createTempFile("temp-file-continents", ".tmp");

    FileOutputStream f = new FileOutputStream(filePlayers);
    ObjectOutputStream o = new ObjectOutputStream(f);

    // Write objects to file
    o.writeObject(players);

    f = new FileOutputStream(fileCountries);
    o = new ObjectOutputStream(f);

    o.writeObject(countries);

    f = new FileOutputStream(fileContinents);
    o = new ObjectOutputStream(f);

    o.writeObject(continents);

    o.close();
    f.close();

    System.out.println("Temp file : " + filePlayers.getAbsolutePath());

    FileInputStream fi = new FileInputStream(filePlayers);
    ObjectInputStream oi = new ObjectInputStream(fi);

    // Read objects
    Map<Integer, Player> playersFromFile = (Map<Integer, Player>) oi.readObject();

    fi = new FileInputStream(fileCountries);
    oi = new ObjectInputStream(fi);

    // Read objects
    Map<String, Country> countriesFromFile = (Map<String, Country>) oi.readObject();

    fi = new FileInputStream(fileContinents);
    oi = new ObjectInputStream(fi);

    // Read objects
    Map<String, Continent> continentsFromFile = (Map<String, Continent>) oi.readObject();

    filePlayers.deleteOnExit();
    fileCountries.deleteOnExit();
    fileContinents.deleteOnExit();
    oi.close();
    fi.close();

    assertEquals(players.size(), playersFromFile.size());
    assertEquals(countries.size(), countriesFromFile.size());
    assertEquals(continents.size(), continentsFromFile.size());
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

    Country peru = new Country("peru");
    peru.addLink("brazil");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("eu", eu);
    countries.put("brazil", brazil);
    countries.put("peru", peru);

    List<Country> southAmericaCountries = new ArrayList<>();
    southAmericaCountries.add(mexico);
    southAmericaCountries.add(brazil);
    southAmericaCountries.add(peru);
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
    p1Countries.add(peru);
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