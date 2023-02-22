package gui;

import java.io.File;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.awt.Image;

public class CoxeterButton extends JButton {
    
    private static final String PATH = "../assets/";
    
    public CoxeterButton(String path) {
        try {
            Image img = ImageIO.read(new File(PATH + path));
            setIcon(new ImageIcon(img));
        } catch(Exception e) {
            System.out.println("file does not exist");
        }
    }
}