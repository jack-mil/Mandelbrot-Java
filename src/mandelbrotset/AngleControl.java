package mandelbrotset;

import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** A syncronized slider and spinner to control the Julia Set angle parameter */
public class AngleControl extends VBox {

  private Spinner<Double> angleSpinner;
  private Slider angleSlider;

  public AngleControl() {
    // Create this VBox with spacing 10
    super(10);

    // Spinner values from 0 to 2π
    angleSpinner = new Spinner<Double>(0.0, 2*Math.PI, MainViewer.JULIA_ANGLE, 0.01);
    angleSpinner.setEditable(true);

    // Angle Slider has custom labels
    angleSlider = getSlider();

    // Add listeners to synchronize the two controls
    // Hacky workaround due to Spinner's value factory implementation
    angleSlider.valueProperty().addListener(ov -> angleSpinner.getValueFactory().setValue(angleSlider.getValue()));
    angleSpinner.valueProperty().addListener(ov -> angleSlider.setValue(angleSpinner.getValue()));

    Label l1 = new Label("Angle 0 - 2π");

    this.getChildren().addAll(l1, angleSlider, angleSpinner);
  }

  private Slider getSlider() {
    // Slider from 0 to 2π
    Slider slider = new Slider(0.0, 2 * Math.PI, MainViewer.JULIA_ANGLE);
    slider.setMajorTickUnit(Math.PI);
    slider.setMinorTickCount(1);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);

    // This little hack is the only way I could find to customize the slider tickmark labels
    slider.setLabelFormatter(new StringConverter<Double>() {
      @Override
      public String toString(Double n) {
        if (n == 0) return "0";
        if (n == Math.PI) return "π";
        return "2π";
      }

      @Override
      public Double fromString(String s) {
        switch (s) {
          case "0": 
          return 0d;
          case "π":
          return Math.PI;
          default:
          return 2 * Math.PI;
          }
        }
    });
    return slider;
  }

  /** Angle Property getter */
  public DoubleProperty angleProperty() {
    return this.angleSlider.valueProperty();
  }
}
