package gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

// import java.io.*;
// import java.net.*;

import euclid.*;
import csp.*;
import neo.*;

public class User {
    private static final String BIG_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZΓΔΘΛΞΠΣΦΨΩ";
    
    final String LOCAL_HOST = "127.0.0.1";
    final int PORT = 5000;
    
    private Tool tool;
    private boolean ctrl;
    private Space context;
    private Solver solver;
    private HashSet<GeometricObject> selected;
    
    private JFrame frame;
    private JPanel sketchPanel, toolPanel, constraintsPanel;
    private JLabel error;
    
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    
    int points = 0;
    
    private Inspector inspector;
    private ConstraintEditor editor;
    
    private enum Tool {
        MOVE,
        POINT, LINE, CIRCLE,
        MIDPOINT
    }
    
    public User() {
        frame = new JFrame("The Coxeter Euclidean Geometry System");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        toolPanel = new ToolPanel();
        sketchPanel = new SketchPanel();
        constraintsPanel = new ConstraintsPanel();
        selected = new HashSet<GeometricObject>();
        context = new Space();
        solver = new Solver(context);
        
        error = new JLabel();
        inspector = new Inspector();
        editor = new ConstraintEditor();
        
        toolPanel.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                ctrl = e.isMetaDown();
            }
            
            public void keyReleased(KeyEvent e) {
                ctrl = false;
            }
            
            public void keyTyped(KeyEvent e) {}
        });
        
        frame.add(toolPanel, BorderLayout.PAGE_START);
        frame.add(sketchPanel, BorderLayout.CENTER);
        frame.add(constraintsPanel, BorderLayout.PAGE_END);
        
        ctrl = false;
        tool = Tool.MOVE;
    }
    
    public static void main(String args) {
        User user = new User();
        user.run();
    }
    
    public void run() {
        frame.setVisible(true);
        
        try {
            socket = new Socket(LOCAL_HOST, PORT);
            output = new PrintWriter(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error in RUN method. Attempting to get ports.");
            e.printStackTrace();
        }
        
        Thread inpectorThread = new Thread(inspector);
        Thread editorThread = new Thread(editor);
        
        inpectorThread.start();
        editorThread.start();
        
        while (true) {
            frame.repaint();
            error.setText("ε = " + context.constraints.error());
            
            if (context.constraints.size() > 0)
                solver.solve();
            
            try {
                output.println(context.xml());
                output.flush();
                System.out.println(input.readLine());
            } catch (Exception e) {
                System.out.println("User.java/User.run.whie.try.System.out.println");
            }
            
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }
        }
    }
    
    public String newName() {
        return new String(new char[] { BIG_LETTERS.charAt(points++) });
    }
            
    public GeometricObject[] getSelected() {
        return selected.toArray(new GeometricObject[selected.size()]);
    }
    
    class ToolPanel extends CoxeterPanel {
        ToolPanel() {
            setFocusable(true);
            requestFocusInWindow();
            setPreferredSize(new Dimension(800, 50));
            
            addButton(new CoxeterButton("cursor.png"), new ToolListener(Tool.MOVE));
            addButton(new CoxeterButton("point.png"), new ToolListener(Tool.POINT));
            addButton(new CoxeterButton("line.png"), new ToolListener(Tool.LINE));
            
            addButton(new JButton("midpoint"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Line line = (Line) getSelected()[0];
                    context.objects.add(new Point(line, newName()));
                }
            });
            
            addButton(new CoxeterButton("circle.png"), new ToolListener(Tool.CIRCLE));
            
            addButton(new CoxeterButton("pin.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (GeometricObject object: selected)
                        object.pin();
                }
            });
            
            addButton(new JButton("Inspector"), new ActionListener() {
                boolean visible = false;
                
                public void actionPerformed(ActionEvent e) {
                    inspector.setVisible(visible = !visible);
                }
            });
            
            addButton(new JButton("Constraints"), new ActionListener() {
                boolean visible = false;
                
                public void actionPerformed(ActionEvent e) {
                    editor.setVisible(visible = !visible);
                }
            });
            
            addButton(new JButton("Delete"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (GeometricObject object: getSelected()) {
                        context.objects.remove(object);
                    }
                }
            });
            
            addButton(new JButton("Load"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    context = Space.load(new File("../context.xml"));
                    solver.update(context);
                }
            });
            
            addButton(new JButton("Save"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    context.save("../context.xml");
                }
            });
            
            error = new JLabel();
        }
    }
    
    class ToolListener implements ActionListener {
        Tool t;
        
        public ToolListener(Tool t) {
            this.t = t;
        }
        
        public void actionPerformed(ActionEvent e) {
            tool = t;
        }
    }
    
    class ConstraintEditor extends JFrame implements Runnable {
        JList<Constraint> list;
        DefaultListModel<Constraint> model;
        JButton delete;
        
        public ConstraintEditor() {
            super("Constraint Editor");
            setSize(300, 300);
            
            delete = new JButton("Delete");
            delete.setFocusable(false);
            
            model = new DefaultListModel<Constraint>();
            list = new JList<Constraint>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(0);
            list.setVisibleRowCount(5);
            JScrollPane scrollPane = new JScrollPane(list);
            add(scrollPane, BorderLayout.CENTER);
            add(delete, BorderLayout.PAGE_END);
            
            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = list.getSelectedIndex();
                    if (index == -1) {
                        return;
                    }
                    context.constraints.remove(index);
                }
            });
        }
        
        public void run() {
            while (true) {
                repaint();
                
                ArrayList<Constraint> c = context.constraints;
                model.setSize(c.size());
                for (int i = 0; i < c.size(); i++) {
                    model.setElementAt(c.get(i), i);
                }
                
                try { Thread.sleep(10); }
                catch (Exception e) {};
            }
        }
    }
    
    class Inspector extends JFrame implements Runnable {
        String information;
        JTextArea text;
        
        public Inspector() {
            super("Inspector");
            setSize(300, 300);
            information = "";
            text = new JTextArea(information);
            add(text);
        }
        
        public void update() {
            information = "";
            for (GeometricObject object: selected) {
                information += object.inspect();
            }
            text.setText(information);
        }
        
        public void run() {
            while (true) {
                update();
                repaint();
                
                try { Thread.sleep(10); }
                catch (Exception e) {};
            }
        }
    }
    
    public class SketchPanel extends JPanel {
        boolean l, c;
        Point P, Q;
        
        int x, y;
        
        SketchPanel() {
            setFocusable(true);
            setDoubleBuffered(true);
            requestFocusInWindow(true);
            setBackground(Color.WHITE);
            
            l = true;
            c = true;
            
            addMouseListener(new AddListener());
            addMouseListener(new SelectionListener());
            addMouseMotionListener(new DragListener());
        }
        
        public Point insertPoint(int x, int y) {
            for (Point point: context.objects.points()) {
                if (point.isAt(x, y)) {
                    return point;
                }
            }
            
            for (Line a: context.objects.lines()) {
                for (Line b: context.objects.lines()) {
                    if (!a.intersection(b).isAt(x, y))
                        continue;
                    
                    Point out = new Point(a, b, newName());
                    context.objects.add(out);
                    return out;
                }
            }
            
            Parameter p = new Parameter(x);
            Parameter q = new Parameter(y);
            context.parameters.add(p);
            context.parameters.add(q);
            Point out = new Point(p, q, newName());
            context.objects.add(out);
            return out;
        }
        
        public void insertLine(int x, int y) {
            if (l) {
                P = insertPoint(x, y);
                Q = new Point(x, y);
                context.objects.add(new Line(P, Q));
                l = false;
            } else {
                Line line = (Line) context.objects.remove(context.objects.size() - 1);
                context.objects.add(new Line(line.first(), insertPoint(x, y)));
                l = true;
            }
        }
        
        public void insertCircle(int x, int y) {
            if (c) {
                P = insertPoint(x, y);
                Q = new Point(x, y);
                context.objects.add(new Circle(P, Q));
                c = false;
            } else {
                Circle obj = (Circle) context.objects.remove(context.objects.size() - 1);
                context.objects.add(new Circle(obj.center(), insertPoint(x, y)));
                c = true;
            }
        }
        
        class AddListener extends MouseAdapter {
            public void mouseClicked(MouseEvent e) {
                if (ctrl && tool == Tool.MOVE) {
                    for (GeometricObject object: context.objects) {
                        if (object.isAt(e.getX(), e.getY())) {
                            object.select();
                            selected.add(object);
                        }
                    }
                }
                
                x = e.getX();
                y = e.getY();
                
                switch (tool) {
                    case POINT:
                        insertPoint(e.getX(), e.getY()); 
                        break;
                    case LINE:
                        insertLine(e.getX(), e.getY());
                        break;
                    case CIRCLE:
                        insertCircle(e.getX(), e.getY());
                        break;
                    case MOVE:
                        break;
                }
                
                if (!ctrl) {
                    for (GeometricObject object: selected)
                        object.unselect();
                    selected.clear();
                }
            }
        }
        
        class SelectionListener extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                if (tool != Tool.MOVE) return;
                
                if (selected.size() == 0) {
                    for (GeometricObject object: context.objects) {
                        if (object.isAt(e.getX(), e.getY())) {
                            object.select();
                            selected.add(object);
                            break;
                        }
                    }
                }
                
                x = e.getX();
                y = e.getY();
            }
            
            public void mouseReleased(MouseEvent e) {
                x = 0;
                y = 0;
            }
        }
        
        class DragListener extends MouseAdapter {
            public void mouseDragged(MouseEvent e) {
                if (tool != Tool.MOVE) return;
                
                for (GeometricObject object: selected) {
                    object.update(e.getX() - x, e.getY() - y);
                }
                
                x = e.getX();
                y = e.getY();
            }
            
            public void mouseMoved(MouseEvent e) {
                if ((!l && tool == Tool.LINE) || (!c && tool == Tool.CIRCLE)) {
                    Q.update(e.getX() - x, e.getY() - y);
                    x = e.getX();
                    y = e.getY();
                }
            }
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString(error.getText(), 20, 20);
            for (GeometricObject object: context.objects) {
                object.draw(g);
            }
        }
    }
    
    class ConstraintsPanel extends CoxeterPanel {
        ConstraintsPanel() {
            addButton(new CoxeterButton("parallel.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Line a = (Line) getSelected()[1];
                    Line b = (Line) getSelected()[0];
                    context.constraints.add(new Parallel(a, b));
                }
            });
            
            addButton(new CoxeterButton("perp.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Line a = (Line) getSelected()[1];
                    Line b = (Line) getSelected()[0];
                    context.constraints.add(new Perpendicular(a, b));
                }
            });
            
            addButton(new CoxeterButton("length.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Line a = (Line) getSelected()[1];
                    Line b = (Line) getSelected()[0];
                    context.constraints.add(new EqualLength(a, b));
                }
            });
        }
    }
}