package gui;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;

/**
 * Special panel for unfocused buttons
 */
public class CoxeterPanel extends JPanel {
    public void addButton(JButton button, ActionListener al) {
        button.setFocusable(false);
        add(button);
        button.addActionListener(al);
    }
}
