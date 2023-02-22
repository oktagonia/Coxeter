package csp;

import java.util.ArrayList;

import euclid.Parameter;
import neo.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.NamedNodeMap;

public class Parameters extends ArrayList<Parameter> {
    public static final double EPSILON = 1;
    
    public Parameters() {
        super();
    }
    
    public Parameters(ArrayList<Parameter> parameters) {
        super(parameters.size());
        for (Parameter p: parameters) this.add(p);
    }
    
    public Vector vector() {
        Vector out = new Vector(this.dimension());
        int index = 0;
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).locked) {
                out.set(index++, this.get(i).get());
            }
        }
        return out;
    }
    
    public int dimension() {
        int count = 0;
        for (Parameter param: this) {
            if (!param.locked) {
                count++;
            }
        }
        return count;
    }
    
    public Vector dx(int index) {
        Vector dxi = new Vector(this.dimension());
        for (int i = 0; i < this.dimension(); i++) {
            dxi.set(i, i == index ? EPSILON : 0);
        }
        return dxi;
    }
    
    public void update(Vector v) {
        if (v.dimension() != this.dimension()) {
            throw new IllegalArgumentException
                (String.format
                     ("bad dimension in UPDATE: (%d,%d)",v.dimension(), size()));
        }
        
        int index = 0;
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).locked) {
                this.get(i).value = v.get(index++);
            }
        }
    }
    
    public String xml() {
        String out = "";
        for (Parameter param: this) {
            out += param.xml();
        }
        return "<parameters>" + out + "</parameters>";
    }
    
    public static Parameters load(NodeList paramList) {
        Parameters params = new Parameters();
        for (int i = 0; i < paramList.getLength(); i++) {
            NamedNodeMap attributes = paramList.item(i).getAttributes();
            boolean locked = Boolean.parseBoolean(attributes.getNamedItem("locked").getNodeValue());
            double value = Double.parseDouble(attributes.getNamedItem("value").getNodeValue());
            params.add(new Parameter(value, locked));
        }
        return params;
    }
}
