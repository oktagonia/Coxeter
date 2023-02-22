package gui;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import euclid.*;
import csp.*;
import java.io.*;
import java.util.ArrayList;

public class ClientMessage {
    Tool tool;
    boolean ctrl;
    ClientInstruction instruction;


    public ClientMessage(String msg) {
        String[] tmp = msg.split(";");
        tool = Tool.valueOf(tmp[0]);
        instruction = new ClientInstruction(tmp[1]);
        ctrl = Boolean.parseBoolean(tmp[2]);
    }

    public ClientMessage(Tool tool, ClientInstruction instruction, boolean ctrl) {
        this.tool = tool;
        this.instruction = instruction;
        this.ctrl = ctrl;
    }

    public String toString() {
        return tool + ";" + instruction + ";" + ctrl;
    }
    
    /**
     * Execute the instruction and change the context
     */
    public void execute(Space context, Solver solver) {

        /* Euclidean geometry supports arithmetic, and
        arithmetic is Turing-complete. Since this is
        an implementation of Euclidean geometry, then
        it is also a Turing complete programming 
        language. */

        switch (instruction.operation) {
            // Mouse movements
            case CLICKED:  click(context);   break;
            case PRESSED:  press(context);   break;
            case RELEASED: release(context); break;
            case DRAGGED:  drag(context);    break;
            case MOVED:    move(context);    break;

            // Misc
            case MIDPOINT: midpoint(context); break;
            case DELETE:   delete(context);   break;

            // Loading and saving
            case LOAD:     load(context, solver); break;
            case SAVE:     save(context);         break;

            // Constraints
            case PIN:      pin(context); break;
            case ADD:      add(context); break;
            default: break;
        }
    }

    // MOUSE MOVEMENTS

    /// Clicking

    /**
     * Changes the context given a click
     */
    private void click(Space context) {
        int x = Integer.parseInt(instruction.arguments[0]);
        int y = Integer.parseInt(instruction.arguments[1]);

        context.X = x;
        context.Y = y;

        if (ctrl && tool == Tool.MOVE) {
            for (GeometricObject object: context.objects) {
                if (object.isAt(x, y)) {
                    object.select();
                    context.selections.add(object);
                }
            }
        }

        switch (tool) {
            case POINT:
                insertPoint(context, x, y); 
                break;
            case LINE:
                insertLine(context, x, y);
                break;
            case CIRCLE:
                insertCircle(context, x, y);
                break;
            case MOVE:
                break;
        }

        if (!ctrl) {
            for (GeometricObject object: context.selections)
                object.unselect();
            context.selections.clear();
        }
    }

    private Point insertPoint(Space context, int x, int y) {
        for (Point point: context.objects.points()) {
            if (point.isAt(x, y)) {
                return point;
            }
        }

        for (Line a: context.objects.lines()) {
            for (Line b: context.objects.lines()) {
                if (!a.intersection(b).isAt(x, y))
                    continue;

                Point out = new Point(a, b, context.newName());
                context.objects.add(out);
                return out;
            }
        }

        Parameter p = new Parameter(x);
        Parameter q = new Parameter(y);
        context.parameters.add(p);
        context.parameters.add(q);
        Point out = new Point(p, q, context.newName());
        context.objects.add(out);
        return out;
    }

    private void insertLine(Space context, int x, int y) {
        if (context.l) {
            context.P = insertPoint(context, x, y);
            context.Q = new Point(x, y);
            context.objects.add(new Line(context.P, context.Q));
            context.l = false;
        } else {
            Line line = (Line) context.objects.remove(context.objects.size() - 1);
            context.objects.add(new Line(line.first(), insertPoint(context, x, y)));
            context.l = true;
        }
    }

    public void insertCircle(Space context, int x, int y) {
        if (context.c) {
            context.P = insertPoint(context, x, y);
            context.Q = new Point(x, y);
            context.objects.add(new Circle(context.P, context.Q));
           context.c = false;
        } else {
            Circle obj = (Circle) context.objects.remove(context.objects.size() - 1);
            context.objects.add(new Circle(obj.center(), insertPoint(context, x, y)));
            context.c = true;
        }
    }

    /// Selections

    /**
     * Changes the context given a press
     */
    public void press(Space context) {
        int x = Integer.parseInt(instruction.arguments[0]);
        int y = Integer.parseInt(instruction.arguments[1]);

        context.X = x;
        context.Y = y;

        if (tool != Tool.MOVE) return;

        if (context.selections.size() == 0) {
            for (GeometricObject object: context.objects) {
                if (object.isAt(x, y)) {
                    object.select();
                    context.selections.add(object);
                    break;
                }
            }
        }
    }

    /**
     * Changes the context given a release
     */
    public void release(Space context) {
        int x = Integer.parseInt(instruction.arguments[0]);
        int y = Integer.parseInt(instruction.arguments[1]);
        context.X = x;
        context.Y = y;
    }

    /// Dragging

    /**
     * Changes the context given a drag
     */
    public void drag(Space context) {
        if (tool != Tool.MOVE) return;

        int x = Integer.parseInt(instruction.arguments[0]);
        int y = Integer.parseInt(instruction.arguments[1]);

        for (GeometricObject object: context.selections) {
            object.update(x - context.X, y - context.Y);
        }

        System.out.println(context.X);
        System.out.println(context.Y);

        context.X = x;
        context.Y = y;
    }

    /**
     * Changes the context given a move
     */
    public void move(Space context) {
        int x = Integer.parseInt(instruction.arguments[0]);
        int y = Integer.parseInt(instruction.arguments[1]);

        if ((!context.l && tool == Tool.LINE) || 
            (!context.c && tool == Tool.CIRCLE)) {
            context.Q.update(x - context.X, y - context.Y);
        }

        System.out.println(context.X);
        System.out.println(context.Y);

        context.X = x;
        context.Y = y;
    }

    /**
     * add a midpoint based on the selections
     */
    public void midpoint(Space context) {
        Line line = (Line) context.getSelected()[0];
        context.objects.add(new Point(line, context.newName()));
    }

    /**
     * Delete an object
     */
    public void delete(Space context) {
        for (GeometricObject object: context.getSelected()) {
            context.objects.remove(object);
        }
    }

    /**
     * load a save file
     */
    public void load(Space context, Solver solver) {
        context = Space.load(new File("../context.xml"));
        solver.update(context);
    }

    /**
     * save to a save file
     */
    public void save(Space context) {
        context.save("../context.xml");
    }

    /**
     * pin the object
     */
    public void pin(Space context) {
        for (GeometricObject object: context.selections)
            object.pin();
    }

    /**
     * add a constraint
     */
    public void add(Space context) {
        Line a = (Line) context.getSelected()[1];
        Line b = (Line) context.getSelected()[0];

        switch (instruction.arguments[0]) {
            case "parallel":
                context.constraints.add(new Parallel(a, b));
                break;
            case "perpendicular":
                context.constraints.add(new Perpendicular(a, b));
                break;
            case "length":
                context.constraints.add(new EqualLength(a, b));
                break;
        }
    }
}
