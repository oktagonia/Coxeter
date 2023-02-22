package euclid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;

import csp.Constraints;
import csp.Parameters;
import csp.Constraint;
import euclid.Parameter;
import euclid.GeometricObject;
import euclid.Line;
import euclid.Point;
import gui.ServerInstruction;
import gui.ServerMessage;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

/**
 * The common context between all the users.
 * Includes the objects, parameters, and constraints
 */
public class Space {
    private static final String BIG_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZΓΔΘΛΞΠΣΦΨΩ";

    public Constraints constraints;
    public Parameters parameters;
    public Objects objects;
    public HashSet<GeometricObject> selections;
    public int points;

    public int X, Y;
    public boolean l = true;
    public boolean c = true;
    public Point P, Q;
    
    public Space() {
        constraints = new Constraints();
        parameters = new Parameters();
        objects = new Objects();
        selections = new HashSet<GeometricObject>();
    }
    
    /**
     * Update the space
     */
    public void update(Space space) {
        this.constraints = space.constraints;
        this.parameters = space.parameters;
        this.objects = space.objects;
        this.selections = space.selections;
    }

    /**
     * Return the objects that are selected.
     * @return selected
     */
    public GeometricObject[] getSelected() {
        return selections.toArray(new GeometricObject[selections.size()]);
    }

    /**
     * Make a new label
     * @return name
     */
    public String newName() {
        return new String(new char[] { BIG_LETTERS.charAt(points++) });
    }

    /**
     * Return the message that tells the client
     * what to display.
     * @return the message.
     */
    public ServerMessage message() {
        ServerMessage msg = new ServerMessage();
        for (GeometricObject object: objects) {
            msg.add(object.recipe());
        }
        return msg;
    }
    
    /**
     * Return an xml representation of the context
     * @return xml
     */
    public String xml() {
        String out = "";
        out += constraints.xml(this);
        out += parameters.xml();
        
        String o = "<objects>";
        for (GeometricObject object: objects) {
            o += object.xml(this);
        }
        o += "</objects>";
        
        out += o;
        return "<?xml version=\"1.0\"?><space>" + out + "</space>";
    }
    
    /**
     * Save the context to a file
     */
    public void save(String fname) {
        try {
            FileWriter fw = new FileWriter(fname);
            fw.write(xml());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a new space from an input source
     * @return a new space
     */
    public static Space load(InputSource is) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            document.getDocumentElement().normalize();
            
            Element space = (Element) document.getDocumentElement();
            Element parameters = (Element) space.getElementsByTagName("parameters").item(0);
            Element constraints = (Element) space.getElementsByTagName("constraints").item(0);
            Element objects = (Element) space.getElementsByTagName("objects").item(0);
            
            NodeList paramList = parameters.getElementsByTagName("parameter");
            NodeList constList = constraints.getElementsByTagName("constraint");
            NodeList objecList = objects.getChildNodes();
            
            Space context = new Space();
            
            context.parameters = Parameters.load(paramList);
            context.objects = Objects.load(objecList, context);
            context.constraints = Constraints.load(constList, context);
            
            return context;
        } catch (Exception e) {
            System.out.println("Error in (LOAD InputSource)");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Make a new space from a string
     * @return Space
     */
    public static Space load(String data) {
        try {
            return load(new InputSource(new StringReader(data)));
        } catch (Exception e) {
            System.out.println("Error in (LOAD String)");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Make a new space from a file
     * @return Space
     */
    public static Space load(File file) {
        try {
            return load(new InputSource(new FileInputStream(file)));
        } catch (Exception e) {
            System.out.println("Error in (LOAD File)");
            e.printStackTrace();
        }
        return null;
    }
}
