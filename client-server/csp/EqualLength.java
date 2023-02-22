package csp;

import euclid.Line;
import euclid.GeometricObject;
import euclid.Space;

/**
 * Constrain to lines to be equal in length
 */
public class EqualLength implements Constraint {
    private Line A, B;
    
    public EqualLength(Line a, Line b) {
        this.A = a;
        this.B = b;
    }
    
    /**
     * The error of this is the square of the difference between
     * the length of the two lines.
     * @return the error
     */
    public double error() {
        return Math.pow(A.distance() - B.distance(), 2);
    }
    
    /**
     * Convert to string
     * @return string representation
     */
    public String toString() {
        return "|" + A.label() + "| = |" + B.label() + "|";
    }
    
    /**
     * Convert to xml representation
     * @return xml
     */
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='EqualLength' A='%d' B='%d'></constraint>", a, b);
    }
    
    /**
     * Return the objects involved in this constraint
     * @return the objects involved in this constraint
     */
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
}
