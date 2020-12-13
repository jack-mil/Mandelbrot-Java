package mandelbrotset;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/** Combo box with image resolution (size) options */
public class SizeControl extends ComboBox<Integer> {

  // Hardcoded image size options
  private ObservableList<Integer> sizes = FXCollections.observableArrayList(400, 640, 800, 1080, 1440, 1900);

  public SizeControl() {
    this.setConverter(new DimensionConverter());
    this.getItems().addAll(sizes);
    this.setValue(800);
  }
}

/** Class to display the image size options */
class DimensionConverter extends StringConverter<Integer> {
  @Override
  public String toString(Integer n) {
    if (n == 400)
      return "400x400";
    if (n == 640)
      return "640x640";
    if (n == 800)
      return "800x800";
    if (n == 1080)
      return "1080x1080";
    if (n == 1440)
      return "1440x1440";
    return "1900x1900 SLOW!";
  }

  @Override
  public Integer fromString(String s) {
    switch (s) {
      case "400x400":
        return 400;
      case "640x640":
        return 640;
      case "800x800":
        return 800;
      case "1080x1080":
        return 1080;
      case "1440x1440":
        return 1440;
      default:
        return 1900;
    }
  }
}
