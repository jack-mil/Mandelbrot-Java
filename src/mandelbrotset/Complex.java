package mandelbrotset;

/** Complex number utility class for converting from polar to rectangular form */
public class Complex {
  private double real;
  private double im;

  public Complex() {
    this.real = 0;
    this.im = 0;
  }

  public Complex(double real, double im) {
    this.real = real;
    this.im = im;
  }

  public static Complex polar2Rect(double radius, double angle) {
    double im = radius * Math.sin(angle);
    double real = radius * Math.cos(angle);
    return new Complex(real, im);
  }

  public double getReal() {
    return this.real;
  }

  public double getIm() {
    return this.im;
  }
}
