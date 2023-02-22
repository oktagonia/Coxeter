package euclid;

import gui.ServerInstruction;
import gui.ServerMessage;

import java.awt.Graphics;
import neo.Vector;
import org.w3c.dom.Node;

/**
 * Lines
 */
public class Line extends GeometricObject {
    private Point A, B;
    
    public Line(Point A, Point B) {
        super();
        this.A = A;
        this.B = B;
    }

    public ShapeType type() {
        return ShapeType.LINE;
    }

    public ServerInstruction recipe() {
        ShapeType type = ShapeType.LINE;
        int x1 = A.first();
        int y1 = A.second();
        int x2 = B.first();
        int y2 = B.second();
        boolean selected = selected();
        String label = label();
        return new ServerInstruction(type, x1, y1, x2, y2, selected, label);
    }
    
    public String label() {
        return A.label() + B.label();
    }

    public void update(int x, int y) {
        A.update(x, y);
        B.update(x, y);
    }
    
    /**
     * A vector representing this line
     */
    public Vector vector() {
        return B.getPoint().minus(A.getPoint());
    }
    
    /**
     * Lock this line and it's points
     */
    public void lock() {
        locked = true;
        A.lock();
        B.lock();
    }
    
    /**
     * Unlock this line and it's points
     */
    public void unlock() {
        locked = false;
        A.unlock();
        B.unlock();
    }
    
    /**
     * Midpoint of the line
     * @return A vector with the coordinates of the midpoint
     */
    public Vector midpoint() {
        double x = (A.first() + B.first()) / 2;
        double y = (A.second() + B.second()) / 2;
        return new Vector(x, y);
    }
    
    /**
     * The intersection of this line and that line.
     * @return a point that is at the intersection.
     */
    public Point intersection(Line that) {
        double x = (this.yIntercept() - that.yIntercept()) / (that.slope() - this.slope());
        double y = this.slope() * x + this.yIntercept();
        return new Point(x, y);
    }
    
    /**
     * The slope of this line
     * @return the slope
     */
    public double slope() {
        Line axis = new Line(new Point(0,0), new Point(0,1));
        Point left, right;
        
        if (axis.distance(A) < axis.distance(B)) {
            left = A;
            right = B;
        } else {
            left = B;
            right = A;
        }
        
        Vector v = left.getPoint();
        Vector u = right.getPoint();
        return (v.get(1) - u.get(1)) / (v.get(0) - u.get(0));
    }
    
    /**
     * Return the y-intercept of this line.
     * @return the y-intercept.
     */
    public double yIntercept() {
        return A.second() - (this.slope() * A.first());
    }
    
    /**
     * The first point of this line segment
     * @return the point.
     */
    public Point first() {
        return A;
    }
    
    /**
     * The second point of this line segment.
     * @return the point.
     */
    public Point second() {
        return B;
    }
    
    /**
     * The angle formed between this line and that line.
     * @return the angle
     */
    public double angle(Line that) {
        double d = this.distance() * that.distance();
        return Math.acos(this.vector().times(that.vector()) / d);
    }
    
    /**
     * Return the length of this line
     * @return the length
     */
    public double distance() {
        return A.distance(B);
    }
    
    /**
     * Return the length of the line which passes
     * through the point C and is perpendicular to
     * this line.
     * @return the length between point and line.
     */
    public double distance(Point C) {
        double a = A.distance(B);
        double b = B.distance(C);
        double c = C.distance(A);
        double s = (a + b + c) / 2;
        double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
        return 2 * area / distance();
    }
    
    public boolean isAt(int x, int y) {
        return distance(new Point(x, y)) < 2;
    }
    
    public String inspect() {
        String p = String.format("  %s    = « %d, %d »\n", A.label(), A.first(), A.second());
        String q = String.format("  %s    = « %d, %d »\n", B.label(), B.first(), B.second());
        String d = String.format("  |%s| = %f", this.label(), this.distance());
        return "Line:\n" + p + q + d + "\n\n";
    }
    
    public String xml(Space context) {
        int a = context.objects.indexOf(A);
        int b = context.objects.indexOf(B);
        return String.format("<line A='%d' B='%d' locked='%b'></line>", a, b, locked);
    }

    public String selectionXml() {
        return String.format("<selection type='%s' x1='%d' y1='%d' x2='%d' y2='%d' locked='%b'>",
                ShapeType.LINE, A.first(), A.second(), B.first(), B.second(), locked());
    }
    
    public void render(Graphics g) {
        g.drawLine(A.first(), A.second(), B.first(), B.second());
    }
}
