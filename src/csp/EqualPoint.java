package csp;

import euclid.Point;
import euclid.Space;

public class EqualPoint implements Constraint {
    Point A, B;
    
    public EqualityConstraint(Point a, Point b) {
        this.A = a;
        this.B = b;
    }
    
    public double error() {
        return Math.pow(A.distance(B), 2);
    }
    
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
    
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='EqualPoint' A='%d' B='%d'></constraint>", a, b);
    }
}