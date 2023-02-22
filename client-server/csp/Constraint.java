package csp;

import neo.Vector;
import euclid.GeometricObject;
import euclid.Space;

import org.w3c.dom.Node;

/**
 * The constraint interface
 */
public interface Constraint {
    public double error();              // The error function
    public GeometricObject[] objects(); // The objects involved
    public String toString();           // String conversion
    public String xml(Space context);   // Convert to XML representation
}
