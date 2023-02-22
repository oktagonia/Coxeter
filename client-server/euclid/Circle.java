package euclid;

import gui.ServerInstruction;
import gui.ServerMessage;

import java.awt.Graphics;
import org.w3c.dom.Node;

/**
 * Circle object
 */
public class Circle extends GeometricObject {
    private Point center, circum;
    
    public Circle(Point center, Point circum) {
        super();
        this.center = center;
        this.circum = circum;
    }

    /**
     * Type of the shape
     */
    public ShapeType type() {
        return ShapeType.CIRCLE;
    }

    /**
     * Return the instruction necessary to render
     * this circle.
     * @return An instruction from the server to the client.
     */
    public ServerInstruction recipe() {
        ShapeType type = ShapeType.CIRCLE;
        int x1 = center.first();
        int y1 = center.second();
        int x2 = circum.first();
        int y2 = circum.second();
        boolean selected = selected();
        String label = label();
        return new ServerInstruction(type, x1, y1, x2, y2, selected, label);
    }
    
    /**
     * The label of a circle is the labels of
     * its two constituent points and the circle symbol.
     * @return the label.
     */
    public String label() {
        return "⨀" + center.label() + circum.label();
    }
    
    /**
     * Move the circle
     */
    public void update(int x, int y) {
        center.update(x, y);
        circum.update(x, y);
    }
    
    /**
     * Lock the circle
     */
    public void lock() {
        this.locked = true;
        center.lock();
        circum.lock();
    }
    
    /**
     * unlock the circle
     */
    public void unlock() {
        this.locked = false;
        center.unlock();
        circum.unlock();
    }
    
    /**
     * Return the center of the circle
     * @return the center.
     */
    public Point center() {
        return center;
    }
    
    /**
     * Is the point <pre>(x, y)</pre> located on the circle?
     * @return true or false
     */
    public boolean isAt(int x, int y) {
        Point p = new Point(x, y);
        return Math.abs(center.distance(p) - radius()) < 4;
    }
    
    /**
     * Return the length of radius of the circle.
     * @return the radius.
     */
    public double radius() {
        return center.distance(circum);
    }
    
    /**
     * render the circle
     */
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
    
    /**
     * convert to xml
     * @return xml
     */
    public String xml(Space context) {
        int a = context.objects.indexOf(center);
        int b = context.objects.indexOf(circum);
        return String.format("<circle A='%d' B='%d' locked='%b'></circle", a, b, locked);
    }

    /**
     * selection xml
     * @return selection xml
     */
    public String selectionXml() {
        return String.format("<selection type='%s' x1='%d' y1='%d' x2='%d' y2='%d' locked='%b'>",
                ShapeType.CIRCLE, center.first(), center.second(), circum.first(), circum.second(), locked());
    }
}
