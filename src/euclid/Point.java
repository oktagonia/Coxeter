package euclid;

import neo.Vector;
import java.awt.Graphics;
import org.w3c.dom.Node;

public class Point extends GeometricObject {
    Parameter P, Q;
    public static final int RADIUS = 4;
    
    private PointType type;
    private Line A;
    private Line B;
    
    public enum PointType {
        NORMAL, MIDPOINT, INTERSECTION
    }
    
    public Point(Parameter P, Parameter Q, String label) {
        super(label);
        type = PointType.NORMAL;
        this.P = P;
        this.Q = Q;
    }
    
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
        P.value += x;
        Q.value += y;
    }
    
    public void update(Vector v) {
        update((int) v.get(0), (int) v.get(1));
    }
    
    public Parameter getX() {
        return P;
    }
    
    public Parameter getY() {
        return Q;
    }
    
    public int first() {
        return (int) P.get(); 
    }
    
    public int second() {
        return (int) Q.get();
    }
    
    public Vector getPoint() {
        return new Vector(first(), second());
    }
    
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
