package csp;

import neo.Vector;
import euclid.GeometricObject;
import euclid.Space;

import org.w3c.dom.Node;

public interface Constraint {
    public double error();
    public GeometricObject[] objects();
    public String toString();
    public String xml(Space context);
}
