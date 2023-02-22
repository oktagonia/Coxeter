package csp;

import java.util.ArrayList;
import euclid.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class Constraints extends ArrayList<Constraint> {
    public double error() {
        double sum = 0;
        double error = 0;
        for (int i = 0; i < this.size(); i++) {
            error = this.get(i).error();
            if (error <= 1) continue;
            sum += error;
        }
        return sum;
    }
    
    public String xml(Space context) {
        String out = "";
        for (Constraint constraints: this) {
            out += constraints.xml(context);
        }
        return "<constraints>" + out + "</constraints>";
    }
    
    public static Constraints load(NodeList constList, Space context) {
        Constraints constraints = new Constraints();
        for (int i = 0; i < constList.getLength(); i++) {
            NamedNodeMap attributes = constList.item(i).getAttributes();
            
            String type = attributes.getNamedItem("type").getNodeValue();
            int a = Integer.parseInt(attributes.getNamedItem("A").getNodeValue());
            int b = Integer.parseInt(attributes.getNamedItem("B").getNodeValue());
            
            GeometricObject A = context.objects.get(a);
            GeometricObject B = context.objects.get(b);
            
            switch (type) {
                case "EqualLength":
                    constraints.add(new EqualLength((Line) A, (Line) B));
                    break;
                case "Perpendicular":
                    constraints.add(new Perpendicular((Line) A, (Line) B));
                    break;
                case "Parallel":
                    constraints.add(new Parallel((Line) A, (Line) B));
                    break;
            }
        }
        
        return constraints;
    }
}