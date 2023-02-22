package csp;

import euclid.Line;
import euclid.GeometricObject;
import euclid.Space;

public class Parallel implements Constraint {
    Line A, B;
    
    public Parallel(Line a, Line b) {
        this.A = a;
        this.B = b;
    }
    
    public double error() {
        return 100 * Math.pow(A.slope() - B.slope(), 2);
    }
    
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='Parallel' A='%d' B='%d'></constraint>", a, b);
    }
    
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
    
    public String toString() {
        return A.label() + " ∥ " + B.label();
    }
}