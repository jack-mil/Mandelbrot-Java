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
import javafx.scene.image.PixelWriter;
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

  protected double _maxIter;
  protected Color _color1;
  protected Color _color2;
  protected PixelWriter pixels;

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

  protected void preRender() {
    // Use PixelWriter to edit this ImageView's internal Image pixels
    pixels = ((WritableImage) this.getImage()).getPixelWriter();

    // Get current settings from bound properties, once per render
    // No need to poll every iteration.
    _maxIter = this.maxIterations.doubleValue();
    _color1 = this.outColor.getValue();
    _color2 = this.inColor.getValue();
  }


  /**
   * The actual fractal algorithm implementation. Checks whether f(Z)=Z² + C
   * converges when iterated where Z and C are complex numbers.
   * <p>
   * For the mandelbrot set, Z=0 initially, and C is our pixel. <p>
   * For the Julia Set, Z is the pixel, and C is some arbitrary complex number of 
   * magnitude < 2. Different C values give different Julia Sets.
   * @return the number of iterations before convergence. n <= maxIter
   */
  protected int checkConvergence(double z, double zi, double c, double ci) {
    // Initial complex number Z = (z + zi)
    // Track iterations
    int n = 0;

    // |Z| must remain <= 2 in every iteration for point c to be
    // in the prisoner set
    while (z * z + zi * zi <= 4.0 && n <= _maxIter) {
      // Calculate Re(z^2 + c0)
      double zT = z * z - zi * zi + c;
      // Calculate Im(z^2 + c0)
      double ziT = 2 * z * zi + ci;
      zi = ziT;
      z = zT;
      n++;
    }
    return n;
  }

  /**
   * Set the hue of each pixel based on the number of iterations completed. The
   * range is set by user input. Two modes are given: Normal mode is a gradient
   * from c1 -> c2 Psychedelic produces random looking well defined bands of color
   * 
   * The pixel will be black if it is determined to be *in* the set
   * 
   * @param N The iteration count to calculate color for
   * @return javafx.scene.paint.Color
   */
  protected Color pickColor(int N) {

    double hue;
    double value;
    double sat;

    // Math is a bit ad-hoc for what looked good to me. Basically a function
    // of convergence speed, i.e. the ratio between max iterations and actual
    // iterations
    // for this pixel
    if (this.psychedelic.getValue()) {
      hue = _color1.getHue() * (_maxIter / N);
      value = (N < _maxIter) ? 1.0 : 0.0;
      sat = _color1.getSaturation();
    } else {
      hue = _color2.getHue() * (N / _maxIter) + _color1.getHue() * (_maxIter - N) / _maxIter;
      value = (N < _maxIter) ? (_color1.getBrightness() + _color2.getBrightness()) / 2.0 : 0.0;
      sat = (_color1.getSaturation() + _color2.getSaturation()) / 2.0;
    }
    return Color.hsb(hue, sat, value);
  }

  

}
