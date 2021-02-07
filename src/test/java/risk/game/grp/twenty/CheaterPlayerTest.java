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
import risk.game.grp.twenty.model.player.behavior.PlayerCheater;

/**
 * Unit test for Fortification Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CheaterPlayerTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  Player p1;
  Player p2;

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
    eu.addLink("china");

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

    p1 = new PlayerCheater("P1");
    List<Country> p1Countries = new ArrayList<>();
    p1Countries.add(usa);
    p1Countries.add(canada);
    p1Countries.add(eu);
    p1.setPlayerId(1);
    p1.setPlayerStatus(PlayerStatus.Fortification);
    p1.setCountries(p1Countries);

    p2 = new PlayerCheater("p2");
    List<Country> p2Countries = new ArrayList<>();
    p2Countries.add(mexico);
    p2Countries.add(brazil);
    p2Countries.add(china);
    p2.setPlayerId(2);
    p2.setPlayerName("p2");
    p2.setPlayerStatus(PlayerStatus.Waiting);
    p2.setCountries(p2Countries);

    players.put(1, p1);
    players.put(2, p2);
  }

  @Test
  public void testSuccessCheaterFortification() throws GameException {
    int previousArmyUsa = GameMap.getCountries().get("usa").getArmies();
    int previousArmyCanada = GameMap.getCountries().get("canada").getArmies();
    p1.fortification(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(p1), null, null, null);

    assertEquals(GameMap.getCountries().get("usa").getArmies(), previousArmyUsa);
    assertEquals(GameMap.getCountries().get("canada").getArmies(), previousArmyCanada * 2);

    int previousArmyChina = GameMap.getCountries().get("china").getArmies();
    int previousArmyMexico = GameMap.getCountries().get("mexico").getArmies();
    p2.fortification(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(p2), null, null, null);

    assertEquals(GameMap.getCountries().get("mexico").getArmies(), previousArmyMexico * 2);
    assertEquals(GameMap.getCountries().get("china").getArmies(), previousArmyChina * 2);
  }

  @Test
  public void testSuccessCheaterReinforcement() throws GameException {
    p1.setPlayerStatus(PlayerStatus.Reinforcement);

    int previousArmyUsa = GameMap.getCountries().get("usa").getArmies();
    int previousArmyCanada = GameMap.getCountries().get("canada").getArmies();
    p1.positioningArmies(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(p1), null, null);

    assertEquals(GameMap.getCountries().get("usa").getArmies(), previousArmyUsa * 2);
    assertEquals(GameMap.getCountries().get("canada").getArmies(), previousArmyCanada * 2);

    int previousArmyChina = GameMap.getCountries().get("china").getArmies();
    int previousArmyMexico = GameMap.getCountries().get("mexico").getArmies();
    p2.positioningArmies(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(p2), null, null);

    assertEquals(GameMap.getCountries().get("mexico").getArmies(), previousArmyMexico * 2);
    assertEquals(GameMap.getCountries().get("china").getArmies(), previousArmyChina * 2);
  }

  @Test
  public void testSuccessCheaterAttack() throws GameException {
    p1.setPlayerStatus(PlayerStatus.Attack);
    int preAttackCountries = p1.getCountries().size();
    int preAttackCountriesP2 = p2.getCountries().size();

    p1.attack(GameMap.playerTurnNumberBasedOnPlayerReqularNumber(p1), null, null, null);

    assertEquals(p1.getCountries().size(), preAttackCountries + 2);
    assertEquals(p2.getCountries().size(), preAttackCountriesP2 - 2);

  }
}
