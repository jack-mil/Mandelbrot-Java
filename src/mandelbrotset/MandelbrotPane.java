package mandelbrotset;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MandelbrotPane extends Pane {

    // Define the image size
    static final int WIDTH = 800;
    static final int HEIGHT = 800;
    // private int _width;
    // private int _height;

    private final IntegerProperty maxIterations;
    private final ObjectProperty<Color> inColor;
    private final ObjectProperty<Color> outColor;
    private final BooleanProperty psychedelic;

    private double _maxIter;
    private Color _color1;
    private Color _color2;

    private WritableImage image;
    private ImageView imageView;

    public MandelbrotPane() {
        
        this.image = new WritableImage(WIDTH, HEIGHT);
        this.imageView = new ImageView(this.image);

        this.maxIterations = new SimpleIntegerProperty(this, "Iteration Count", 100);
        this.psychedelic = new SimpleBooleanProperty(this, "Crazy colors", false);
        this.inColor = new SimpleObjectProperty<>(this, "inColor", Color.RED);
        this.outColor = new SimpleObjectProperty<>(this, "outColor", Color.BLUE);

        getChildren().add(this.imageView);
    }

    /**
     * The MandelbrotSet Object generates its pixelBuffer when it is run. Basic
     * algorithm from:
     * https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
     */
    public void render() {
        PixelWriter pixels = this.image.getPixelWriter();

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
     * range is set by user input Two modes are given: Normal mode is a gradient
     * from c1 -> c2 Psychedelic produces random looking well defined bands of color
     * 
     * The pixel will be black if it is determined to be *in* the set
     * 
     * @param N The iteration count to calculate color for
     * @return
     */
    private Color pickColor(int N) {
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

    public BooleanProperty psychedelicProperty() {
        return this.psychedelic;
    }

    public IntegerProperty iterationsProperty() {
        return this.maxIterations;
    }

    public ObjectProperty<Color> inColorProperty() {
        return this.inColor;
    }

    public final ObjectProperty<Color> outColorProperty() {
        return this.outColor;
    }

    public Image getImage() {
        return this.image;
    }
}
