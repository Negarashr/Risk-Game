package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import risk.game.grp.twenty.endpoint.AttackPhaseEndpoint;
import risk.game.grp.twenty.game.exception.GameException;
import risk.game.grp.twenty.model.AttackResult;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;

/**
 * Unit test for attack phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AttackPhaseTest {

  @Rule
  public ExpectedException exc = ExpectedException.none();

  @Autowired
  private AttackPhaseEndpoint attackPhaseEndpoint;

  /**
   * Method to prepare the test
   */
  @Before
  public void setup() {
    Map<String, Country> countries = GameMap.getCountries();
    Map<Integer, Player> players = GameMap.getPlayers();

    Country usa = new Country("usa");
    usa.setArmies(20);
    usa.addLink("canada");
    usa.addLink("mexico");
    usa.addLink("bermuda");

    Country canada = new Country("canada");
    canada.setArmies(1);
    canada.addLink("usa");

    Country mexico = new Country("mexico");
    mexico.setArmies(2);
    mexico.addLink("usa");

    Country bermuda = new Country("bermuda");
    bermuda.setArmies(1);
    bermuda.addLink("usa");

    countries.put("usa", usa);
    countries.put("canada", canada);
    countries.put("mexico", mexico);
    countries.put("bermuda", bermuda);

    Player player1 = new PlayerRegular("Player-1");
    List<Country> player1Countries = new ArrayList<>();
    player1Countries.add(usa);
    player1Countries.add(bermuda);
    player1.setPlayerId(1);
    player1.setPlayerStatus(PlayerStatus.Attack);
    player1.setCountries(player1Countries);

    Player player2 = new PlayerRegular("Player-2");
    List<Country> player2Countries = new ArrayList<>();
    player2Countries.add(canada);
    player2.setPlayerId(2);
    player2.setPlayerStatus(PlayerStatus.Waiting);
    player2.setCountries(player2Countries);

    Player player3 = new PlayerRegular("Player-3");
    List<Country> player3Countries = new ArrayList<>();
    player3Countries.add(mexico);
    player3.setPlayerId(2);
    player3.setPlayerName("Player-3");
    player3.setPlayerStatus(PlayerStatus.Waiting);
    player3.setCountries(player3Countries);

    players.put(1, player3);
    players.put(2, player2);
    players.put(3, player1);
  }

  /**
   * Method to test valid attack
   */
  @Test
  public void testSuccessfulAttack() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attack("1", "usa", "canada", "3");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertTrue(GameMap.getCountries().get("canada").getArmies() > 0);
    assertTrue(GameMap.getCountries().get("usa").getArmies() > 0);
    assertFalse(GameMap.getPlayer("usa").checkWinner());
  }

  /**
   * Method to test invalid attack, attacking country is not connected to defending country
   */
  @Test
  public void testNotConnectedCountries() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint
        .attack("2", "mexico", "canada", "1");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test invalid attack, player attacks itself
   */
  @Test
  public void testSamePlayer() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attack("1", "usa", "bermuda", "1");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test invalid attack, player has not enough armies
   */
  @Test
  public void testNotEnoughArmies() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attack("1", "usa", "canada", "40");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test invalid attack, player is not in attacking phase
   */
  @Test
  public void testNotInAttackingPhase() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attack("3", "mexico", "usa", "2");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test valid attack allout
   */
  @Test
  public void testSuccessfulAttackAllOut() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attackAllOut("1", "usa", "mexico");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertTrue(GameMap.getCountries().get("usa").getArmies() > 0);
    assertTrue(GameMap.getCountries().get("mexico").getArmies() > 0);
  }

  /**
   * Method to test invalid attack all out, player attacks itself
   */
  @Test
  public void testSamePlayerAllOut() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attackAllOut("1", "usa", "bermuda");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test invalid attack all out, attacking country is not connected to defending country
   */
  @Test
  public void testNotConnectedCountriesAllOut() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint
        .attackAllOut("2", "mexico", "canada");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Method to test invalid attack all out, player is not in attacking phase
   */
  @Test
  public void testNotInAttackingPhaseAllOut() {
    ResponseEntity<Object> responseEntity = attackPhaseEndpoint.attackAllOut("3", "mexico", "usa");
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testValidMoveAfterConquering() throws GameException {
    boolean conquered = false;
    while ((!conquered) && (GameMap.getCountries().get("usa").getArmies() > 1)) {
      int usaArmies = GameMap.getCountries().get("usa").getArmies() - 1;
      int canadaArmies = GameMap.getCountries().get("canada").getArmies();
      AttackResult result = GameMap.getPlayers().get(1)
          .attack("1", "usa", "canada", "" + usaArmies);
      int usaActualArmies = GameMap.getCountries().get("usa").getArmies() - 1;
      int canadaActualArmies = GameMap.getCountries().get("canada").getArmies();
      assertEquals(result.getInitialAttackerArmy(), usaArmies);
      assertEquals(result.getInitialDefenderArmy(), canadaArmies);
      if (result.getConqueredCountry() == null) {
        assertEquals(result.getSurvivedAttackerArmy(), usaActualArmies);
        assertEquals(result.getSurvivedDefenderArmy(), canadaActualArmies);
        assertEquals(result.getDeadAttackerArmy(), usaArmies - usaActualArmies);
        assertEquals(result.getDeadDefenderArmy(), canadaArmies - canadaActualArmies);
      } else {
        conquered = true;
        assertEquals(result.getSurvivedDefenderArmy(), 0);
        assertEquals(result.getDeadDefenderArmy(), canadaArmies);
        assertEquals(result.getSurvivedAttackerArmy(), usaActualArmies + canadaActualArmies);
        assertEquals(result.getDeadAttackerArmy(),
            usaArmies - usaActualArmies - canadaActualArmies);
      }
    }
  }

  @Test
  public void testEndOfGame() {
    Player player = GameMap.getPlayers().get(1);
    assertFalse(player.checkWinner());
    for (Player p : GameMap.getPlayers().values()) {
      if (p != player) {
        p.setPlayerStatus(PlayerStatus.Eliminated);
      }
    }
    assertTrue(player.checkWinner());
  }
}
