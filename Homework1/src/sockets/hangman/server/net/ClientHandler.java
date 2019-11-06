package sockets.hangman.server.net;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import sockets.hangman.common.ByteReader;
import sockets.hangman.common.Constants;
import sockets.hangman.common.HangmanException;
import sockets.hangman.common.MsgType;
import sockets.hangman.server.controller.Controller;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.io.BufferedReader;

class ClientHandler implements Runnable {
    private final Controller contr = new Controller();
    private final Socket clientSocket;
    private boolean connected;
    private DataInputStream fromClient;
    private DataOutputStream toClient;
    private ByteReader byteReader = new ByteReader();
    private String username = "root";
    private String password = "123";
    private String JWS;
    private Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
     /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientSocket The socket to which this handler's client is connected.
     */
    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        connected = true;
    }
    
    /**
     * The loop that handel the communication to and from the client.
     */

    @Override
    public void run(){
        try {
        	fromClient = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            toClient = new DataOutputStream(clientSocket.getOutputStream());
        }
        catch(IOException ioe){
            throw new UncheckedIOException(ioe);
        }
        while (connected){
            try {
   
                Input ipt = new Input(byteReader.readData(fromClient));
           
                switch(ipt.iptType){
                	case START:
                		if(checkUser(ipt)) {
                		String gameStatus = getWord();
                		sendStart(gameStatus);
                		}
                		else {
                			msgClient(null, MsgType.INVALIDLOGIN);
                		}
                	break;
                	
                    case GUESS:
                    
                    	if(checkJWS(username, ipt.key)) {
                    	msgClient(hang(ipt.body), MsgType.GAMERESPONSE);
                    	}
                    	else {
                    		msgClient(null, MsgType.INVALIDACCESSTOKEN);
                    	}
                    break;
                    case PLAY:
                    	if(checkJWS(username, ipt.key)) {
                    		msgClient(getWord(),MsgType.GAMERESPONSE);
                    	}
                    	else {
                    		msgClient(null, MsgType.INVALIDACCESSTOKEN);
                    	}
                    break;
                    case DISCONNECT: 
                    	System.out.println("user "+ username +" disconnected!");
                    break;
                    default: 
                    	msgClient(ipt.input, MsgType.INVALIDINPUT);
                }
            } catch(IOException ioe){
               
					disconnect();
					throw new HangmanException(ioe);
            }
        }
    }

    private boolean checkUser(Input ipt) {
    	String[] body = splitBody(ipt);
    	if(body[0].contentEquals(username)&& body[1].contentEquals(password)) {
    		setJWS(body[0]);
    		return true;
    	}
    	return false;
    }
    private void setJWS(String username) {
    	
    	JWS = Jwts.builder().setSubject(username).signWith(signingKey).compact();
    }
    private boolean checkJWS(String username, String JWS) {
    	try {
    	assert Jwts.parser().setSigningKey(signingKey).parseClaimsJws(JWS).getBody().getSubject().equals(username);
    	return true;
    	}
    	catch(JwtException e){
    		return false;
    	}
    }
    private void sendStart(String msg) throws IOException {
    	String gameStatus = getWord();
		String startMsg= gameStatus+Constants.MSG_DELIMETER+JWS;
		msgClient(startMsg, MsgType.GAMERESPONSE);

    }
    private void msgClient(String msg, MsgType msgT) throws IOException {
    	StringBuilder str = new StringBuilder();
    	str.append(msgT.toString());
		str.append(Constants.MSG_DELIMETER.toString());
    	if(msg!=null) {
    		str.append(msg);
    	}
    	byte[] dataInBytes = str.toString().getBytes(StandardCharsets.UTF_8);
    	toClient.writeInt(dataInBytes.length);
    	toClient.write(dataInBytes);    	
    }
    
    private String getWord(){
        return contr.getWord();
       
    }
    private String hang(String guess){
         return contr.hang(guess.toUpperCase());
        
    }
    private void disconnect(){
        try{
            clientSocket.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        connected = false;
    }
    private String[] splitBody(Input ipt){
    	return ipt.body.split(Constants.BODY_DELIMETER);
    }
    /**
     * Class that handles the input from the user.
     */
    private static class Input {
        private MsgType iptType;
        private String body;
        private String input;
        private String key;

        private Input(String ipt){
            input = ipt;
            try{
                String[] msgTokens = ipt.split(Constants.MSG_DELIMETER);
                iptType = MsgType.valueOf(msgTokens[Constants.MSG_TYPE].toUpperCase());
                if(msgTokens.length>1 ){
                    body = msgTokens[Constants.MSG_BODY];
              
                    if(msgTokens.length>2) {
                    key = msgTokens[Constants.MSG_KEY];
                    }
                }
            } catch (Throwable throwable){
                throw new HangmanException(throwable);
            }
        }


    }
    
}


