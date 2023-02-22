package csp;

import euclid.Line;
import euclid.GeometricObject;
import euclid.Space;

/**
 * Constrain two lines to be perpendicular
 */
public class Perpendicular implements Constraint {
    Line A, B;
    
    public Perpendicular(Line a, Line b) {
        this.A = a;
        this.B = b;
    }
    
    /**
     * The error is the square of the difference of 
     * the angle between them and 90 degrees.
     * @return the error
     */
    public double error() {
        return 10000 * Math.pow(A.angle(B) - (Math.PI / 2), 2);
    }
    
    /**
     * Convert to xml representation
     * @return xml
     */
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='Perpendicular' A='%d' B='%d'></constraint>", a, b);
    }
    
    /**
     * Return the objects involved in this constraint.
     * @return objects
     */
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
    
    public String toString() {
        return A.label() + " ⟂ " + B.label();
    }
}
