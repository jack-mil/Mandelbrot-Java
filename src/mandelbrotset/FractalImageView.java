package mandelbrotset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Represents a 2D fractal Image.
 * 
 * Method render() generates internal Image object property using settings
 * defined by public Property values.
 * <p>
 * This class is configured to easily create both Julia and Mandelbrot Set
 * images
 * <p>
 * Contains change listeners and event handlers to process user mouse and scroll
 * input to position the viewing area.
 * <p>
 * Colors are assigned to pixels in pickColor() method
 */
public class FractalImageView extends ImageView {

  // Private instance variables used to cache some property values
  private double _maxIter;
  private Color _color1;
  private Color _color2;
  private int _size;

  // Private settings properties
  private final IntegerProperty maxIterations;
  private final DoubleProperty centerX;
  private final DoubleProperty centerY;
  private final DoubleProperty radius;
  private final BooleanProperty psychedelic;
  private final ObjectProperty<Color> inColor;
  private final ObjectProperty<Color> outColor;
  private final BooleanProperty isJulia;
  private final DoubleProperty juliaAngle;
  private final IntegerProperty size;

  /** Creates a new ImageView Node with internal fractal image. */
  public FractalImageView() {
    this.setImage(new WritableImage(MainViewer.SIZE, MainViewer.SIZE));

    // Initialize instance properties with appropriate defaults
    this.maxIterations = new SimpleIntegerProperty(this, "Iteration Count", MainViewer.ITERATIONS);
    this.centerX = new SimpleDoubleProperty(this, "Render Center Point X", MainViewer.CENTER_X);
    this.centerY = new SimpleDoubleProperty(this, "Render Center Point Y", MainViewer.CENTER_Y);
    this.radius = new SimpleDoubleProperty(this, "Square render radius", MainViewer.RADIUS);

    this.psychedelic = new SimpleBooleanProperty(this, "Crazy colors", false);
    this.inColor = new SimpleObjectProperty<Color>(this, "inColor", Color.RED);
    this.outColor = new SimpleObjectProperty<Color>(this, "outColor", Color.BLUE);

    this.isJulia = new SimpleBooleanProperty(this, "Is Julia Fractal", false);
    this.juliaAngle = new SimpleDoubleProperty(this, "Imaginary part", 0.5);

    this.size = new SimpleIntegerProperty(this, "Pixel Size", MainViewer.SIZE);

    initHandlers();
  }

  /** Property handlers and event listeners */
  private void initHandlers() {
    // Resize internal image when size changes
    this.size.addListener(ov -> {
      int newSize = this.size.getValue();
      this.setImage(new WritableImage(newSize, newSize));
      render();
    });

    // Re-render whenever these properties change
    this.inColor.addListener(ov -> render());
    this.outColor.addListener(ov -> render());
    this.psychedelic.addListener(ov -> render());

    // Re-render when Julia angle changes
    this.juliaAngle.addListener(ov -> render());

    // Reset defaults when switching fractal types
    this.isJulia.addListener(ov -> {
      if (isJulia.getValue()) {
        this.centerX.set(MainViewer.JULIA_CENTER_X);
        this.radius.set(MainViewer.JULIA_RADIUS);
      } else {
        this.centerX.set(MainViewer.CENTER_X);
        this.radius.set(MainViewer.RADIUS);
      }
      this.centerY.set(MainViewer.CENTER_Y);
      this.maxIterations.set(MainViewer.ITERATIONS);
      render();
    });

    // Right click to set a new center point
    addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
      if (e.getButton() == MouseButton.SECONDARY) {
        Point2D fPoint = convertSpace(e.getX(), e.getY());
        this.centerX.set(fPoint.getX());
        this.centerY.set(fPoint.getY());
        render();
      }
    });

    // Increment the view (square) radius when scrolling on the fractal
    addEventFilter(ScrollEvent.SCROLL, e -> {
      if (e.getDeltaY() == 0)
        return;

      double t = this.radius.getValue();
      this.radius.set((e.getDeltaY() < 0) ? t + (MainViewer.ZOOM_PRCNT * t) : t - (MainViewer.ZOOM_PRCNT * t));
      render();
      e.consume();
    });

    // Increment the iteration count when arrow keys pressed
    setOnKeyPressed(e -> {
      int n = this.maxIterations.getValue();
      if (e.getCode() == KeyCode.UP) {
        this.maxIterations.set((int) (n + (n * MainViewer.ZOOM_PRCNT)));
      } else if (e.getCode() == KeyCode.DOWN) {
        this.maxIterations.set((int) (n - (n * MainViewer.ZOOM_PRCNT)));
      }
      render();
      e.consume();
    });
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

   /** Render the fractal image according to current property settings */
  public void render() {
    // Use PixelWriter to edit this ImageView's internal Image pixels
    PixelWriter pixels = ((WritableImage) this.getImage()).getPixelWriter();

    // Cache current settings from bound properties, once per render
    // No need to lookup every pixel and iteration
    _maxIter = this.maxIterations.doubleValue();
    _color1 = this.outColor.getValue();
    _color2 = this.inColor.getValue();
    _size = this.sizeProperty().getValue();

    // The magic radius 0.7885 comes from this animation:
    // https://en.wikipedia.org/wiki/Julia_set#/media/File:JSr07885.gif
    boolean doJulia = this.isJulia.getValue();
    Complex juliaConstant = Complex.polar2Rect(0.7885, this.juliaAngle.doubleValue());

    for (int Py = 0; Py < _size; Py++) {
      for (int Px = 0; Px < _size; Px++) {
        // Scale image coordinates to range
        // double z = 2.0 * (Px - _size / 2) / (_size / 2);
        // double zi = 1.33 * (Py - _size / 2) / (_size / 2);
        Point2D fPoint = convertSpace(Px, Py);

        int steps;
        // When calculating a mandelbrot set, Z starts at 0, and C changes per point
        // When calculating a julia set, Z is determined by the point, and C is some
        if (doJulia) {
          steps = checkConvergence(fPoint.getX(), fPoint.getY(), juliaConstant.getReal(), juliaConstant.getIm());
        } else {
          steps = checkConvergence(0.0, 0.0, fPoint.getX(), fPoint.getY());
        }

        pixels.setColor(Px, Py, pickColor(steps));
      }
    }
  }

  /**
   * Convert from image pixel space to point on complex plane
   * 
   * @param Px Pixel X position
   * @param Py Pixel Y position
   * @return Corrisponding point on Complex Plane
   */
  public Point2D convertSpace(double Px, double Py) {
    // From requested center point and radius,
    double radius = this.radius.doubleValue();
    double centerX = this.centerX.getValue();
    double centerY = this.centerY.getValue();

    // Math algorithm could probably be optimized, division and multiplication is slow
    // Scales and translates the input point
    double fReal = ((Px * (2 * radius)) / _size) + (centerX - radius);
    double fIm = ((Py * (2 * radius)) / _size) + (centerY - radius);

    return new Point2D(fReal, fIm);
  }

  /**
   * The actual fractal algorithm implementation. Checks whether f(Z)=Z² + C
   * converges when iterated where Z and C are complex numbers.
   * <p>
   * For the mandelbrot set, Z=0 initially, and C is our pixel.
   * <p>
   * For the Julia Set, Z is the pixel, and C is some arbitrary complex number of
   * magnitude < 2. Different C values give different Julia Sets.
   * <p>
   * The math here calculates the imaginary and real parts individually, instead
   * of using some sort of ComplexNumber object for maximum speed.
   * 
   * @return the number of iterations before convergence. n <= maxIter
   * <p> 
   * Algorithm from
   * https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set
   */
  protected int checkConvergence(double z, double zi, double c, double ci) {
    // Initial complex number Z = (z + zi)

    // Iteration counter
    int iter = 0;

    // |Z| must remain <= 2 in every iteration for point c to be
    // in the prisoner set
    while (z * z + zi * zi <= 4.0 && iter <= _maxIter) {
      // Calculate Re(z^2 + c)
      double zT = z * z - zi * zi + c;
      // Calculate Im(z^2 + c)
      double ziT = 2 * z * zi + ci;
      zi = ziT;
      z = zT;
      iter++;
    }
    return iter;
  }

  /**
   * Set the hue of each pixel based on the number of iterations completed. The
   * range is set by user input. Two modes are given: Normal mode is a gradient
   * from c1 -> c2. Psychedelic produces random looking well defined bands of
   * color.
   * <p>
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

  // Public Property getters to configure parameters \\
  // ----------------------------------------------- \\

  public IntegerProperty iterationsProperty() {
    return this.maxIterations;
  }

  public DoubleProperty radiusProperty() {
    return this.radius;
  }

  public DoubleProperty centerXProperty() {
    return this.centerX;
  }

  public DoubleProperty centerYProperty() {
    return this.centerY;
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

  public BooleanProperty isJuliaProperty() {
    return this.isJulia;
  }

  public DoubleProperty juliaAngleProperty() {
    return this.juliaAngle;
  }

  public IntegerProperty sizeProperty() {
    return this.size;
  }
}
