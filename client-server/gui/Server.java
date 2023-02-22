package gui;

import csp.*;
import euclid.*;
import neo.*;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * The server
 */
public class Server implements Runnable {
    final int PORT = 5000;
    
    private ArrayList<ConnectionHandler> c;
    private LinkedList<String> messages;
    private HashSet<GeometricObject> selections;

    private Space context;
    private Solver solver;
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private long time;

    public Server() {
        c = new ArrayList<ConnectionHandler>();
        messages = new LinkedList<String>();

        context = new Space();
        solver = new Solver(context);
        time = 0;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();
    }
    
    /**
     * Run the server
     */
    public void run() { 
        System.out.println("Waiting for a connection request from a client ...");
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            System.out.println("oh no fuck me");
        }

        // open connections with clients
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        clientSocket = serverSocket.accept();
                        System.out.println("Connection accepted");
                        ConnectionHandler connection = new ConnectionHandler(clientSocket);
                        c.add(connection);
                        connection.start();
                    } catch (Exception e) {
                        System.out.println("Oopsie woopsie");
                    }
                } 
            }
        }).start();

        // execute messages and solve constraints
        while (true) {
            if (messages.size() > 1) {
                ClientMessage msg = new ClientMessage(messages.remove());
                msg.execute(context, solver);
            }

            if (context.constraints.size() > 0) {
                solver.solve();
            }

            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * handle connections
     */
    class ConnectionHandler extends Thread { 
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        
        public ConnectionHandler(Socket socket) { 
            this.socket = socket;
        }
        
        /**
         * run the connection
         */
        public void run() {
            // make a connection
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
            } catch (Exception e) {
                System.out.println("Error in Server.run method. Getting readers and writers");
            }

            // send the current context to the client
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            output.println(context.message());
                            output.flush();
                            Thread.sleep(5);
                        } catch (Exception e) {
                        }
                    }
                }
            }).start();
            
            // recieve messages from the client and add
            // them to the event queue.
            while (true) {
                try {
                    long x = System.nanoTime();
                    if (x - time > 5000000) {
                        messages.add(input.readLine());
                    }
                    time = x;
                    Thread.sleep(5);
                } catch (Exception e) {
                    System.out.println("Error in Server.run method. Getting the queue");
                }
            }
        }
    }    
}
