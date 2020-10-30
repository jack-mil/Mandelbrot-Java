package mandelbrotset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MandelbrotSet extends Application {

    private MandelbrotPane fractalPane = new MandelbrotPane();

    private Stage primaryStage;

    /**
     * The start() method is run automatically by the Application superclass Here we
     * setup the JavaFX UI to display a static Mandelbrot Fractal
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Stage settings
        primaryStage.setTitle("CS2: Mandelbrot Set");
        primaryStage.getIcons().add(new Image(MandelbrotSet.class.getResourceAsStream("icon.png")));

        /* --- Options area (right) --- */
        VBox options = new VBox();
        options.setAlignment(Pos.TOP_CENTER);
        options.setPadding(new Insets(10));
        options.setSpacing(10);

        // Bind button events to MandelbrotPane instance methods
        Button render = new Button("(Re)render");
        render.setOnAction(e -> fractalPane.render());

        Label infoLabel = new Label("Hover for tooltips");

        options.getChildren().addAll(render, infoLabel);

        // Create Color pickers and labels
        ColorPicker color1 = new ColorPicker(Color.RED);
        ColorPicker color2 = new ColorPicker(Color.BLUE);
        // The chosen color is bound to Image properties, and triggers a render when
        // selected
        fractalPane.outColorProperty().bind(color1.valueProperty());
        fractalPane.inColorProperty().bind(color2.valueProperty());
        color1.setOnAction(e -> fractalPane.render());
        color2.setOnAction(e -> fractalPane.render());

        options.getChildren().addAll(color1, new Label("Color 1", color1), color2, new Label("Color 2", color2));

        // Create a button to save the current image to a file
        Button save = new Button("Save Image");
        save.setOnAction(e -> saveImageToFile(fractalPane.getImage()));

        // Create radio buttons to control coloring modes
        ToggleGroup group = new ToggleGroup();

        RadioButton range = new RadioButton("Range Color 1 -> 2");
        range.setTooltip(new Tooltip("A gradient based on how close to the set a point is"));
        range.setSelected(true);
        range.setToggleGroup(group);

        RadioButton psych = new RadioButton("Psychedelic Colors");
        psych.setTooltip(new Tooltip(
                "\"Random\" bands of color, change color 1 and the iteration count for completely new renders"
                        + "\n Looks best when Color 1 is *not* RED"));
        psych.setToggleGroup(group);
        // Set sensible default for this color mode
        psych.setOnAction(e -> {
            color1.setValue(Color.web("99b3ff"));
            color2.setValue(Color.WHITE);
        });
        // Bind this radio button to toggle the color mode in MandelbrotPane Object
        fractalPane.psychedelicProperty().bind(psych.selectedProperty());

        // Create a spinner to configure the maximum iterations
        Spinner<Integer> iterSpinner = new Spinner<>(1, 1000, 30);
        Label iterLabel = new Label("Iteration Count", iterSpinner);
        iterSpinner.setEditable(true);
        fractalPane.iterationsProperty().bind(iterSpinner.valueProperty());

        options.getChildren().addAll(range, psych, iterSpinner, iterLabel, save);

        /* --- Image area (left) --- */
        // Add signature
        Text name = new Text("Jackson Miller");
        name.setFont(new Font(20));
        name.setStroke(Color.BLACK);
        name.setFill(Color.BLACK);

        // Create Stackpane to layer name and fractal image
        StackPane.setAlignment(name, Pos.BOTTOM_RIGHT);
        StackPane imageGroup = new StackPane(fractalPane, name);

        // Add image and options to main Scene
        HBox mainPane = new HBox();
        mainPane.setPadding(new Insets(10));
        mainPane.getChildren().addAll(imageGroup, options);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
        // Render the Fractal Image
        fractalPane.render();
    }

    /**
     * Open a file chooser and save the current image
     * 
     * @param stage
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

    /** The main class only exists to run from within the IDE */
    public static void main(String[] args) {
        launch(args);
    }

}