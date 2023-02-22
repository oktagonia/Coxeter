package gui;

import euclid.*;

/** Instruction from the server to the client
 */
public class ServerInstruction {
    ShapeType type;
    int x1, y1;
    int x2, y2;
    boolean selected;
    String label;

    public ServerInstruction(String[] instruction) {
        type = ShapeType.valueOf(instruction[0]);
        x1 = Integer.parseInt(instruction[1]);
        y1 = Integer.parseInt(instruction[2]);
        x2 = Integer.parseInt(instruction[3]);
        y2 = Integer.parseInt(instruction[4]);
        selected = Boolean.parseBoolean(instruction[5]);
        label = instruction[6];
    }

    public ServerInstruction(ShapeType type, 
                             int x1, int y1,
                             int x2, int y2,
                             boolean selected,
                             String label) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.selected = selected;
        this.label = label;
    }

    public String toString() {
        return String.format("%s %d %d %d %d %b %s",
                             type, x1, y1, x2, y2, selected, label);
    }

    /**
     * Return the object specified by this instruction
     * @return a GeometircObject
     */
    public GeometricObject compile() {
        if (type == ShapeType.POINT) {
            Point point = new Point(x1, y1, label);
            point.setSelected(selected);
            return point;
        } else if (type == ShapeType.LINE) {
            Line line = new Line(new Point(x1, y1), new Point(x2, y2));
            line.setSelected(selected);
            return line;
        } else if (type == ShapeType.CIRCLE) {
            Circle circle = new Circle(new Point(x1, y1), new Point(x2, y2));
            circle.setSelected(selected);
            return circle;
        }
        return null;
    }
}
