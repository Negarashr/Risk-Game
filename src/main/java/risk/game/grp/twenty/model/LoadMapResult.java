package risk.game.grp.twenty.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is the Model of <em>LoadMapResult</em> Object in Game. after user requesting for Map
 * its is either already existing maps which place in <em>existingMaps</em> to show to user to
 * choose among them. or it is s String of map content created by user, in response if content is
 * valid name of created and saved file will return to user.
 *
 * @author Team 20
 */
public class LoadMapResult implements Serializable {

  private static final long serialVersionUID = 1L;
  private String fileName;
  private String creationTime = String.valueOf(new Date());
  private List<String> existingMaps = new ArrayList<>();
  private String requestedMapContent;

  public LoadMapResult() {
  }

  public LoadMapResult(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public List<String> getExistingMaps() {
    return existingMaps;
  }

  public void setExistingMaps(List<String> existingMaps) {
    this.existingMaps = existingMaps;
  }

  public String getRequestedMapContent() {
    return requestedMapContent;
  }

  public void setRequestedMapContent(String requestedMapContent) {
    this.requestedMapContent = requestedMapContent;
  }
}
