package euclid;

import java.util.ArrayList;

import csp.*;
import neo.*;
import gui.*;
import euclid.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class Objects extends ArrayList<GeometricObject> {
    public ArrayList<Line> lines() {
        ArrayList<Line> lines = new ArrayList<Line>();
        for (GeometricObject object: this) {
            if (object instanceof Line) {
                lines.add((Line) object);
            }
        }
        
        return lines;
    }
    
    public ArrayList<Point> points() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (GeometricObject object: this) {
            if (object instanceof Point) {
                points.add((Point) object);
            }
        }
        
        return points;
    }
    
    public static Objects load(NodeList objecList, Space context) {
        Objects objects = new Objects();
        for (int i = 0; i < objecList.getLength(); i++) {
            Node node = objecList.item(i);
            if (node.getLocalName() == null) continue;
            
            NamedNodeMap attributes = node.getAttributes();
            String kind = node.getLocalName();
            boolean locked = Boolean.parseBoolean(attributes.getNamedItem("locked").getNodeValue());
            
            if (kind == "point") {
                String type = attributes.getNamedItem("type").getNodeValue();
                String label = attributes.getNamedItem("label").getNodeValue();
                int a, b;
                Parameter p, q;
                Line A, B;
                Point point;
                
                switch (type) {
                    case "NORMAL":
                        a = Integer.parseInt(attributes.getNamedItem("A").getNodeValue());
                        b = Integer.parseInt(attributes.getNamedItem("B").getNodeValue());
                        p = context.parameters.get(a);
                        q = context.parameters.get(b);
                        point = new Point(p, q, label);
                        break;
                    case "MIDPOINT":
                        a = Integer.parseInt(attributes.getNamedItem("A").getNodeValue());
                        A = (Line) objects.get(a);
                        point = new Point(A, label);
                        break;
                    case "INTERSECTION":
                        a = Integer.parseInt(attributes.getNamedItem("A").getNodeValue());
                        b = Integer.parseInt(attributes.getNamedItem("B").getNodeValue());
                        A = (Line) objects.get(a);
                        B = (Line) objects.get(b);
                        point = new Point(A, B, label);
                        break;
                    default:
                        point = new Point(0, 0);
                        break;
                }
                
                point.setLocked(locked);
                objects.add(point);
                continue;
            }
            
            int a = Integer.parseInt(attributes.getNamedItem("A").getNodeValue());
            int b = Integer.parseInt(attributes.getNamedItem("B").getNodeValue());
            
            if (kind == "line") {
                Line line = new Line((Point) objects.get(a), (Point) objects.get(b));
                line.setLocked(locked);
                objects.add(line);
            } else if (kind == "line") {
                Circle circle = new Circle((Point) objects.get(a), (Point) objects.get(b));
                circle.setLocked(locked);
                objects.add(circle);
            }
        }
        
        return objects;
    }
}