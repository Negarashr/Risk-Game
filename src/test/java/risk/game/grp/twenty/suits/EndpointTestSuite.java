package risk.game.grp.twenty.suits;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import risk.game.grp.twenty.AggresivePlayerTest;
import risk.game.grp.twenty.AttackPhaseTest;
import risk.game.grp.twenty.BenevolentPlayerTest;
import risk.game.grp.twenty.CheaterPlayerTest;
import risk.game.grp.twenty.FortificationPhaseTest;
import risk.game.grp.twenty.RandomPlayerTest;
import risk.game.grp.twenty.ReinforcementPhaseTest;
import risk.game.grp.twenty.SaveLoadPhaseTest;
import risk.game.grp.twenty.SkipPhaseTest;
import risk.game.grp.twenty.StartupPhaseTest;
import risk.game.grp.twenty.TournamentPhaseTest;
import risk.game.grp.twenty.ViewPhaseTest;
import risk.game.grp.twenty.model.GameMapTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AggresivePlayerTest.class,
    AttackPhaseTest.class,
    BenevolentPlayerTest.class,
    CheaterPlayerTest.class,
    FortificationPhaseTest.class,
    RandomPlayerTest.class,
    ReinforcementPhaseTest.class,
    SaveLoadPhaseTest.class,
    SkipPhaseTest.class,
    StartupPhaseTest.class,
    TournamentPhaseTest.class,
    ViewPhaseTest.class,
    GameMapTest.class
})
public class EndpointTestSuite {

}
