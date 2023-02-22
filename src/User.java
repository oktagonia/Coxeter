package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import euclid.*;
import csp.*;

public class User {
    private JFrame frame;
    private JPanel sketchPanel, toolsPanel;
    private Space context;
    private String tool;
    private int desired, acquired;
    
    public User() {
        frame = new JFrame("Coxeter");
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        context = new Space();
        
        toolsPanel = new ToolsPanel(context);
        frame.add(toolsPanel, BorderLayout.PAGE_START);
        
        sketchPanel = new SketchPanel(context);
        frame.add(sketchPanel, BorderLayout.CENTER);
        
        tool = "point";
    }
    
    public void run() {
        frame.setVisible(true);
        Solver solver = new Solver(context.parameters, context.constraints);
        
        while (true) {
            frame.repaint();
            
            if (desired == 0) {
                GeometricObject obj = null;
                
                switch (tool) {
                    case "line":
                        obj = new Line(context.peek(1), context.peek(0), context);
                        desired = 2;
                        break;
                    case "circle":
                        obj = new Circle(context.peek(1),context.peek(0),context);
                        desired = 2;
                        break;
                    default: break;
                }
                
                if (obj != null) {
                    context.objects.add(obj);
                }
            }
            
            System.out.println("E = " + context.constraints.error());
            
            if (context.constraints.size() > 0) {
                solver.solve();
            }
            
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            };
        }
    }
    
    class ToolsPanel extends JPanel {
        private JButton move, point, line, circle;
        private JButton pointEq;
        private JButton lock, unlock;
        private Space context;
        
        ToolsPanel(Space context) {
            setFocusable(true);
            requestFocusInWindow();
            setPreferredSize(new Dimension(600, 40));
            
            this.context = context;
            
            move = new JButton("Move");
            point = new JButton("Point");
            line = new JButton("Line");
            circle = new JButton("Circle");
            
            pointEq = new JButton("EQUAL");
            
            lock = new JButton("LOCK");
            unlock = new JButton("UNLOCK");
            
            this.add(move);
            this.add(point);
            this.add(line);
            this.add(circle);
            this.add(pointEq);
            this.add(lock);
            this.add(unlock);
            
            move.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "move";
                    desired = 0;
                }
            });
            
            point.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "point";
                    desired = 0;
                }
            });
            
            line.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "line";
                    desired = 2;
                }
            });
            
            circle.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "circle";
                    desired = 2;
                }
            });
            
            pointEq.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "constraint";
                    context.constraints.add
                        (new EqualityConstraint(context.peek(1),
                                                context.peek(0)));
                }
            });
            
            lock.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "lock";
                }
            });
            
            unlock.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tool = "unlock";
                }
            });
        }
    }
    
    private boolean shape(String tool) {
        return 
            tool.equals("point") || 
            tool.equals("line")  || 
            tool.equals("circle");
    }
    
    class SketchPanel extends JPanel {
        private Space context;
        
        SketchPanel(Space context) {
            setDoubleBuffered(true);
            setFocusable(true);
            requestFocusInWindow(true);
            // setPreferredSize(new Dimension(600, 775));
            this.context = context;
            
            addMouseListener(new AddListener());
            addMouseListener(new SelectionListener());
            addMouseMotionListener(new DragListener());
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < context.objects.size(); i++) {
                context.objects.get(i).draw(g);
            }
        }
        
        class AddListener extends MouseAdapter {
            public void mouseClicked(MouseEvent e) {
                if (tool.equals("move")) return;
                
                if (tool.equals("lock")) {
                    for (Point point: context.points()) {
                        if (point.equals(e.getX(), e.getY())) {
                            point.lock();
                            break;
                        }
                    }
                    return;
                }
                
                if (tool.equals("unlock")) {
                    for (Point point: context.points()) {
                        if (point.equals(e.getX(), e.getY())) {
                            point.unlock();
                            break;
                        }
                    }
                    return;
                }
                
                Parameter p = new Parameter(e.getX());
                Parameter q = new Parameter(e.getY());
                context.parameters.add(p);
                context.parameters.add(q);
                if (shape(tool)) {
                    context.objects.add(new Point(p, q, context));
                    if (desired > 0) desired--;
                }
            }
        }
        
        class SelectionListener extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                if (tool.equals("move")) {
                    for (Point point: context.points()) {
                        if (point.equals(e.getX(), e.getY())) {
                            point.select();
                            break;
                        }
                    }
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (tool.equals("move")) {
                    for (Point point: context.points())
                        point.unselect();
                }
            }
        }
        
        class DragListener extends MouseAdapter {
            public void mouseDragged(MouseEvent e) {
                if (tool.equals("move")) {
                    for (Point point: context.points())
                        point.update(e.getX(), e.getY());
                }
            }
        }
    }
}
