package mandelbrotset;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/* ****************************
/* Jackson Miller
/* COSC2103 Computer Science 2
/* 2020-12-08
/* Fractal image viewer
/* ****************************/

/** Constructs a scene with pan-able fractal image and controls */
public class FractalViewer extends Application {
  private FractalImageView fractalIV;

  @Override
  public void init() {
    // The Fractal Image View to be displayed.
    fractalIV = new JuliaImageView();
  }

  /** The main class only exists to run from within the IDE */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * The start() method is run automatically by the Application superclass. Here
   * we setup the JavaFX UI to display a static Mandelbrot Fractal
   */
  @Override
  public void start(Stage stage) {
    // Stage settings, with application icon
    stage.setTitle("Fractal Viewer");
    stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

    /* --- Options and controls (right) --- */
    OptionsPane options = new OptionsPane();

    // Bind color options to fractal view from options
    fractalIV.outColorProperty().bind(options.color1ValueProperty());
    fractalIV.inColorProperty().bind(options.color2ValueProperty());

    // Bind iteration count property from options
    fractalIV.iterationsProperty().bind(options.iterationValueProperty());

    // Bind radio toggle button for color modes
    fractalIV.psychedelicProperty().bind(options.psychSelectedProperty());

    // Bind button events to MandelbrotPane instance methods
    options.setRenderAction(e -> fractalIV.render());
    options.setSaveAction(e -> fractalIV.saveImageToFile());

    /* --- Image area (left) --- */
    ScrollPane scroll = createScrollPane(fractalIV);
    // Add current center coordinates
    Text name = new Text("re: xxx.xxx, im: xxx.xxx");
    name.setFont(new Font(10));
    name.setStroke(Color.BLACK);
    name.setFill(Color.BLACK);

    // Create StackPane to layer coords and fractal image
    StackPane.setAlignment(name, Pos.TOP_LEFT);
    StackPane layout = new StackPane(scroll, name);

    // Add image and options to main Scene
    HBox mainPane = new HBox();
    mainPane.setPadding(new Insets(10));
    mainPane.getChildren().addAll(layout, options);

    // Render the Fractal Image
    fractalIV.render();

    // show the scene.
    Scene scene = new Scene(mainPane);
    stage.setScene(scene);
    stage.show();

    // Bind the preferred size of the scroll area to the size of the scene.
    scroll.prefWidthProperty().bind(scene.widthProperty());
    scroll.prefHeightProperty().bind(scene.heightProperty());

    // Center the scroll contents.
    scroll.setHvalue(scroll.getHmin() + (scroll.getHmax() - scroll.getHmin()) / 2);
    scroll.setVvalue(scroll.getVmin() + (scroll.getVmax() - scroll.getVmin()) / 2);
  }

  /** @return ScrollPane which scrolls the layout. */
  private ScrollPane createScrollPane(Node node) {
    ScrollPane scroll = new ScrollPane();
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setPrefViewportWidth(800);
    scroll.setPrefViewportHeight(800);
    scroll.setPannable(true);
    scroll.setContent(node);
    return scroll;
  }

}