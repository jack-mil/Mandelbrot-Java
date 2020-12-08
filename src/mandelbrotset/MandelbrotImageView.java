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

    /**
     * The MandelbrotSet Object generates its pixelBuffer when it is run. Basic
     * algorithm from: https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
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

                // Initial complex number z = (x + iy)
                double x = 0.0;
                double y = 0.0;
                // Track iterations
                int n = 0;

                // |z| must remain <= 2 in every iteration for point c to be
                // in the mandelbrot set
                while (x * x + y * y <= 2 * 2 && n < _maxIter) {
                    // Calculate Re(z^2 + c0)
                    double xtemp = x * x - y * y + x0;
                    // Calculate Im(z^2 + c0)
                    y = 2 * x * y + y0;
                    x = xtemp;
                    n++;
                }

                pixels.setColor(Px, Py, pickColor(n));
            }
        }
    }

    /**
     * Set the hue of each pixel based on the number of iterations completed. The
     * range is set by user input. Two modes are given: Normal mode is a gradient
     * from c1 -> c2 Psychedelic produces random looking well defined bands of color
     * 
     * The pixel will be black if it is determined to be *in* the set
     * 
     * @param N The iteration count to calculate color for
     * @return
     */
    protected Color pickColor(int N) {

        double hue;
        double value;
        double sat;
        
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
