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
import javafx.scene.control.Slider;
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

    private MandelbrotPane fractal = new MandelbrotPane();

    private Stage primaryStage;

    /**
     * The start() method is run automatically by the Application superclass Here we
     * setup the JavaFX UI to display a static Mandelbrot Fractal
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Stage settings
        primaryStage.setTitle("CS2: Mandelbrot Set");

        /* --- Options area (right) --- */
        VBox options = new VBox();
        options.setAlignment(Pos.TOP_CENTER);
        options.setPadding(new Insets(10));
        options.setSpacing(10);

        ColorPicker color1 = new ColorPicker(Color.WHITE);
        ColorPicker color2 = new ColorPicker(Color.BLACK);

        // Bind button events to MandelbrotPane instance methods
        Button render = new Button("(Re)render");
        render.setOnAction(e -> fractal.render());

        Button save = new Button("Save Image");
        save.setOnAction(e -> saveImageToFile(fractal.getImage()));

        // Create a slider that controls the number of iterations
        Slider iter = new Slider(0, 1000, 100);
        iter.setShowTickLabels(true);
        iter.setShowTickMarks(true);
        iter.setMajorTickUnit(100);
        fractal.iterationsProperty().bind(iter.valueProperty());


        options.getChildren().addAll(render, color1, color2, iter, save);

        /* --- Image area (left) --- */
        fractal.inColorProperty().bind(color1.valueProperty());
        fractal.outColorProperty().bind(color2.valueProperty());
        fractal.render();
        Text name = new Text("Jackson Miller");
        name.setFont(new Font(20));
        name.strokeProperty().bind(color1.valueProperty());
        name.fillProperty().bind(color1.valueProperty());

        StackPane.setAlignment(name, Pos.BOTTOM_RIGHT);
        StackPane imageGroup = new StackPane(fractal, name);

        // Add image and options to main Scene
        HBox mainPane = new HBox();
        mainPane.setPadding(new Insets(10));
        mainPane.getChildren().addAll(imageGroup, options);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
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