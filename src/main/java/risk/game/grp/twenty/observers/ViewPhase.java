package risk.game.grp.twenty.observers;


import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import risk.game.grp.twenty.model.DominationView;
import risk.game.grp.twenty.model.GameMap;
import risk.game.grp.twenty.model.abstractModel.Player;

/**
 * This Class subscribe for changes inside <em>Player</em> upon a change in player status it
 * notifies and will notifies the web socket which register to it for updating UI
 *
 * @author ali
 * @see java.util.Observer
 */
@Service
public class ViewPhase implements Observer {

  private final static Logger LOGGER = LoggerFactory.getLogger(ViewPhase.class);

  private static Player currentPlayer;
  private static DominationView currentDominationView;

  @Autowired
  private SimpMessagingTemplate template;

  public static Player getCurrentPlayer() {
    return currentPlayer;
  }

  public static DominationView getCurrentDominationView() {
    return currentDominationView;
  }

  public static void resetViews() {
    currentPlayer = null;
    currentDominationView = null;
  }

  /**
   * This method is called whenever the observed object is changed. An application calls an
   * <tt>Observable</tt> object's
   * <code>notifyObservers</code> method to have all the object's
   * observers notified of the change.
   *
   * @param o the observable object.
   * @param arg an argument passed to the <code>notifyObservers</code>
   */
  @Override
  public void update(Observable o, Object arg) {
    currentPlayer = (Player) o;
    currentDominationView = GameMap.getDominationView();

    LOGGER.info(currentPlayer.getPlayerLog().toString());
    LOGGER.info(currentDominationView.toString());
    LOGGER.info(
        "-----------------------------------------------------------------------------------");
  }

  /**
   * send the data to web socket endpoint which register for player view changes
   *
   * @param player player which its status has been changed
   * @throws Exception if fail to push notification or network face run time issues.
   */
  public void pushViewPhaseData(Player player) throws Exception {
    template.convertAndSend("/topic/viewPhase", player);
  }


  /**
   * send the data to web socket endpoint which register for domination view changes
   *
   * @param dominationView player which its status has been changed
   * @throws Exception if fail to push notification or network face run time issues.
   */

  public void pushDominationView(DominationView dominationView) throws Exception {
    template.convertAndSend("/topic/dominationView", dominationView);
  }
}

