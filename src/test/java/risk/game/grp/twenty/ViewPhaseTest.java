package risk.game.grp.twenty;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import risk.game.grp.twenty.model.Continent;
import risk.game.grp.twenty.model.Country;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.PlayerStatus;
import risk.game.grp.twenty.model.abstractModel.Player;
import risk.game.grp.twenty.model.player.behavior.PlayerRegular;
import risk.game.grp.twenty.observers.ViewPhase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ViewPhaseTest {

  @Autowired
  private ViewPhase viewPhase;

  private Player player;

  @Before
  public void before() {

    Continent asia = new Continent();
    Country ir = new Country("IRAN");
    ir.setCountryId(1);

    List<Country> countryList = new ArrayList<>();
    countryList.add(ir);
    asia.setCountries(countryList);

    player = new PlayerRegular("Test-Player");
    player.setPlayerStatus(PlayerStatus.Waiting);
    player.setCard(10);
    player.setArmy(20);
    player.setPlayerId(1);
    player.setPlayerName("TestName");
    player.setCountries(countryList);
  }

  @After
  public void after() {

  }

  @Test
  public void testPhaseView() {
    player.setPlayerStatus(PlayerStatus.Reinforcement);
    viewPhase.update(player, null);
    assertNotNull(GameMap.getDominationView());

  }

}
