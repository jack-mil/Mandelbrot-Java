package mandelbrotset;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Represents a Mandelbrot fractal Image.
 * 
 * Method render() generates internal Image object property using settings
 * defined by public Property value.
 * 
 * Only generates 800 x 800 px images at this point, resizable images are saved
 * for future.
 */
public class MandelbrotImageView extends FractalImageView {

  private double _maxIter;
  private Color _color1;
  private Color _color2;

  // Values for the Mandelbrot set
  private static double MANDELBROT_RE_MIN = -2;
  private static double MANDELBROT_RE_MAX = 1;
  private static double MANDELBROT_IM_MIN = -1.2;
  private static double MANDELBROT_IM_MAX = 1.2;

  /**
   * The MandelbrotSet Object generates its pixelBuffer when it is run. Basic
   * algorithm from:
   * https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
   */
  public void render() {
    // Use PixelWriter to edit this ImageView's internal Image pixels
    PixelWriter pixels = ((WritableImage) this.getImage()).getPixelWriter();

    // Get current settings from bound properties, once per render
    // No need to poll every iteration.
    _maxIter = this.maxIterations.doubleValue();
    _color1 = this.outColor.getValue();
    _color2 = this.inColor.getValue();

    for (int Py = 0; Py < HEIGHT; Py++) {
      for (int Px = 0; Px < WIDTH; Px++) {
        // Scale image coordinates to mandelbrot range
        // All points are within a radius 2 circle on complex plane
        // I chose points that made a nice centered, square image
        // This will be point c0
        double x0 = Px / (double) HEIGHT * 3 - 2.15;
        double y0 = Py / (double) WIDTH * 3 - 1.5;

        int steps = checkConvergence(x0, y0);

        pixels.setColor(Px, Py, pickColor(steps));
      }
    }
  }

  /** The actual Mandelbrot algorithm implementation.
   * Checks whether f(Z)=Z² + C converges when iterated from z=0;
   * where Z and C are complex numbers
   * @param c real part of complex number
   * @param ci imaginary part
   * @return the number of iterations before convergence. n <= maxIter
   */
  private int checkConvergence(double c, double ci) {
    // Initial complex number Z = (z + zi)
    double z = 0.0;
    double zi = 0.0;
    // Track iterations
    int n = 0;

    // |Z| must remain <= 2 in every iteration for point c to be
    // in the mandelbrot set
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
