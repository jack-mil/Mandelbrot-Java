package mandelbrotset;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

/** The HelpDialog class displays some information in a popup window */
public class HelpDialog extends Alert {

  public HelpDialog(AlertType type) {
    super(type);
    setTitle("Controls Help");
    setHeaderText(null);
    initStyle(StageStyle.UTILITY);
    setContentText(
      "- Use RMB to pick a new center coordinate\n" +
      "- Scroll wheel will zoom in/out\n" +
      "- Use UP and DOWN arrows to increase/decrease the iteration count\n" +
      "- If the generated image is larger than the window, LMB to drag and pan the view\n");
  }
  
}
