package mandelbrotset;

import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class OptionsPane extends VBox {

  private ColorPicker color2Picker;
  private ColorPicker color1Picker;

  private Spinner<Integer> iterationSpinner;

  private RadioButton psychButton;

  private RadioToggleButton mandButton;
  private RadioToggleButton juliaButton;

  private Button render;
  private Button save;

  public ReadOnlyObjectProperty<Color> color1ValueProperty() {
    return this.color1Picker.valueProperty();
  }

  public ReadOnlyObjectProperty<Color> color2ValueProperty() {
    return this.color2Picker.valueProperty();
  }
  
  public ReadOnlyBooleanProperty psychSelectedProperty() {
    return this.psychButton.selectedProperty();
  }

  public ReadOnlyObjectProperty<Integer> iterationValueProperty() {
    return this.iterationSpinner.valueProperty();
  }

  public boolean isJuliaSelected() {
    return juliaButton.isSelected();
  }

  public void setRenderAction(EventHandler<ActionEvent> e) {
    render.setOnAction(e);
  }

  public void setSaveAction(EventHandler<ActionEvent> e) {
    save.setOnAction(e);
  }

  public OptionsPane() {
    this.setAlignment(Pos.TOP_CENTER);
    this.setPadding(new Insets(15));
    this.setSpacing(10);
    this.setMinWidth(250);

    this.render = new Button("(Re)render");

    Label infoLabel = new Label("Hover for tooltips");

    // Add render button and label to top of Vbox
    this.getChildren().addAll(render, infoLabel);

    // Add Color controls to Vbox
    this.getChildren().addAll(getColorControls());

    // Add iteration count and slider controls
    this.getChildren().addAll(getIterControls());

    this.getChildren().add(getTypeButtons());

    // Create a button to save the current image to a file
    this.save = new Button("Save Image");
    this.getChildren().add(save);
  }

  private HBox getTypeButtons() {
    ToggleGroup typeGroup = new ToggleGroup();
    mandButton = new RadioToggleButton("Mandelbrot Set");
    juliaButton = new RadioToggleButton("Julia Set");
    mandButton.setToggleGroup(typeGroup);
    juliaButton.setToggleGroup(typeGroup);

    mandButton.setSelected(true);

    HBox hbox = new HBox(10, mandButton, juliaButton);
    hbox.setAlignment(Pos.CENTER);
    return hbox;
  }

  private ArrayList<Node> getIterControls() {
    ArrayList<Node> nodes = new ArrayList<>();
    // Create a spinner to configure the maximum iterations
    iterationSpinner = new Spinner<>(1, 1000, 30);
    Label iterLabel = new Label("Iteration Count", iterationSpinner);
    iterLabel.setContentDisplay(ContentDisplay.BOTTOM);
    iterationSpinner.setEditable(true);
    Collections.addAll(nodes, iterationSpinner, iterLabel);
    return nodes;
  }

  /**
   * Generates the controls related to color selection and generation mode Options
   * are bound to MandelbrotPane Properties
   * 
   * @return List of nodes
   */
  private ArrayList<Node> getColorControls() {

    ArrayList<Node> nodes = new ArrayList<>();

    // Create Color pickers and labels
    color1Picker = new ColorPicker(Color.RED);
    color2Picker = new ColorPicker(Color.BLUE);

    // When any colors are changed, invoke the render button action
    color1Picker.setOnAction(e -> render.fire());
    color2Picker.setOnAction(e -> render.fire());

    // Add color pickers and labels to list of nodes
    Label l1 = new Label("Color 1", color1Picker);
    Label l2 = new Label("Color 2", color2Picker);
    l1.setContentDisplay(ContentDisplay.BOTTOM);
    l2.setContentDisplay(ContentDisplay.BOTTOM);
    Collections.addAll(nodes, l1, l2);

    // Create radio buttons to control coloring modes
    // Use a toggle group to restrict one mode at a time
    ToggleGroup group = new ToggleGroup();

    // The range mode maps points far from the set with color 1,
    // and close to the set with color 2
    RadioButton range = new RadioButton("Range Color 1 -> 2");
    range.setTooltip(new Tooltip("A gradient based on how close to the set a point is"));
    range.setSelected(true);
    range.setToggleGroup(group);

    // Reset default colors when switching modes
    range.setOnAction(e -> {
      color1Picker.setValue(Color.RED);
      color2Picker.setValue(Color.BLUE);
    });

    // This color mode was discovered by accident, but it looks cool
    // See MandelbrotImageView class for implementations
    psychButton = new RadioButton("Psychedelic Colors");
    psychButton.setTooltip(
        new Tooltip("\"Random\" bands of color, change color 1 and the iteration count for completely new renders"
            + "\n Looks best when Color 1 is *not* RED"));
    psychButton.setToggleGroup(group);

    // Set current colors to nice options when this mode is selected
    psychButton.setOnAction(e -> {
      color1Picker.setValue(Color.web("99b3ff"));
      color2Picker.setValue(Color.WHITE);
    });

    // Add radio buttons to list of nodes, and return
    Collections.addAll(nodes, range, psychButton);
    return nodes;
  }
}
