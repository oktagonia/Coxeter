package euclid;

import gui.ServerInstruction;
import gui.ServerMessage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

import org.w3c.dom.Node;

/**
 * The main objects of our program
 */
public abstract class GeometricObject {
    protected boolean visible;
    protected boolean locked;
    protected boolean selected;
    protected String label;
    
    public GeometricObject(String label) {
        this.visible = true;
        this.locked = false;
        this.selected = false;
        this.label = label;
    }
    
    public GeometricObject() {
        this.visible = true;
        this.locked = false;
        this.selected = false;
        this.label = "";
    }
    
    /**
     * each object has a label
     */
    abstract public String label();
        
    /**
     * is this object selected?
     * @return selected
     */
    public boolean selected() {
        return selected;
    }

    /**
     * Setter for selected
     */
    public void setSelected(boolean bool) {
        selected = bool;
    }
    
    /**
     * select this object
     */
    public void select() {
        selected = true;
    }
    
    /**
     * unselect this object
     */
    public void unselect() {
        selected = false;
    }
    
    /**
     * Is this locked?.
     * Locking an object prevents it from being
     * changed during constraint solving.
     * @return locked
     */
    public boolean locked() {
        return locked;
    }
    
    /**
     * Inspect this object
     * @return the result of inspection
     */
    abstract public String inspect();
    
    /**
     * Set locked
     */
    public void setLocked(boolean b) {
        locked = b;
    }
    
    /**
     * Lock this object
     */
    abstract public void lock();

    /**
     * unlock this object.
     */
    abstract public void unlock();
    
    /**
     * pin this object
     */
    public void pin() {
        if (locked) {
            this.unlock();
        } else {
            this.lock();
        }
    }
    
    /**
     * Move this object
     */
    abstract public void update(int x, int y);

    /**
     * Check for collisions with the mouse
     * @return whether the mouse is on the shape
     */
    abstract public boolean isAt(int x, int y);

    /**
     * Render it onto the screen
     */
    abstract public void render(Graphics g);

    /**
     * Provide a recipe for drawing this object
     * @return a ServerInstruction for drawing
     */
    abstract public ServerInstruction recipe();

    /**
     * Return the type of the object
     * @return the type.
     */
    abstract public ShapeType type();
    
    /**
     * Convert to xml representation
     * @return xml
     */
    abstract public String xml(Space context);

    /**
     * Selection xml
     * @return string
     */
    abstract public String selectionXml();
    
    /**
     * Convert to string
     * @return string represetation
     */
    public String toString() {
        return label();
    }
    
    /**
     * Draw the object onto the screen
     */
    public void draw(Graphics g) {
        Graphics2D h = (Graphics2D) g;
        h.setColor(Color.BLACK);
        if (selected) h.setColor(Color.BLUE);
        h.setStroke(new BasicStroke(2));
        if (visible) {
            this.render(h);
        }
    }
}
