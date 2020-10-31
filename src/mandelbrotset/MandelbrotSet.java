package mandelbrotset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

// Jackson Miller
// COSC2103 Computer Science 2
// 2020-10-31
// CS2 Art Project: Static Mandelbrot Fractal Render.
// This program displays a procedurally generated Mandelbrot Fractal with
// basic settings like iteration counts and coloring mode.

/** Main Class */
public class MandelbrotSet extends Application {

    // The Fractal Image View to be displayed. Accessible to private Methods 
    private MandelbrotImageView fractalIV = new MandelbrotImageView();

    private Stage primaryStage;

    /** The main class only exists to run from within the IDE */
    public static void main(String[] args) { launch(args); }

    /**
     * The start() method is run automatically by the Application superclass Here we
     * setup the JavaFX UI to display a static Mandelbrot Fractal
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Stage settings, with application icon
        primaryStage.setTitle("CS2: Mandelbrot Set");
        primaryStage.getIcons().add(new Image(MandelbrotSet.class.getResourceAsStream("icon.png")));
        primaryStage.setResizable(false);

        /* --- Options and controls (right) --- */
        VBox options = getOptionsPanel();

        /* --- Image area (left) --- */
        // Add signature
        Text name = new Text("Jackson Miller");
        name.setFont(new Font(20));
        name.setStroke(Color.BLACK);
        name.setFill(Color.BLACK);

        // Create StackPane to layer name and fractal image
        StackPane.setAlignment(name, Pos.BOTTOM_RIGHT);
        StackPane imageGroup = new StackPane(fractalIV, name);

        // Add image and options to main Scene
        HBox mainPane = new HBox();
        mainPane.setPadding(new Insets(10));
        mainPane.getChildren().addAll(imageGroup, options);

        // Render the Fractal Image
        fractalIV.render();

        // Create the main scene and display the application window
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    /**
     * Make the right hand controls and options
     * @return VBox containing menu controls
     */
    private VBox getOptionsPanel() {
        /* --- Options area (right) --- */
        VBox options = new VBox();
        options.setAlignment(Pos.TOP_CENTER);
        options.setPadding(new Insets(10));
        options.setSpacing(10);

        // Bind button events to MandelbrotPane instance methods
        Button render = new Button("(Re)render");
        render.setOnAction(e -> fractalIV.render());

        Label infoLabel = new Label("Hover for tooltips");

        // Add render button and label to top of Vbox
        options.getChildren().addAll(render, infoLabel);

        // Add Color controls to Vbox
        options.getChildren().addAll(getColorControls());
        
        // Create a spinner to configure the maximum iterations
        Spinner<Integer> iterSpinner = new Spinner<>(1, 1000, 30);
        Label iterLabel = new Label("Iteration Count", iterSpinner);
        iterSpinner.setEditable(true);
        fractalIV.iterationsProperty().bind(iterSpinner.valueProperty());
        options.getChildren().addAll(iterSpinner, iterLabel);

        // Create a button to save the current image to a file
        // Save method defined below
        Button save = new Button("Save Image");
        save.setOnAction(e -> saveImageToFile(fractalIV.getImage()));
        options.getChildren().add(save);

        return options;
    }

    /**
     * Generates the controls related to color selection and generation mode
     * Options are bound to MandelbrotPane Properties
     * @return List of nodes to add
     */
    private ArrayList<Node> getColorControls() {

        ArrayList<Node> nodes = new ArrayList<>();

        // Create Color pickers and labels
        ColorPicker color1 = new ColorPicker(Color.RED);
        ColorPicker color2 = new ColorPicker(Color.BLUE);
        // The chosen color is bound to Image properties, and renders when changed
        fractalIV.outColorProperty().bind(color1.valueProperty());
        fractalIV.inColorProperty().bind(color2.valueProperty());
        color1.setOnAction(e -> fractalIV.render());
        color2.setOnAction(e -> fractalIV.render());

        // Add color pickers and labels to list of nodes
        Collections.addAll(nodes,
                    color1, new Label("Color 1", color1), 
                    color2, new Label("Color 2", color2));

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
            color1.setValue(Color.RED);
            color2.setValue(Color.BLUE);
        });

        // This color mode was discovered by accident, but it looks cool
        // See MandelbrotPane class for implementations
        RadioButton psych = new RadioButton("Psychedelic Colors");
        psych.setTooltip(new Tooltip(
                "\"Random\" bands of color, change color 1 and the iteration count for completely new renders"
                        + "\n Looks best when Color 1 is *not* RED"));
        psych.setToggleGroup(group);

        // Set current colors to nice options when this mode is selected
        psych.setOnAction(e -> {
            color1.setValue(Color.web("99b3ff"));
            color2.setValue(Color.WHITE);
        });
        // Bind this radio button to toggle the coloring mode in MandelbrotPane Object
        fractalIV.psychedelicProperty().bind(psych.selectedProperty());
        
        // Add radio buttons to list of nodes, and return
        Collections.addAll(nodes, range, psych);
        return nodes;
    }

    /**
     * Open a file chooser and save the current image
     * @param image The Image to save
     */
    public void saveImageToFile(Image image) {

        // Let the user select a location to store the image
        // Default is "home/pictures"
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Image");
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures"));
        chooser.setInitialFileName("mandelbrot.png");
        chooser.getExtensionFilters().add(new ExtensionFilter("Image file (*.png)", "*.png"));
        File outFile = chooser.showSaveDialog(this.primaryStage);

        if (outFile != null) {
            // Convert to an ImageBuffer we can write to a file
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(bImage, "png", outFile);
                System.out.println("File saved to " + outFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}