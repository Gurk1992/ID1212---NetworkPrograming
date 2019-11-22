package rmi.catalog.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import rmi.catalog.common.ByteReader;
import rmi.catalog.server.controller.Controller;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private boolean connected;
    private DataInputStream fromClient;
    private DataOutputStream toClient;
    private ByteReader byteReader = new ByteReader();
    private Controller contr;
   
    


     /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientSocket The socket to which this handler's client is connected.
     * @param contr 
     */
    ClientHandler(Socket clientSocket, Controller contr) {
    	this.contr = contr;
        this.clientSocket = clientSocket;
        connected = true;
    }
    public void run(){
       	try {
       		fromClient = new DataInputStream(clientSocket.getInputStream());
           	toClient = new DataOutputStream(clientSocket.getOutputStream());
       	}
       	catch(IOException ioe){
           	throw new UncheckedIOException(ioe);
       	}
       	while (connected){
       		try {
       			Input ipt= new Input(byteReader.readData(fromClient));
       		switch(ipt.msgType) {
       		case "UPLOAD":
       			contr.addclientHandler(ipt.userId, this);
       			saveFile(ipt);
       			break;
       		case"DOWNLOAD":
       				File file = new File(System.getProperty("user.dir")+"\\Files\\"+ipt.name);
       				sendFile(new String(Files.readAllBytes(file.toPath())),ipt.name);
       			break;
       		case"DISCONNECT":
       				disconnect();
       			break;
       			}
       		}catch(IOException ioe){
       				disconnect();
 					ioe.printStackTrace();
             }
       		         
        }
    }
    private void sendFile(String data, String name) throws IOException {
        	StringBuilder msg = new StringBuilder();
        	msg.append("DOWNLOAD");
        	msg.append("#");
        	msg.append(name);
        	msg.append("#");
        	msg.append(data);
        	
        	byte[] msgByte = msg.toString().getBytes(StandardCharsets.UTF_8);
        	toClient.writeLong(msgByte.length);
        	toClient.write(msgByte);
		
	}
	private void saveFile(Input ipt) throws IOException {
    	FileOutputStream fileStream = new FileOutputStream(System.getProperty("user.dir")+"\\Files\\"+ipt.name);
    	fileStream.write(ipt.body.getBytes());
    	fileStream.close();
    	contr.findUser(ipt.userId).sendMsg("fileStatus#true");
    }
    
    private void disconnect(){
        try{
            clientSocket.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        connected = false;
    }
    
    
    private static class Input {
    	private String msgType;
        private long userId;
        private String name;
        private String body;
        private String input;
        private Input(String ipt){
            input = ipt;
          
            try{
                String[] msg = ipt.split("#");
                msgType = msg[0];
                if(msg.length>1) {
                	userId = Long.parseLong(msg[1]);
                	name = msg[2];
                	if(msg.length>3) {
                		body= msg[3];
                	}
                }
        
            } catch (Throwable e){
               e.printStackTrace();
            }
        }
    }
    
}
