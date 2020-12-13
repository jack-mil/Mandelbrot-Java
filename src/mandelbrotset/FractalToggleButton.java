package mandelbrotset;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

/** This button selects between two modes.
 * Extends javafx.scene.control.ToggleButton by changing the label when toggled
 */
public class FractalToggleButton extends ToggleButton {

    public FractalToggleButton(String arg0) {
        super(arg0);
        this.setTooltip(new Tooltip("Switch between Julia or Mandelbrot Set"));
    }

    @Override
    public void fire() {
        if(this.isSelected()) {
            this.setText("Julia Set");
        } else {
            this.setText("Mandelbrot Set");
        }
        super.fire();
    }

}
