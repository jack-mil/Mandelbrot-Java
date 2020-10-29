package mandelbrotset;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MandelbrotSet extends Application {

    private MandelbrotPane fractal;

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
        render.setOnAction(e -> fractal.render(color1.getValue(), color2.getValue()));

        Button save = new Button("Save Image");
        save.setOnAction(e -> fractal.saveToFile(primaryStage));

        options.getChildren().addAll(render, color1, color2, save);

        /* --- Image area (left) --- */
        fractal = new MandelbrotPane();
        fractal.render(color1.getValue(), color2.getValue());
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

    /** The main class only exists to run from within the IDE */
    public static void main(String[] args) {
        launch(args);
    }

}