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
  @Override
  public void render() {
    this.preRender();

    for (int Py = 0; Py < HEIGHT; Py++) {
      for (int Px = 0; Px < WIDTH; Px++) {
        // Scale image coordinates to mandelbrot range
        // All points are within a radius 2 circle on complex plane
        // I chose points that made a nice centered, square image
        // This will be point c0
        // double x0 = Px / (double) WIDTH * 3 - 2.15;
        // double y0 = Py / (double) HEIGHT * 3 - 1.5;
        double x0 = 2.0 * (Px - WIDTH / 2) / (WIDTH / 2);
        double y0 = 1.33 * (Py - HEIGHT / 2) / (HEIGHT / 2);

        int steps = checkConvergence(0.0, 0.0, x0, y0);

        pixels.setColor(Px, Py, pickColor(steps));
      }
    }
  }

}
