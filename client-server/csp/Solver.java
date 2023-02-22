package csp;

import neo.Vector;
import euclid.Space;

/**
 * Finds the optimal solution to a system of constraints
 */
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
    
    /**
     * Calculate the gradient of the system.
     * The gradient is a vector containing the
     * derivative of the function with respect to
     * each variable.
     *
     * <pre>
     * gradient(f) = [D(f, x_1), D(f, x_2), ..., D(f, x_3)]
     * </pre>
     * @return the gradient of the system
     */
    public Vector gradient() {
        Vector grad = new Vector(parameters.dimension());
        for (int i = 0; i < parameters.dimension(); i++) {
            double d = deriv(i);
            grad.set(i, d);
        }
        return grad;
    }
    
    /**
     * The partial derivative with respect to the ith variable.
     * Since rendering on a screen is discrete, we don't need
     * infinitesimally small changes to calculate the derivative.
     * We can just use 1.
     */
    public double deriv(int i) {
        double A = constraints.error();
        parameters.update(parameters.vector().plus(parameters.dx(i)));
        double B = constraints.error();
        parameters.update(parameters.vector().minus(parameters.dx(i)));
        return (B - A) / Parameters.EPSILON;
    }
    
    /**
     * Makes a small knudge in the parameters that reduces
     * the total error of the system. This algorithm is based
     * on <a href='https://en.wikipedia.org/wiki/Gradient_descent'>Gradient Descent</a>
     * which is a greedy-algorithm that moves in the direction
     * that reduces the total error the most.
     */
    public void solve() {
        Vector gradient = gradient();
        if (constraints.error() > 1) {
            parameters.update
                (parameters.vector().plus(gradient.times(-1).direction()));
        }
    }
}
