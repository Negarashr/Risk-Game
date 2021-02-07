package risk.game.grp.twenty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_MAP_PATH;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import risk.game.grp.twenty.endpoint.StartupPhaseEndpoint;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.play.phase.impl.StartupPhase;
import risk.game.grp.twenty.tools.InternalTools;

/**
 * Unit test for StartupPhase.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StartupPhaseTest {

  private String MAPS_RESOURCES_PATH = "src/test/resources/maps/";

  @Autowired
  private StartupPhaseEndpoint startupPhaseEndpoint;

  @Before
  public void before() {
    GameMap.resetGameMap();
  }

  @After
  public void after() {

  }

  @Test
  public void testExtractContinentsFromFileCorrectFile() {
    startupPhaseEndpoint.loadMap(null, "DEFAULT_MAP_PATH");
    startupPhaseEndpoint.assignCountries("3");
    assertTrue(GameMap.getPlayers().size() > 0);
  }

  @Test
  public void testFileWithNotConnectedGraph() {
    ResponseEntity<Object> response = startupPhaseEndpoint
        .loadMap(null, MAPS_RESOURCES_PATH + "countriesSimpleNewBad01.rmap");
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testFileWithMissedInformationInContinentLevel() {
    startupPhaseEndpoint.loadMap(null, MAPS_RESOURCES_PATH + "countriesSimpleNewBad02.rmap");
    ResponseEntity<Object> response = startupPhaseEndpoint
        .assignCountries("3");
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testFileWithMissedInformationInCountryLevel() {
    startupPhaseEndpoint.loadMap(null, MAPS_RESOURCES_PATH + "countriesSimpleNewBad03.rmap");
    ResponseEntity<Object> response = startupPhaseEndpoint
        .assignCountries("3");
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testCreateMapWithUserContent() throws IOException {
    startupPhaseEndpoint.loadMap(InternalTools.readFile(DEFAULT_MAP_PATH), null);
    startupPhaseEndpoint.assignCountries("2");
    assertTrue(GameMap.getPlayers().get(1).getArmy() > 0);

    startupPhaseEndpoint.loadMap(InternalTools.readFile(DEFAULT_MAP_PATH), null);
    startupPhaseEndpoint.assignCountries("3");
    assertTrue(GameMap.getPlayers().get(1).getArmy() > 0);

    startupPhaseEndpoint.loadMap(InternalTools.readFile(DEFAULT_MAP_PATH), null);
    startupPhaseEndpoint.assignCountries("4");
    assertTrue(GameMap.getPlayers().get(1).getArmy() > 0);

    startupPhaseEndpoint.loadMap(InternalTools.readFile(DEFAULT_MAP_PATH), null);
    startupPhaseEndpoint.assignCountries("5");
    assertTrue(GameMap.getPlayers().get(1).getArmy() > 0);

    startupPhaseEndpoint.loadMap(InternalTools.readFile(DEFAULT_MAP_PATH), null);
    startupPhaseEndpoint.assignCountries("6");
    assertTrue(GameMap.getPlayers().get(1).getArmy() > 0);

  }

  @Test
  public void testGetExistingMaps() throws IOException {
    ResponseEntity<Object> response = startupPhaseEndpoint.getExistingMaps();
    assertEquals(response.getStatusCode(), HttpStatus.OK);

  }

  @Test
  public void testUUID() {
    StartupPhase startupPhase = new StartupPhase();
    String uuid = startupPhase.createGameMap();
    assertNotNull(startupPhase.getGame(uuid));
    assertNull(startupPhase.getGame("QQ"));
    assertTrue(startupPhase.checkGame(uuid));
    assertFalse(startupPhase.checkGame("QQ"));
  }
}
