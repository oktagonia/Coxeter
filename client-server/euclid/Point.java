package euclid;

import gui.ServerInstruction;
import gui.ServerMessage;

import neo.Vector;
import java.awt.Graphics;
import org.w3c.dom.Node;

public class Point extends GeometricObject {
    Parameter P, Q;
    public static final int RADIUS = 4;
    
    private PointType type;
    private Line A;
    private Line B;

    private long time = 0;
    
    public enum PointType {
        NORMAL, MIDPOINT, INTERSECTION
    }
    
    public Point(Parameter P, Parameter Q, String label) {
        super(label);
        type = PointType.NORMAL;
        this.P = P;
        this.Q = Q;
    }
    
    // Point from intersection of two lines
    public Point(Line a, Line b, String label) {
        super(label);
        this.type = PointType.INTERSECTION;
        this.P = new Parameter(() -> {
            return a.intersection(b).getX().get();
        });
        
        this.Q = new Parameter(() -> {
            return a.intersection(b).getY().get();
        });
        
        this.A = a;
        this.B = b;
    }
    
    // Midpoint of a line
    public Point(Line line, String label) {
        super(label);
        type = PointType.MIDPOINT;
        this.P = new Parameter(() -> { return line.midpoint().get(0); });
        this.Q = new Parameter(() -> { return line.midpoint().get(1); });
        this.A = line;
    }
    
    public Point(double x, double y) {
        super();
        this.P = new Parameter(x);
        this.Q = new Parameter(y);
    }

    public Point(double x, double y, String label) {
        super(label);
        this.P = new Parameter(x);
        this.Q = new Parameter(y);
    }

    public ShapeType type() {
        return ShapeType.POINT;
    }

    public ServerInstruction recipe() {
        ShapeType type = ShapeType.POINT;
        int x1 = first();
        int y1 = second();
        int x2 = -1;
        int y2 = -1;
        boolean selected = selected();
        String label = label();
        return new ServerInstruction(type, x1, y1, x2, y2, selected, label);
    }
    
    public void lock() {
        this.locked = true;
        P.locked = true;
        Q.locked = true;
    }
    
    public void unlock() {
        this.locked = false;
        P.locked = false;
        Q.locked = false;
    }
    
    public String label() {
        return label;
    }
    
    public void update(int x, int y) {
        long t = System.nanoTime();
        if (t - time > 5000000) {
            P.value += x;
            Q.value += y;
        }
        time = t;
    }
    
    public void update(Vector v) {
        update((int) v.get(0), (int) v.get(1));
    }
    
    /**
     * Return the first parameter
     * @return the first paramter.
     */
    public Parameter getX() {
        return P;
    }
    
    /**
     * Return the second parameter
     * @return the second paramter.
     */
    public Parameter getY() {
        return Q;
    }
    
    /**
     * Return the x-cordinate
     * @return x
     */
    public int first() {
        return (int) P.get(); 
    }
    
    /**
     * Return the y-coordinate
     * @return y.
     */
    public int second() {
        return (int) Q.get();
    }
    
    /**
     * Retrun a vector representing this point
     */
    public Vector getPoint() {
        return new Vector(first(), second());
    }
    
    /**
     * Distance between two points
     * @return distance.
     */
    public double distance(Point that) {
        return this.getPoint().minus(that.getPoint()).magnitude();
    }
    
    public String inspect() {
        return String.format("Point:\n  « %d, %d »\n locked: %b\n", first(), second(), locked());
    }
    
    public String xml(Space context) {
        String str = "";
        if (type == PointType.INTERSECTION) {
            int a = context.objects.indexOf(A);
            int b = context.objects.indexOf(B);
            str = String.format("A='%d' B='%d'", a, b);
        } else if (type == PointType.NORMAL){
            int a = context.parameters.indexOf(P);
            int b = context.parameters.indexOf(Q);
            str = String.format("A='%d' B='%d'", a, b);
        } else if (type == PointType.MIDPOINT) {
            int a = context.objects.indexOf(A);
            str = String.format("A='%d'", a);
        }
        return String.format("<point type='%s' %s label='%s' locked='%b'></point>", type, str, label(), locked);
    }

    public String selectionXml() {
        return String.format("<selection type='%s' x='%d' y='%d' locked='%b'>",
                ShapeType.POINT, first(), second(), locked());
    }
    
    public boolean isAt(int x, int y) {
        return this.distance(new Point(x, y)) < 1.5 * RADIUS;
    }
    
    public void render(Graphics g) {
        int diam = 2*RADIUS;
        int r = RADIUS;
        g.drawString(label(), first() + diam, second() + diam);
        g.fillOval(this.first()-r, this.second()-r, diam, diam);
    }
}
