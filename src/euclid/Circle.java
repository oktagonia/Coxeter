package euclid;

import java.awt.Graphics;
import org.w3c.dom.Node;

public class Circle extends GeometricObject {
    private Point center, circum;
    
    public Circle(Point center, Point circum) {
        super();
        this.center = center;
        this.circum = circum;
    }
    
    public String label() {
        return "⨀" + center.label() + circum.label();
    }
    
    public void update(int x, int y) {
        center.update(x, y);
        circum.update(x, y);
    }
    
    public void lock() {
        this.locked = true;
        center.lock();
        circum.lock();
    }
    
    public void unlock() {
        this.locked = false;
        center.unlock();
        circum.unlock();
    }
    
    public Point center() {
        return center;
    }
    
    public boolean isAt(int x, int y) {
        Point p = new Point(x, y);
        return Math.abs(center.distance(p) - radius()) < 4;
    }
    
    public double radius() {
        return center.distance(circum);
    }
    
    public void render(Graphics g) {
        int r = (int)center.distance(circum);
        g.drawOval(center.first()-r, center.second()-r, (int)2*r, (int)2*r);
    }
    
    public String inspect() {
        String p = String.format("  %s    = « %d, %d »\n", center.label(), center.first(), center.second());
        String q = String.format("  %s    = « %d, %d »\n", circum.label(), circum.first(), circum.second());
        String d = String.format("  |%s%s| = %f", center.label(), circum.label(), center.distance(circum));
        return "Circle:\n" + p + q + d + "\n\n";
    }
    
    public String xml(Space context) {
        int a = context.objects.indexOf(center);
        int b = context.objects.indexOf(circum);
        return String.format("<circle A='%d' B='%d' locked='%b'></circle", a, b, locked);
    }
}
