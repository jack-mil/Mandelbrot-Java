package mandelbrotset;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class PanScrollPane extends ScrollPane {
  // private final double SCALE_DELTA = 8;

  // private SimpleDoubleProperty scale;

  public PanScrollPane(Node node) {
    this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    // this.setPrefViewportWidth(800);
    // this.setPrefViewportHeight(800);
    this.setPannable(true);
    this.setContent(node);
  }
}
