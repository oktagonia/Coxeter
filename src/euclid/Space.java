package euclid;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import csp.Constraints;
import csp.Parameters;
import csp.Constraint;
import euclid.Parameter;
import euclid.GeometricObject;
import euclid.Line;
import euclid.Point;

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

public class Space {
    public Constraints constraints;
    public Parameters parameters;
    public Objects objects;
    
    public Space() {
        constraints = new Constraints();
        parameters = new Parameters();
        objects = new Objects();
    }
    
    public void update(Space space) {
        this.constraints = space.constraints;
        this.parameters = space.parameters;
        this.objects = space.objects;
    }
    
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
    
    public void save(String fname) {
        try {
            FileWriter fw = new FileWriter(fname);
            fw.write(xml());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
    
    public static Space load(String data) {
        try {
            return load(new InputSource(new StringReader(data)));
        } catch (Exception e) {
            System.out.println("Error in (LOAD String)");
            e.printStackTrace();
        }
        return null;
    }
    
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
