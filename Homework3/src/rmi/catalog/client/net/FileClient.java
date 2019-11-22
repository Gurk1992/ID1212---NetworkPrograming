package rmi.catalog.client.net;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import rmi.catalog.common.ByteReader;




public class FileClient{
	private Socket clientSocket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;
    private String host = "127.0.0.1";
	private FileOutputStream outFile;
	private ByteReader byteReader= new ByteReader();
    
    
    public FileClient(ResponseHandler outputer) throws IOException {
    	clientSocket = new Socket(host, 4444);
    	clientSocket.setSoTimeout(1800000);
    	toServer = new DataOutputStream(clientSocket.getOutputStream());
        fromServer = new DataInputStream(clientSocket.getInputStream());
        new Thread(new ServerListner(outputer)).start();
        
    }
    public void download(String name,  long userId) {
    	StringBuilder msg = new StringBuilder();
    	msg.append("DOWNLOAD");
    	msg.append("#");
    	msg.append(userId);
    	msg.append("#");
    	msg.append(name);
    	byte[] msgByte = msg.toString().getBytes(StandardCharsets.UTF_8);
    	try {
			toServer.writeLong(msgByte.length);
			toServer.write(msgByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
  		
  	}
    
    public void sendFile(String data, String name,  long userId) {
    	StringBuilder msg = new StringBuilder();
    	msg.append("UPLOAD");
    	msg.append("#");
    	msg.append(userId);
    	msg.append("#");
    	msg.append(name);
    	msg.append("#");
    	msg.append(data);
    
    	byte[] msgByte = msg.toString().getBytes(StandardCharsets.UTF_8);
    	try {
			toServer.writeLong(msgByte.length);
			toServer.write(msgByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
 
    public boolean saveFile(String fileName, String data) throws IOException {
    	try {
    	
    		FileOutputStream fileStream = new FileOutputStream(System.getProperty("user.dir")+"\\Cfiles\\"+fileName);
    		fileStream.write(data.getBytes());
    		fileStream.close();
    	return true;
    	}catch(Exception e) {
    		return false;
    	}
    	
    }
    /**
     * Simple disconnection request from the user.
     * @throws IOException
     */
    public void sendDisconnect() throws IOException{
    	String data = "DISCONNECT#";
    	byte[] dataInBytes = data.getBytes(StandardCharsets.UTF_8);
    	toServer.writeInt(dataInBytes.length);
    	toServer.write(dataInBytes);
    	clientSocket.close();
    }
    
    private class ServerListner implements Runnable {
    	private final ResponseHandler outputer;
    	private ServerListner(ResponseHandler outputer) {
    		this.outputer = outputer;
    	}
		@Override
		public void run() {
			try {
				for(int i=1; i>=1; i++){
					outputer.serverResponse(MsgFromServer(byteReader.readData(fromServer)));
				}
			}catch(Throwable connectionFailure) {
				outputer.serverResponse("Connection lost!");
			}
		
		}
		private String MsgFromServer(String msg) {
			String[] msgs=msg.split("#");
			if(msgs[0].contentEquals("DOWNLOAD")) {
				
				try {
					if(saveFile(msgs[1],msgs[2])) {
						return "File has been downloaded";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return "File could not be downloaded";
		}
    }

 
   
}

