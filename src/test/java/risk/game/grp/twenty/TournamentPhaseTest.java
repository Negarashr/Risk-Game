package risk.game.grp.twenty;

import static risk.game.grp.twenty.constant.GameConstant.DEFAULT_MAP_PATH;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerType;
import risk.game.grp.twenty.model.Tournament;
import risk.game.grp.twenty.model.TournamentResult;
import risk.game.grp.twenty.play.phase.impl.TournamentPhase;

/**
 * Unit test for Fortification Phase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TournamentPhaseTest {


  @Rule
  public ExpectedException exc = ExpectedException.none();
  private Tournament tournament;

  @Autowired
  private TournamentPhase tournamentPhase;

  @Before
  public void setup() {
    GameMap.resetGameMap();
    tournament = new Tournament();
    tournament.getTournamentMaps().add(DEFAULT_MAP_PATH);
    tournament.getTournamentMaps().add("game_maps/countriesSimple01.rmap");

    tournament.setNumberOfTurns(50);
    tournament.setNumberOfGames(5);
  }

  @Test
  public void testTournamentCompletely() throws Exception {
    tournament.getTournamentMaps().add(null);
    tournament.getPlayerTypes().add(PlayerType.Aggressive.name());
    tournament.getPlayerTypes().add(PlayerType.Cheater.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Random.name());

    tournamentPhase.start(tournament);

    Assert.assertEquals(GameMap.getPlayers().size(), 4);
    Assert.assertTrue(GameMap.getCountries().size() > 0);
    Assert.assertTrue(GameMap.getContinents().size() > 0);
  }

  @Test
  public void testAggressiveVictory() throws Exception {
    tournament.getPlayerTypes().add(PlayerType.Aggressive.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournamentPhase.start(tournament);
    List<TournamentResult> tournamentResult = tournamentPhase.start(tournament);
    for (TournamentResult result : tournamentResult) {
      Assert.assertEquals("Aggressive-Player-1", result.getWinner());
    }
  }

  @Test
  public void testBenevolentDraw() throws Exception {
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    List<TournamentResult> tournamentResult = tournamentPhase.start(tournament);
    for (TournamentResult result : tournamentResult) {
      Assert.assertEquals("Draw", result.getWinner());
    }
  }

  @Test
  public void testCheaterVictory() throws Exception {
    tournament.getPlayerTypes().add(PlayerType.Cheater.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournamentPhase.start(tournament);
    List<TournamentResult> tournamentResult = tournamentPhase.start(tournament);
    for (TournamentResult result : tournamentResult) {
      Assert.assertEquals("Cheater-Player-1", result.getWinner());
    }
  }

  @Test
  public void testRandomDraw() throws Exception {
    tournament.getPlayerTypes().add(PlayerType.Random.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournamentPhase.start(tournament);
    List<TournamentResult> tournamentResult = tournamentPhase.start(tournament);
    for (TournamentResult result : tournamentResult) {
      Assert.assertEquals("Draw", result.getWinner());
    }
  }

  @Test
  public void testSingleModeWithoutHuman() throws Exception {
    tournament.getTournamentMaps().add(null);
    tournament.getPlayerTypes().add(PlayerType.Aggressive.name());
    tournament.getPlayerTypes().add(PlayerType.Cheater.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Random.name());

    tournamentPhase.startSingleMode(tournament);

    Assert.assertTrue(GameMap.getContinents().size() > 0);
    Assert.assertTrue(GameMap.getCountries().size() > 0);
    Assert.assertTrue(GameMap.getPlayers().size() > 0);
    Assert.assertTrue(
        GameMap.getCountries().values().stream().anyMatch(country -> country.getArmies() > 0));
  }

  @Test
  public void testSingleModeWithHuman() throws Exception {
    tournament.getTournamentMaps().add(null);
    tournament.getPlayerTypes().add(PlayerType.Aggressive.name());
    tournament.getPlayerTypes().add(PlayerType.Human.name());
    tournament.getPlayerTypes().add(PlayerType.Benevolent.name());
    tournament.getPlayerTypes().add(PlayerType.Random.name());

    tournamentPhase.startSingleMode(tournament);

    Assert.assertTrue(GameMap.getContinents().size() > 0);
    Assert.assertTrue(GameMap.getCountries().size() > 0);
    Assert.assertTrue(GameMap.getPlayers().size() > 0);
    Assert.assertTrue(
        GameMap.getCountries().values().stream().anyMatch(country -> country.getArmies() > 0));
  }
}
