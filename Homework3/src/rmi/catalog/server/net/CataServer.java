package rmi.catalog.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import rmi.catalog.server.controller.Controller;
import rmi.catalog.server.model.UserHandler;


public class CataServer {
	private Controller contr;
	 public CataServer(Controller contr) {
		this.contr = contr;
	}
	public void serve() {
	        try {
	            ServerSocket listeningSocket = new ServerSocket(4444);
	            while (true) {
	            	
	                Socket clientSocket = listeningSocket.accept();
	                startHandler(clientSocket);
	                
	            }
	        } catch (IOException e) {
	            System.err.println("Server failure.");
	        }
	    }
	 private void startHandler(Socket clientSocket) throws SocketException {
	    	
	        clientSocket.setSoLinger(true, 5000);
	        clientSocket.setSoTimeout(1800000);
	        ClientHandler handler = new ClientHandler(clientSocket, contr);
	        Thread handlerThread = new Thread(handler);
	        handlerThread.setPriority(Thread.MAX_PRIORITY);
	        handlerThread.start();
	    }
}
