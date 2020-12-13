package mandelbrotset;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class OptionsPanel extends VBox {

  // Private control references for class methods
  private ColorPicker color2Picker;
  private ColorPicker color1Picker;

  private Spinner<Integer> iterationSpinner;
  private SizeControl sizeControl;

  private CoordinateControl coordControl;

  private AngleControl juliaControls;

  private RadioButton psychBt;

  // This button is *True* when mandelbrot is selected
  private FractalToggleButton typeSelectBt;

  // Buttons to render and save fractal
  private Button renderBt;
  private Button saveBt;

  /**
   * This class has all the settings to configure and save a fractal image.
   * Control values are accessed through public Property getter methods.
   */
  public OptionsPanel() {
    this.setAlignment(Pos.TOP_CENTER);
    this.setPadding(new Insets(10));
    this.setSpacing(5);
    this.setMinWidth(250);
    this.setMaxWidth(300);

    this.renderBt = new Button("(Re)render");

    Label infoLabel = new Label("Hover for tooltips");

    // Add render button and label to top of Vbox
    this.getChildren().addAll(renderBt, infoLabel);

    // Switches from Julia set to Mandelbrot set view
    this.typeSelectBt = new FractalToggleButton("Mandelbrot Set");
    this.typeSelectBt.setSelected(true);
    this.getChildren().add(this.typeSelectBt);

    // The angle controls for Julia Set is only visible when selected
    this.getChildren().add(this.juliaControls = new AngleControl());
    juliaControls.visibleProperty().bind(this.typeSelectBt.selectedProperty().not());

    // Add Color controls to Vbox
    this.getChildren().add(getColorControls());

    // Add iteration count and slider controls
    this.getChildren().addAll(getIterSpinner());

    // Add coordinate and size controls (and save global referance)
    this.getChildren().add(this.coordControl = new CoordinateControl());
    this.getChildren().add(this.sizeControl = new SizeControl());

    // Create a button to save the current image to a file
    HBox box2 = new HBox(5, getResetButton(), this.saveBt = new Button("Save Image"));
    box2.setAlignment(Pos.CENTER);
    this.getChildren().add(box2);

    // Help dialog for controls
    Button helpBt = new Button();
    helpBt.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/info.png"))));
    HelpDialog dialog = new HelpDialog(AlertType.INFORMATION);
    helpBt.setOnAction(e -> dialog.showAndWait());
    this.getChildren().add(helpBt);
  }

  /** Labeled spinner to select iterations */
  private Node getIterSpinner() {
    // Create a spinner to configure the maximum iterations
    iterationSpinner = new Spinner<Integer>(1, 1000, MainViewer.ITERATIONS);
    Label iterLabel = new Label("Iteration Count", iterationSpinner);
    iterLabel.setContentDisplay(ContentDisplay.BOTTOM);
    iterationSpinner.setEditable(true);
    return iterLabel;
  }

  /** A button to reset settings to defaults */
  private Button getResetButton() {
    Button resetBt = new Button("Reset");
    resetBt.setOnAction(e -> {
      if (typeSelectBt.isSelected()) {
        coordControl.radiusProperty().set(MainViewer.RADIUS);
        coordControl.xProperty().set(MainViewer.CENTER_X);
      } else {
        coordControl.radiusProperty().set(MainViewer.JULIA_RADIUS);
        coordControl.xProperty().set(MainViewer.JULIA_CENTER_X);
      }
      coordControl.yProperty().set(MainViewer.CENTER_Y);

      juliaControls.angleProperty().set(MainViewer.JULIA_ANGLE);

      sizeControl.valueProperty().set(MainViewer.SIZE);
      iterationSpinner.getValueFactory().setValue(MainViewer.ITERATIONS);
    });
    return resetBt;
  }

  /** Generates the controls related to color selection and generation mode */
  private VBox getColorControls() {
    VBox box = new VBox(5);
    box.setAlignment(Pos.CENTER);

    // Create Color pickers and labels
    color1Picker = new ColorPicker(Color.RED);
    color2Picker = new ColorPicker(Color.BLUE);

    // Add color pickers and labels to list of nodes
    Label l1 = new Label("Color 1", color1Picker);
    Label l2 = new Label("Color 2", color2Picker);
    l1.setContentDisplay(ContentDisplay.BOTTOM);
    l2.setContentDisplay(ContentDisplay.BOTTOM);
    box.getChildren().addAll(l1, l2);

    // Create radio buttons to control coloring modes
    // Use a toggle group to restrict one mode at a time
    ToggleGroup modeGroup = new ToggleGroup();

    // The range mode maps points far from the set with color 1,
    // and close to the set with color 2
    RadioButton normalMode = new RadioButton("Range Color 1 -> 2");
    normalMode.setTooltip(new Tooltip("A gradient based on how close to the set a point is"));
    normalMode.setSelected(true);
    normalMode.setToggleGroup(modeGroup);

    // Reset default colors when switching modes
    normalMode.setOnAction(e -> {
      color1Picker.setValue(Color.RED);
      color2Picker.setValue(Color.BLUE);
    });

    // This color mode was discovered by accident, but it looks cool
    // See MandelbrotImageView class for implementations
    psychBt = new RadioButton("Psychedelic Colors");
    psychBt.setTooltip(
        new Tooltip("\"Random\" bands of color, change color 1 and the iteration count for completely new renders"
            + "\n Looks best when Color 1 is *not* RED"));
    psychBt.setToggleGroup(modeGroup);

    // Set current colors to nice options when this mode is selected
    psychBt.setOnAction(e -> {
      color1Picker.setValue(Color.web("99b3ff"));
      color2Picker.setValue(Color.WHITE);
    });

    // Add radio buttons to list of nodes, and return
    box.getChildren().addAll(normalMode, psychBt);
    return box;
  }

  /** The action event when for changes that should trigger a re-render */
  public void setRenderAction(EventHandler<ActionEvent> e) {
    renderBt.setOnAction(e);

    class EnterHandler implements EventHandler<KeyEvent> {
      @Override
      public void handle(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER)
          renderBt.fire();
      }
    }
    EnterHandler eh = new EnterHandler();
    juliaControls.setOnKeyPressed(eh);
    iterationSpinner.setOnKeyPressed(eh);
    coordControl.setOnKeyPressed(eh);
  }

  /** The action event for the save button */
  public void setSaveAction(EventHandler<ActionEvent> e) {
    saveBt.setOnAction(e);
  }

  // Public property to be bound in MainViewer.java \\
  // --------------------------------------------- \\

  public ReadOnlyObjectProperty<Color> color1ValueProperty() {
    return this.color1Picker.valueProperty();
  }

  public ReadOnlyObjectProperty<Color> color2ValueProperty() {
    return this.color2Picker.valueProperty();
  }

  public ReadOnlyBooleanProperty psychSelectedProperty() {
    return this.psychBt.selectedProperty();
  }

  public ObjectProperty<Integer> iterationProperty() {
    return this.iterationSpinner.getValueFactory().valueProperty();
  }

  public DoubleProperty radiusProperty() {
    return this.coordControl.radiusProperty();
  }

  public double getRadius() {
    return this.coordControl.radiusProperty().getValue();
  }

  public void setRadius(double value) {
    this.coordControl.radiusProperty().set((value <= 4) ? value : 4);
  }

  public DoubleProperty centerXProperty() {
    return this.coordControl.xProperty();
  }

  public DoubleProperty centerYProperty() {
    return this.coordControl.yProperty();
  }

  public void setCenter(Point2D point) {
    this.coordControl.xProperty().set(point.getX());
    this.coordControl.yProperty().set(point.getY());
  }

  public ReadOnlyBooleanProperty typeSelectedProperty() {
    return this.typeSelectBt.selectedProperty();
  }

  public ReadOnlyDoubleProperty juliaAngleProperty() {
    return this.juliaControls.angleProperty();
  }

  public ReadOnlyObjectProperty<Integer> sizeProperty() {
    return this.sizeControl.valueProperty();
  }

}
