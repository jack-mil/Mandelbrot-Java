package mandelbrotset;

import java.text.NumberFormat;
import java.text.ParseException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

/**
 * Textfield implementation that accepts formatted number and stores them in a
 * Double property The user input is formatted when the focus is lost or the
 * user hits RETURN.
 * 
 * From: https://dzone.com/articles/javafx-numbertextfield-and
 * 
 * @author Thomas Bolz
 */
public class NumberTextField extends TextField {

  private final NumberFormat nf;
  private DoubleProperty number = new SimpleDoubleProperty();

  public final double getNumber() {
    return number.get();
  }

  public final void setNumber(double value) {
    number.set(value);
  }

  public DoubleProperty numberProperty() {
    return number;
  }

  public NumberTextField() {
    this(0);
  }

  public NumberTextField(double value) {
    this(value, NumberFormat.getInstance());
    initHandlers();
  }

  public NumberTextField(double value, NumberFormat nf) {
    super();
    this.nf = nf;
    initHandlers();
    setNumber(value);
  }

  public NumberTextField(NumberFormat nf) {
    super();
    this.nf = nf;
    initHandlers();
  }

  private void initHandlers() {

    // try to parse when focus is lost or RETURN is hit
    setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent e) {
        parseAndFormatInput();
      }
    });

    focusedProperty().addListener(new ChangeListener<Boolean>() {

      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue.booleanValue()) {
          parseAndFormatInput();
        }
      }
    });

    // Set text in field if Double property is changed from outside.
    numberProperty().addListener((obs, oldValue, newValue) -> {
      setText(nf.format(newValue));
    });
  }

  /**
   * Tries to parse the user input to a number according to the provided
   * NumberFormat
   */
  private void parseAndFormatInput() {
    try {
      String input = getText();
      if (input == null || input.length() == 0) {
        return;
      }
      Number parsedNumber = nf.parse(input);
      Double newValue = Double.parseDouble(parsedNumber.toString());
      setNumber(newValue);
      selectAll();
    } catch (ParseException ex) {
      // If parsing fails keep old number
      setText(nf.format(number.get()));
    }
  }
}
