package mandelbrotset;

import java.text.DecimalFormat;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** These Text field controls update when the user Right clicks or scrolls,
 * and represent the area in the complex plane to draw. 
 * Precise values can be entered here if a specific region is desired
 */
public class CoordinateControl extends VBox {

  // The NumberTextField class is from a solution I found online to limit 
  // Text fields to specific number format
  private NumberTextField imFld, realFld, radiusFld;

  public CoordinateControl() {
    // These numbers can get very small, so use scientific notation
    DecimalFormat df = new DecimalFormat("0.0######E00");

    // Add, label and format 3 text fields
    radiusFld = new NumberTextField(MainViewer.RADIUS, df);
    realFld = new NumberTextField(MainViewer.CENTER_X, df);
    realFld.setPrefWidth(100);

    imFld = new NumberTextField(MainViewer.CENTER_Y, df);
    imFld.setPrefWidth(100);

    Label l1 = new Label("View Radius", radiusFld);
    l1.setContentDisplay(ContentDisplay.BOTTOM);

    Label l2 = new Label("X", realFld);
    Label l3 = new Label("Y", imFld);
    HBox box1 = new HBox(2, l2, l3);
    box1.setAlignment(Pos.CENTER);

    Label l4 = new Label("Center");
    l4.setContentDisplay(ContentDisplay.BOTTOM);
    l4.setGraphic(box1);

    this.setSpacing(2);
    this.setAlignment(Pos.CENTER);
    this.getChildren().addAll(l4, l1);
  }

  // Property getters made accessible for OptionsPane instance to use \\
  // ---------------------------------------------------------------- \\

  public DoubleProperty radiusProperty() {
    return this.radiusFld.numberProperty();
  }

  public DoubleProperty yProperty() {
    return this.imFld.numberProperty();
  }

  public DoubleProperty xProperty() {
    return this.realFld.numberProperty();
  }

}