package sockets.hangman.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class HangmanServer{

    private int port = 29019;

/**
 * Main function starts a new hangmanserver
 */
public static void main(String[] args){
    HangmanServer server = new HangmanServer();
    System.out.println("Server");
    server.serve();
}
    /**
     * Function that serves incoming connections to the server when a incoming connection is made it creates a 
     * handler for that connection.
     */
  private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(port);
            while (true) {
            	
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
                
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
    /**
     * @param clientSocket Takes the incomming client socket for which a specific connection to the server is handled on.
     */
    private void startHandler(Socket clientSocket) throws SocketException {
    	
        clientSocket.setSoLinger(true, 5000);
        clientSocket.setSoTimeout(1800000);
        ClientHandler handler = new ClientHandler(clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
}