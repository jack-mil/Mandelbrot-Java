package mandelbrotset;

import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class OptionsPane extends VBox {

  public ReadOnlyObjectProperty<Color> color1Property;
  public ReadOnlyObjectProperty<Color> color2Property;

  public ReadOnlyObjectProperty<Integer> iterationsProperty;
  
  public ReadOnlyBooleanProperty psychModeProperty;
  
  public Button render;
  public Button save;

  public OptionsPane() {
    this.setAlignment(Pos.TOP_CENTER);
    this.setPadding(new Insets(10));
    this.setSpacing(30);

    render = new Button("(Re)render");

    Label infoLabel = new Label("Hover for tooltips");

    // Add render button and label to top of Vbox
    this.getChildren().addAll(render, infoLabel);

    // Add Color controls to Vbox
    this.getChildren().addAll(getColorControls());

    // Add iteration count and slider controls
    this.getChildren().addAll(getIterControls());

    // Create a button to save the current image to a file
    save = new Button("Save Image");
    this.getChildren().add(save);
  }

  private ArrayList<Node> getIterControls() {
    ArrayList<Node> nodes = new ArrayList<>();
    // Create a spinner to configure the maximum iterations
    Spinner<Integer> iterSpinner = new Spinner<>(1, 1000, 30);
    iterationsProperty = iterSpinner.valueProperty();
    Label iterLabel = new Label("Iteration Count", iterSpinner);
    iterSpinner.setEditable(true);
    Collections.addAll(nodes, iterSpinner, iterLabel);
    return nodes;
  }

  /**
   * Generates the controls related to color selection and generation mode Options
   * are bound to MandelbrotPane Properties
   * @return List of nodes
   */
  private ArrayList<Node> getColorControls() {

    ArrayList<Node> nodes = new ArrayList<>();

    // Create Color pickers and labels
    ColorPicker color1Picker = new ColorPicker(Color.RED);
    ColorPicker color2Picker = new ColorPicker(Color.BLUE);

    color1Property = color1Picker.valueProperty();
    color2Property = color2Picker.valueProperty();

    // When any colors are changed, invoke the render button action
    color1Picker.setOnAction(e -> render.fire());
    color2Picker.setOnAction(e -> render.fire());

    // Add color pickers and labels to list of nodes
    Collections.addAll(nodes, new Label("Color 1", color1Picker), new Label("Color 2", color2Picker));

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
    RadioButton psych = new RadioButton("Psychedelic Colors");
    psychModeProperty = psych.selectedProperty();
    psych.setTooltip(new Tooltip(
        "\"Random\" bands of color, change color 1 and the iteration count for completely new renders"
            + "\n Looks best when Color 1 is *not* RED"));
    psych.setToggleGroup(group);

    // Set current colors to nice options when this mode is selected
    psych.setOnAction(e -> {
      color1Picker.setValue(Color.web("99b3ff"));
      color2Picker.setValue(Color.WHITE);
    });

    // Add radio buttons to list of nodes, and return
    Collections.addAll(nodes, range, psych);
    return nodes;
  }
}
