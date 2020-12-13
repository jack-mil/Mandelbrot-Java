package mandelbrotset;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/* ****************************
/* Jackson Miller
/* COSC2103 Computer Science 2
/* 2020-12-08
/* Fractal image viewer
/* ****************************/

/** Constructs a scene with pan-able fractal image and controls */
public class MainViewer extends Application {

  // Static default constants used in other classes
  public static final double RADIUS = 1.3;
  public static final double CENTER_X = -0.6;
  public static final double CENTER_Y = 0.0;
  public static final int ITERATIONS = 25;
  public static final double JULIA_ANGLE = Math.PI;
  public static final double JULIA_RADIUS = 1.7;
  public static final double JULIA_CENTER_X = 0.0;
  public static final int SIZE = 800;

  public static final double ZOOM_PRCNT = 0.1;

  // The current displayed fractal
  private FractalImageView selectedFrac;

  /** The main class only exists to run from within the IDE */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() {
    selectedFrac = new FractalImageView();
  }

  /**
   * The start() method is run automatically by the Application superclass. Here
   * we setup the JavaFX UI to display a static Mandelbrot Fractal
   */
  @Override
  public void start(Stage stage) {
    // Stage settings, with application icon
    stage.setTitle("Fractal Viewer");
    stage.getIcons().add(new Image(getClass().getResourceAsStream("resources/icon.png")));

    /* --- Options and controls (right) --- */
    OptionsPanel options = new OptionsPanel();

    // Bind color options to fractal view from options
    selectedFrac.outColorProperty().bind(options.color1ValueProperty());
    selectedFrac.inColorProperty().bind(options.color2ValueProperty());

    // Bind iteration count property from options
    options.iterationProperty().addListener(ov -> {
      selectedFrac.iterationsProperty().set(options.iterationProperty().get());
    });
    selectedFrac.iterationsProperty().addListener(ov -> {
      options.iterationProperty().set(selectedFrac.iterationsProperty().get());;
    });

    // Bind view window properties from options
    selectedFrac.radiusProperty().bindBidirectional(options.radiusProperty());
    selectedFrac.centerXProperty().bindBidirectional(options.centerXProperty());
    selectedFrac.centerYProperty().bindBidirectional(options.centerYProperty());

    // Bind size property from options
    selectedFrac.sizeProperty().bind(options.sizeProperty());

    // Bind radio toggle button for color modes
    selectedFrac.psychedelicProperty().bind(options.psychSelectedProperty());

    // Bind Julia Set related settings
    selectedFrac.isJuliaProperty().bind(options.typeSelectedProperty().not());
    selectedFrac.juliaAngleProperty().bind(options.juliaAngleProperty());

    // Create button events to MandelbrotPane instance methods
    options.setSaveAction(e -> selectedFrac.saveImageToFile());
    options.setRenderAction(e -> selectedFrac.render());

    /* --- Image area (left) --- */
    PanScrollPane scroll = new PanScrollPane(selectedFrac);

    // Add image and options to main Scene
    HBox mainPane = new HBox();
    mainPane.setPadding(new Insets(10));
    mainPane.getChildren().addAll(scroll, options);

    // Render the Fractal Image
    // Change listeners do this the first time too
    // selectedFrac.render();

    // Show the scene
    Scene scene = new Scene(mainPane);

    scene.addEventFilter(KeyEvent.KEY_PRESSED, selectedFrac.getOnKeyPressed());
    stage.setScene(scene);
    stage.show();

    // Bind the preferred size of the scroll area to the size of the scene.
    scroll.prefWidthProperty().bind(scene.widthProperty());
    scroll.prefHeightProperty().bind(scene.heightProperty());

    // Center the scroll contents.
    scroll.setHvalue(scroll.getHmin() + (scroll.getHmax() - scroll.getHmin()) / 2);
    scroll.setVvalue(scroll.getVmin() + (scroll.getVmax() - scroll.getVmin()) / 2);
  }

}