package csp;

import euclid.Line;
import euclid.GeometricObject;
import euclid.Space;

/**
 * Constrain two lines to be parallel
 */
public class Parallel implements Constraint {
    Line A, B;
    
    public Parallel(Line a, Line b) {
        this.A = a;
        this.B = b;
    }
    
    /**
     * The error is the square of the different in slopes
     * @return the error
     */
    public double error() {
        return 100 * Math.pow(A.slope() - B.slope(), 2);
    }
    
    /**
     * The xml representation
     * @return the xml representation
     */
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='Parallel' A='%d' B='%d'></constraint>", a, b);
    }
    
    /**
     * Return the objects involved in this constraint
     * @return the objects involved in this constraint
     */
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
    
    public String toString() {
        return A.label() + " ∥ " + B.label();
    }
}
