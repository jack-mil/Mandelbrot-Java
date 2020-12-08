package mandelbrotset;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
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
public abstract class FractalImageView extends ImageView {

    // Define the image size
    protected final int WIDTH = 800;
    protected final int HEIGHT = 800;

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
