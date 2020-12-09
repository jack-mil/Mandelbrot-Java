package mandelbrotset;

import javafx.scene.control.ToggleButton;

/** Create RadioButton like functionality from a toggle button. */
public class RadioToggleButton extends ToggleButton {


    public RadioToggleButton(String arg0) {
        super(arg0);
    }
    /**
     * Toggles the state of the radio button if and only if the RadioButton has not
     * already selected or is not part of a ToggleGroup.
     */
    @Override
    public void fire() {
        if (getToggleGroup() == null || !isSelected()) {
            super.fire();
        }
    }

}
