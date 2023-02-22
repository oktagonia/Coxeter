package gui;

import java.io.*;
import java.net.*;

import csp.*;
import euclid.*;
import neo.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Server {
    final int PORT = 5000;
    
    ArrayList<ConnectionHandler> c;
    LinkedList<String> queue = new LinkedList<String>();
    
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter = 0;
    
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.go();
    }
    
    public void go() throws Exception { 
        c = new ArrayList<ConnectionHandler>();
        System.out.println("Waiting for a connection request from a client ...");
        serverSocket = new ServerSocket(PORT);
        
        while(true) {
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted");
            ConnectionHandler connection = new ConnectionHandler(clientSocket);
            c.add(connection);
            connection.start();
        }
    }
    
    class ConnectionHandler extends Thread { 
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        
        public ConnectionHandler(Socket socket) { 
            this.socket = socket;
        }
        
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
            } catch (Exception e) {
                System.out.println("Error in Server.run method. Getting readers and writers");
            }
            
            while (true) {
                try {
                    queue.add(input.readLine());
                    output.flush();
                    Thread.sleep(5);
                } catch (Exception e) {
                    System.out.println("Error in Server.run method. Getting the queue");
                }
            }
        }
    }    
}
