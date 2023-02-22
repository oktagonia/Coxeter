package gui;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import euclid.Space;

public class ToolsPanel extends JPanel {
    
    private JButton point, line, circle;
    private Space context;
    
    public ToolsPanel(Space context) {
        setFocusable(true);
        requestFocusInWindow();
        setPreferredSize(new Dimension(600, 40));
        
        this.context = context;
        
        point = new JButton("Point");
        line = new JButton("Line");
        circle = new JButton("Circle");
        
        this.add(point);
        this.add(line);
        this.add(circle);
        
        point.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                context.use("point");
            }
        });
        
        line.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                context.use("line");
            }
        });
        
        circle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                context.use("circle");
            }
        });
    }
}