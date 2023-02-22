package csp;

import neo.Vector;
import euclid.Space;

public class Solver {
    private Parameters parameters;
    private Constraints constraints;
    private double prev = 0;
    
    public Solver(Parameters parameters, Constraints constraints) {
        this.parameters = parameters;
        this.constraints = constraints;
    }
    
    public Solver(Space context) {
        this.parameters = context.parameters;
        this.constraints = context.constraints;
    }
    
    public void update(Space context) {
        this.parameters = context.parameters;
        this.constraints = context.constraints;
    }
    
    public Vector gradient() {
        Vector grad = new Vector(parameters.dimension());
        for (int i = 0; i < parameters.dimension(); i++) {
            double d = deriv(i);
            grad.set(i, d);
        }
        return grad;
    }
    
    public double deriv(int i) {
        double A = constraints.error();
        parameters.update(parameters.vector().plus(parameters.dx(i)));
        double B = constraints.error();
        parameters.update(parameters.vector().minus(parameters.dx(i)));
        return (B - A) / Parameters.EPSILON;
    }
    
    public void solve() {
        Vector gradient = gradient();
        if (constraints.error() > 1) {
            parameters.update
                (parameters.vector().plus(gradient.times(-1).direction()));
        }
    }
}
