package csp;

import euclid.Line;
import euclid.GeometricObject;
import euclid.Space;

public class EqualLength implements Constraint {
    private Line A, B;
    
    public EqualLength(Line a, Line b) {
        this.A = a;
        this.B = b;
    }
    
    public double error() {
        return Math.pow(A.distance() - B.distance(), 2);
    }
    
    public String toString() {
        return "|" + A.label() + "| = |" + B.label() + "|";
    }
    
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<constraint type='EqualLength' A='%d' B='%d'></constraint>", a, b);
    }
    
    /* public Constraint load(Node node, Space context) {
        
    } */
    
    public GeometricObject[] objects() {
        return new GeometricObject[] { A, B };
    }
}