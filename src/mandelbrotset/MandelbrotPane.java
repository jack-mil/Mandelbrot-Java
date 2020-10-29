package mandelbrotset;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MandelbrotPane extends Pane {
    // Define the window size
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    // Define maximum iterations to calculate for *each* point
    static final int ITERATIONS = 100;

    private final IntegerProperty iterations;
    private final ObjectProperty<Color> inColor;
    private final ObjectProperty<Color> outColor;

    private WritableImage image;
    private ImageView imageView;

    public MandelbrotPane() {
        this.image = new WritableImage(WIDTH, HEIGHT);
        this.imageView = new ImageView(this.image);

        this.iterations = new SimpleIntegerProperty(this, "Iteration Count", ITERATIONS);
        this.inColor = new SimpleObjectProperty<>(this, "inColor", Color.WHITE);
        this.outColor = new SimpleObjectProperty<>(this, "outColor", Color.BLACK);

        getChildren().add(this.imageView);
    }

    public IntegerProperty iterationsProperty() {
        return this.iterations;
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

    /**
     * The MandelbrotSet Object generates its pixelBuffer when it is run. Basic
     * algorithm from:
     * https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
     */
    public void render() {
        PixelWriter pixels = this.image.getPixelWriter();

        for (int Py = 0; Py < HEIGHT; Py++) {
            for (int Px = 0; Px < WIDTH; Px++) {

                // Scale image coordinates to mandelbrot range
                // (-2.5 < x < 1) and (-1 < y < 1)
                // This will be point c0
                double x0 = Px / (double) WIDTH * 3.5 - 2.5;
                double y0 = Py / (double) HEIGHT * 2 - 1;

                // Initial complex number z = (x + iy)
                double x = 0.0;
                double y = 0.0;
                // Track iterations
                int n = 0;

                // |z| must remain <= 2 in every iteration for point c to be
                // in the mandelbrot set
                while (x * x + y * y <= 2 * 2 && n < this.iterations.intValue()) {
                    // Calculate Re(z^2 + c0)
                    double xtemp = x * x - y * y + x0;
                    // Calculate Im(z^2 + c0)
                    y = 2 * x * y + y0;
                    x = xtemp;
                    n++;
                }
                // A pixel is WHITE if it is *not* part of the mandelbrot set
                Color rgb = (n == this.iterations.intValue()) ? this.inColor.getValue() : this.outColor.getValue();
                pixels.setColor(Px, Py, rgb);
            }
        }
    }

}
