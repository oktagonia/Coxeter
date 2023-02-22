package csp;

import euclid.Point;
import euclid.Space;

/**
 * Constrain two points to overlap
 */
public class EqualPoint implements Constraint {
    Point A, B;
    
    public EqualityConstraint(Point a, Point b) {
        this.A = a;
        this.B = b;
    }
    
    /**
     * The error is the square of the distance
     * between the two points.
     * @return the error
     */
    public double error() {
        return Math.pow(A.distance(B), 2);
    }
    
    /**
     * Return the objects involved in this constraint
     * @return the objects involved in this constraint
     */
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
    
    /**
     * return xml representation
     * @return xml
     */
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='EqualPoint' A='%d' B='%d'></constraint>", a, b);
    }
}
