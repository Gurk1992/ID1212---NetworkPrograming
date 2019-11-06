package sockets.hangman.client.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.io.BufferedReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import sockets.hangman.common.ByteReader;
import sockets.hangman.common.Constants;
import sockets.hangman.common.HangmanException;
import sockets.hangman.common.MsgType;


public class Client {
	private Socket clientSocket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;
    private ByteReader byteReader = new ByteReader();
    private volatile boolean connected;
    private String key;
    /**
     * Trys to connect the client socket to the server socket.
     * @param hostname ip address of server
     * @param port the port of the server
     * @param outputer the outputter to help with server responses.
     * @throws IOException Exception from trying to connect to the server
     */
    public void connect(String hostname, int port, ResponseHandler outputer) throws IOException{
    	clientSocket = new Socket(hostname, port);
    	clientSocket.setSoTimeout(1800000);
    	connected= true;
    	toServer = new DataOutputStream(clientSocket.getOutputStream());
        fromServer = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        new Thread(new ServerListner(outputer)).start();
        outputer.serverResponse("Connected to 127.0.0.1 \nTo start a game use command - Start username password"
        		+ "\nOnce started use command - Guess char to make a guess"
        		+ "\nFor help use command - Help"
        		+ "\nTo quit use command - disconnect");
    }
    
    /**
     * Function to start a game of hangman
     */
    public void startGame(String username, String pass) throws IOException{
    	StringBuilder str = new StringBuilder();
    	str.append(username);
    	str.append(Constants.BODY_DELIMETER);
    	str.append(pass);
    	toServer(MsgType.START.toString(), str.toString());
    }
    /**
     * Simple function to send a hangman guess to the server.
     * @param guess User guess for the hangman guess.
     * @throws IOException 
     */
    public void sendGuess(String guess) throws IOException {
    	toServer(MsgType.GUESS.toString(), guess);
    	
    }
    public void sendPlay() throws IOException{
    	toServer(MsgType.PLAY.toString(),"play");
    }
    /**
     * Simple disconnection request from the user.
     * @throws IOException
     */
    public void sendDisconnect() throws IOException{
    	String data = MsgType.DISCONNECT.toString();
    	byte[] dataInBytes = data.getBytes(StandardCharsets.UTF_8);
    	toServer.writeInt(dataInBytes.length);
    	toServer.write(dataInBytes);
    	disconnect();
    }
    public void disconnect() throws IOException {
    	clientSocket.close();
		connected=false;
    }
    /**
     * private helper function to send a guess or a message to the server.
     * @param msgType The type of message to send.
     * @param msg The acctual message to send to the server.
     * @throws IOException 
     */
    private void toServer(String msgType, String data) throws IOException {
    	 StringBuilder str = new StringBuilder();
    	 str.append(msgType);
    	 str.append(Constants.MSG_DELIMETER);
    	 str.append(data); 
    	 if(!msgType.contentEquals(MsgType.START.toString())) {
    	 str.append(Constants.MSG_DELIMETER);
    	 str.append(key);
    	 }
    	 byte[] dataInBytes =str.toString().getBytes(StandardCharsets.UTF_8);
     	 toServer.writeInt(dataInBytes.length);
     	 toServer.write(dataInBytes);
    	    
    }
    
    /**
     * ServerListener, run in a seperate thread to not block the view.
     * Listens for respones from the server and uses the responsehandler to output them to the user.
     */
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
				outputer.serverResponse("Connection lost Please comeback and play another time!");
			}
		
		}
		private String MsgFromServer(String msg) {
			String[] typeBody=msg.split(Constants.MSG_DELIMETER);
			if(typeBody[0].equals(MsgType.GAMERESPONSE.toString())) {
				String[] hangManData=typeBody[1].split(Constants.BODY_DELIMETER);
				
				if(typeBody.length >2) {
					key= typeBody[Constants.MSG_KEY];
					
				}
				if(hangManData[Constants.ATTEMPTS_BODY].equals("0")) {
					return "Word: "+hangManData[Constants.WORD_BODY]+" Attempts left: "+hangManData[Constants.ATTEMPTS_BODY]+ " Score "+ hangManData[Constants.SCORE_BODY]+" \nYou lost, please enter PLAY to play again!";
				}
				
				return "Word: "+hangManData[Constants.WORD_BODY]+" Attempts left: "+hangManData[Constants.ATTEMPTS_BODY]+ " Score "+ hangManData[Constants.SCORE_BODY];
				
			}
			if(typeBody[0].equals(MsgType.INVALIDINPUT.toString())) {
				return "Invalid input to server, please try again";
			}
			if(typeBody[0].equals(MsgType.INVALIDLOGIN.toString())) {
				return "Invalid username or password, please try again";
			}
			if(typeBody[0].equals(MsgType.INVALIDACCESSTOKEN.toString())) {
				return "Invalid accessToken, please try again";
			}
			else throw new HangmanException(msg);
		}
    }
}
