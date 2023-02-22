package neo;

public class Vector {
    private double[] data;
    private int dimension;
    
    public Vector(int dimension) {
        this.data = new double[dimension];
        this.dimension = dimension;
    }
    
    public Vector(double... data) {
        this.data = data;
        this.dimension = data.length;
    }
    
    /**
     * Whether two vectors are equal element-wise.
     * @return true or false.
     */
    public boolean equals(Vector that) {
        for (int i = 0; i < dimension(); i++) {
            if (this.get(i) != that.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Add two vectors
     * @return the resultant
     */
    public Vector plus(Vector that) {
        checkDimension(that);
        
        Vector out = new Vector(dimension);
        for (int i = 0; i < dimension; i++) {
            out.set(i, this.get(i) + that.get(i));
        }
        
        return out;
    }
    
    /**
     * The dot-product of two vectors
     * @return the product
     */
    public double times(Vector that) {
        checkDimension(that);
        double out = 0;
        for (int i = 0; i < dimension; i++) {
            out += this.get(i) * that.get(i);
        }
        return out;
    }
    
    /**
     * Scalar product
     * @return the product
     */
    public Vector times(double that) {
        Vector out = new Vector(dimension);
        for (int i = 0; i < dimension; i++) {
            out.set(i, this.get(i) * that);
        }
        return out;
    }
    
    /**
     * Difference of two vectors.
     * @return the difference of two vectors.
     */
    public Vector minus(Vector that) {
        return this.plus(that.times(-1.0));
    }
    
    /**
     * Set the ith element of the vector
     */
    public void set(int index, double element) {
        data[index] = element;
    }
    
    /**
     * Get the ith element of the vector
     * @return the ith element
     */
    public double get(int index) {
        return data[index];
    }
    
    /**
     * Get the dimension of the vector
     * @return dimension
     */
    public int dimension() {
        return dimension;
    }
    
    /**
     * Get the length/magnitude of the vector
     * @return length
     */
    public double magnitude() {
        double sum = 0.0;
        for (double i: data) {
            sum += Math.pow(i, 2);
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Get the data of the vector
     * @return an array
     */
    public double[] getArray() {
        return data;
    }
    
    /**
     * Get a unit-vector in the direction of
     * the array
     * @return a unit vector
     */
    public Vector direction() {
        return this.times(1.0 / this.magnitude());
    }
    
    public String toString() {
        String out = "";
        for (int i = 0; i < dimension - 1; i++) {
            out += String.format("%.3G\t", this.get(i));
        }
        return "[" + out + String.format("%.3G", this.get(dimension-1)) + "]";
    }
    
    private void checkDimension(Vector that) {
        if (that.dimension() != this.dimension) {
            throw new IllegalArgumentException("Vector dimensions don't match");
        }
    }
}
