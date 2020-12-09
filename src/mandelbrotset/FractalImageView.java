package mandelbrotset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Represents a 2D fractal Image.
 * 
 * Method render() generates internal Image object property using settings
 * defined by public Property values.
 * 
 * Only generates 800 x 800 px images at this point, resizable images are saved
 * for future.
 */
public abstract class FractalImageView extends ImageView {

  // Define the image size
  protected final int WIDTH = 1200;
  protected final int HEIGHT = 1200;

  protected final IntegerProperty maxIterations;
  protected final ObjectProperty<Color> inColor;
  protected final ObjectProperty<Color> outColor;
  protected final BooleanProperty psychedelic;

  /** Creates a new 800x800 px ImageView Node with internal fractal image. */
  public FractalImageView() {

    this.setImage(new WritableImage(WIDTH, HEIGHT));

    this.maxIterations = new SimpleIntegerProperty(this, "Iteration Count");
    this.psychedelic = new SimpleBooleanProperty(this, "Crazy colors", false);
    this.inColor = new SimpleObjectProperty<>(this, "inColor", Color.RED);
    this.outColor = new SimpleObjectProperty<>(this, "outColor", Color.BLUE);

  }

  // Fractal Image Properties
  public IntegerProperty iterationsProperty() {
    return this.maxIterations;
  }

  public ObjectProperty<Color> inColorProperty() {
    return this.inColor;
  }

  public final ObjectProperty<Color> outColorProperty() {
    return this.outColor;
  }

  public BooleanProperty psychedelicProperty() {
    return this.psychedelic;

  }

  /** Open a file chooser and save the internal fractal image */
  public void saveImageToFile() {

    // Let the user select a location to store the image
    // Default is "home/pictures"
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Save Image");
    chooser.setInitialDirectory(
        new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures"));
    chooser.setInitialFileName("mandelbrot.png");
    chooser.getExtensionFilters().add(new ExtensionFilter("Image file (*.png)", "*.png"));
    File outFile = chooser.showSaveDialog(this.getScene().getWindow());

    if (outFile != null) {
      // Convert to an ImageBuffer we can write to a file
      BufferedImage bImage = SwingFXUtils.fromFXImage(this.getImage(), null);
      try {
        ImageIO.write(bImage, "png", outFile);
        System.out.println("File saved to " + outFile.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * The rendering algorithm must be defined in child classes of abstract class
   * FractalImageView
   */
  public abstract void render();

  /**
   * The abstract method pickColor is used internally by render() to determine
   * rendered pixel colors. This must be defined my desired fractal class
   */
  protected abstract Color pickColor(int N);

}