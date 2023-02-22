package gui;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import euclid.Space;
import euclid.Point;
import euclid.Parameter;

public class SketchPanel extends JPanel {
    private Space context;
    
    public SketchPanel(Space context) {
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(600, 775));
        this.context = context;
        
        addMouseListener(new Mouse());
    }
    
    public void paintComponent(Graphics g) {
        for (int i = 0; i < context.numberOfObjects(); i++) {
            context.getObject(i).draw(g);
        }
    }
    
    class Mouse implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Mouse click!");
            
            Parameter p = new Parameter(e.getX());
            Parameter q = new Parameter(e.getY());
            context.addParameter(p);
            context.addParameter(q);
            context.addObject(new Point(p, q, context));
            desired--;
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
}
