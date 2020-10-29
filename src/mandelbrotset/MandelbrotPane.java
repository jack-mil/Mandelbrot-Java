package mandelbrotset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MandelbrotPane extends Pane {
    // Define the window size
    static final int WIDTH = 1000;
    static final int HEIGHT = 1000;

    // Define maximum iterations to calculate for *each* point
    static final int ITERATIONS = 100;

    private WritableImage image = new WritableImage(WIDTH, HEIGHT);
    private ImageView imageView = new ImageView(this.image);
    public MandelbrotPane() {
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        getChildren().add(this.imageView);
    }

    /**
     * The MandelbrotSet Object generates its pixelBuffer when it is run. 
     * Basic algorithm from:
     * https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
     */
    public void render(Color inColor, Color outColor) {
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
                while (x * x + y * y <= 2 * 2 && n < ITERATIONS) {
                    // Calculate Re(z^2 + c0)
                    double xtemp = x * x - y * y + x0;
                    // Calculate Im(z^2 + c0)
                    y = 2 * x * y + y0;
                    x = xtemp;
                    n++;
                }
                // A pixel is WHITE if it is *not* part of the mandelbrot set
                Color rgb = (n == ITERATIONS) ? inColor : outColor;
                pixels.setColor(Px, Py, rgb);
            }
        }
    }

    /**
     * Open a file chooser and save the current image
     * 
     * @param stage
     */
    public void saveToFile(Stage stage) {

        // Let the user select a location to store the image
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Image");
        chooser.setInitialFileName("mandelbrot.png");
        chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Image file (*.png)", "*.png"));
        File outFile = chooser.showSaveDialog(stage);

        if (outFile != null) {
            // Convert to an ImageBuffer we can write to a file
            BufferedImage bImage = SwingFXUtils.fromFXImage(this.image, null);
            try {
                ImageIO.write(bImage, "png", outFile);
                System.out.println("File saved to " + outFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
