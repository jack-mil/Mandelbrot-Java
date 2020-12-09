package mandelbrotset;

public class JuliaImageView extends FractalImageView {

    private double c = -0.5;
    private double ci = 0.5;

    @Override
    public void render() {
        this.preRender();

        for (int Py = 0; Py < HEIGHT; Py++) {
            for (int Px = 0; Px < WIDTH; Px++) {
                // Scale image coordinates to mandelbrot range
                // All points are within a radius 2 circle on complex plane
                // I chose points that made a nice centered, square image
                // This will be point c0
                double x0 = 2.0 * (Px - WIDTH / 2) / (WIDTH / 2);
                double y0 = 1.33 * (Py - HEIGHT / 2) / (HEIGHT / 2);

                int steps = checkConvergence(x0, y0, c, ci);

                pixels.setColor(Px, Py, pickColor(steps));
            }
        }
    }

}
