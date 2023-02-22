package euclid;

import java.util.function.Supplier;

/**
 * A parameter is any value in the system
 */
public class Parameter {
    public double value;
    public boolean locked;
    public Supplier<Double> supplier;
    
    public Parameter(double value, boolean locked) {
        this.value = value;
        this.locked = locked;
        this.supplier = () -> this.value;
    }
    
    public Parameter(Supplier<Double> supplier) {
        this.supplier = supplier;
        this.locked = true;
        this.value = 0.0;
    }

    public Parameter(double value) {
        this.value = value;
        this.locked = true;
        this.supplier = () -> this.value;
    }
    
    /**
     * Get the value of the parameter.
     * @return the value.
     */
    public double get() {
        return supplier.get();
    }
    
    public String toString() {
        return String.format("p[%f, %b]", get(), locked);
    }
    
    public String xml() {
        return String.format
            ("<parameter value='%f' locked='%b'></parameter>", get(), locked);
    }
}
