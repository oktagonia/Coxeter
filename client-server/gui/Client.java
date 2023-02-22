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

import java.io.*;
import java.net.*;

import euclid.*;
import csp.*;
import neo.*;

/**
 * The client
 */
public class Client {
    private static final String BIG_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZΓΔΘΛΞΠΣΦΨΩ";
    
    final String LOCAL_HOST = "127.0.0.1";
    final int PORT = 5000;
    
    private Tool tool;
    private boolean ctrl;
    private LinkedList<String> messages;
    private LinkedList<ClientMessage> senders;
    
    private JFrame frame;
    private JPanel sketchPanel, toolPanel, constraintsPanel;
    
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private long time;
    
    public Client() {
        frame = new JFrame("The Coxeter Euclidean Geometry System");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        toolPanel = new ToolPanel();
        sketchPanel = new SketchPanel();
        constraintsPanel = new ConstraintsPanel();
        messages = new LinkedList<String>();
        senders = new LinkedList<ClientMessage>();
        
        toolPanel.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                ctrl = e.isMetaDown() || e.isControlDown();
            }
            
            public void keyReleased(KeyEvent e) {
                ctrl = false;
            }
            
            public void keyTyped(KeyEvent e) {}
        });
        
        frame.add(toolPanel, BorderLayout.PAGE_START);
        frame.add(sketchPanel, BorderLayout.CENTER);
        frame.add(constraintsPanel, BorderLayout.PAGE_END);
        
        time = 0;
        ctrl = false;
        tool = Tool.MOVE;
    }
    
    public static void main(String args) {
        Client client = new Client();
        client.run();
    }
    
    /**
     * Run the client
     */
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

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    frame.repaint();
                    try {
                        messages.add(input.readLine());
                    } catch (Exception e) {
                        System.out.println("Error in run");
                    }
                }
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Send a message to the server.
     * Send a message every 5 ms
     */
    public void send(Operation op, String[] args) {
        try {
            long x = System.nanoTime();
            if (x - time > 5000000 || op == Operation.CLICKED) {
                output.println(new ClientMessage(tool, new ClientInstruction(op, args), ctrl));
                output.flush();
            }
            time = x;
        } catch (Exception e) {
            System.out.println("Error in SEND");
            e.printStackTrace();
        }
    }
            
    /**
     * Toolpanel has a bunch of buttons for tools
     */
    class ToolPanel extends CoxeterPanel {
        ToolPanel() {
            setFocusable(true);
            requestFocusInWindow();
            setPreferredSize(new Dimension(800, 50));
            
            addButton(new CoxeterButton("cursor.png"), new ToolListener(Tool.MOVE));
            addButton(new CoxeterButton("point.png"), new ToolListener(Tool.POINT));
            addButton(new CoxeterButton("line.png"), new ToolListener(Tool.LINE));
            addButton(new CoxeterButton("circle.png"), new ToolListener(Tool.CIRCLE));
            addButton(new JButton("midpoint"),
                      new CoxeterListener(Operation.MIDPOINT, new String[] {}));
            addButton(new CoxeterButton("pin.png"),
                      new CoxeterListener(Operation.PIN, new String[] {}));
            addButton(new JButton("Delete"),
                      new CoxeterListener(Operation.DELETE, new String[] {}));
        }
    }

    /**
     * Special listener for sending messages
     */
    class CoxeterListener implements ActionListener {
        Operation op;
        String[] args;

        public CoxeterListener(Operation op, String[] args) {
            this.op = op;
            this.args = args;
        }

        public void actionPerformed(ActionEvent e) {
            send(op, args);
        }
    }
    
    /**
     * Special listener for setting the tool
     */
    class ToolListener implements ActionListener {
        Tool t;
        
        public ToolListener(Tool t) {
            this.t = t;
        }
        
        public void actionPerformed(ActionEvent e) {
            tool = t;
        }
    }
    
    /**
     * Where you sketch your diagram
     */
    class SketchPanel extends JPanel {
        SketchPanel() {
            setFocusable(true);
            setDoubleBuffered(true);
            requestFocusInWindow(true);
            setBackground(Color.WHITE);
            
            addMouseListener(new Clicker());
            addMouseListener(new Presser());
            addMouseMotionListener(new Motion());
        }
        
        /**
         * For checking clicks
         */
        class Clicker extends MouseAdapter {
            /**
             * If the user clicks, send a message to the server.
             */
            public void mouseClicked(MouseEvent e) {
                String[] args = new String[2];
                args[0] = "" + e.getX();
                args[1] = "" + e.getY();
                send(Operation.CLICKED, args);
            }
        }

        /**
         * For checking presses
         */
        class Presser extends MouseAdapter {
            /**
             * If the user presses, send a message to the server.
             */
            public void mousePressed(MouseEvent e) {
                String[] args = new String[2];
                args[0] = "" + e.getX();
                args[1] = "" + e.getY();
                send(Operation.PRESSED, args);
            }
            
            /**
             * If the user releases, send a message to the server.
             */
            public void mouseReleased(MouseEvent e) {
                String[] args = new String[2];
                args[0] = "" + e.getX();
                args[1] = "" + e.getY();
                send(Operation.RELEASED, args);
            }
        }

        /**
         * For checking motion
         */
        class Motion extends MouseAdapter {
            /**
             * If the user drag, send a message to the server.
             */
            public void mouseDragged(MouseEvent e) {
                String[] args = new String[2];
                args[0] = "" + e.getX();
                args[1] = "" + e.getY();
                send(Operation.DRAGGED, args);
            }
            
            /**
             * If the user moves, send a message to the server.
             */
            public void mouseMoved(MouseEvent e) {
                String[] args = new String[2];
                args[0] = "" + e.getX();
                args[1] = "" + e.getY();
                send(Operation.MOVED, args);
            }
        }
        
        /**
         * Dequeue a message from the queue
         * and execute the instructions therein.
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (messages.size() == 0) return;
            ServerMessage msg = new ServerMessage(messages.remove());
            for (int i = 0; i < msg.size(); i++) {
                msg.get(i).compile().draw(g);
            }
        }
    }
    
    /**
     * Panel with a bunch of buttons for constraints.
     */
    class ConstraintsPanel extends CoxeterPanel {
        ConstraintsPanel() {
            addButton(new CoxeterButton("parallel.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    send(Operation.ADD, new String[] { "parallel" });
                }
            });
            
            addButton(new CoxeterButton("perp.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    send(Operation.ADD, new String[] { "perpendicular" });
                }
            });
            
            addButton(new CoxeterButton("length.png"), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    send(Operation.ADD, new String[] { "length" });
                }
            });
        }
    }
}
