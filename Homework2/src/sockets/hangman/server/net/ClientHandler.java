package sockets.hangman.server.net;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import sockets.hangman.common.Constants;
import sockets.hangman.common.HangmanException;
import sockets.hangman.common.MsgSeperator;
import sockets.hangman.common.MsgType;
import sockets.hangman.server.controller.Controller;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.ArrayDeque;
import java.util.Queue;


class ClientHandler{
	private final Controller contr = new Controller();
	private final SocketChannel clientChannel;
	private final ByteBuffer fromClient = ByteBuffer.allocateDirect(8192);
	private final Queue<ByteBuffer> toClient = new ArrayDeque<>();
	private String username = "root";
	private String password = "123";
	private String JWS;
	private Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private MsgSeperator msgSeperator = new MsgSeperator();
	private HangmanServer hangManServer;

	/**
	 * Creates a new instance, which will handle communication with one specific client connected to
	 * the specified socket.
	 * @param selector 
	 *
	 * @param clientSocket The socket to which this handler's client is connected.
	 */
	ClientHandler(SocketChannel clientChannel, HangmanServer hangmanServer) {
		this.clientChannel = clientChannel;
		this.hangManServer = hangmanServer; 
	}

	/**
	 * The run function, that handles the communication from the client.
	 */
	public void run(){
		try {

			while(msgSeperator.hasNext()) {
				Input ipt = new Input(msgSeperator.getNext());
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
					disconnect();
					break;
				default: 
					msgClient(ipt.input, MsgType.INVALIDINPUT);
				}
			} 
		}catch(IOException e) {
			System.out.println(e);
			System.out.println("Failed!");
		}
	}
	/**
	 * Disconnect function, closes clientChannel.
	 * @throws IOException
	 */
	public void doDisconnect() throws IOException{
		disconnect();
	}
	/**
	 * Recives the message from the client, and puts it into msgSeparator to create a finished message.
	 * 
	 */
	public void recvMsg() throws IOException{
		fromClient.clear();

		int numBytes =clientChannel.read(fromClient);
		if(numBytes == -1) {
			throw new IOException("client has closed connection");
		}
		String recvdString = extractMsg();
		msgSeperator.appendS(recvdString);
		run();
	}
	/**
	 *  Extracts message from the ByteBuffer from the client.
	 * @return a String with the message from the client.
	 */
	private String extractMsg() {
		fromClient.flip();
		byte[] bytes = new byte[fromClient.remaining()];
		fromClient.get(bytes);
		return new String(bytes);
	}
	/**
	 * Function to check if the client is the hard coded user.
	 * @param ipt the input from the client
	 * @return true if user is correct, false if incorrect.
	 */
	private boolean checkUser(Input ipt) {
		String[] body = splitBody(ipt);
		if(body[0].contentEquals(username)&& body[1].contentEquals(password)) {
			setJWS(body[0]);
			return true;
		}
		return false;
	}
	/**
	 * Generates a JWS string that is subject of a specific user. 
	 * And signed with a key.
	 * @param username the username that the jwsString should be subject of.
	 */
	private void setJWS(String username) {
		JWS = Jwts.builder().setSubject(username).signWith(signingKey).compact();
	}
	/**
	 * Check the client JWS so that we can be sure that the client is the correct user.
	 * @param username The username the JWS is subject to.
	 * @param JWS jws String that the client sent in.
	 * @return true if the jws is connected to the specific user, otherwise false.
	 */
	private boolean checkJWS(String username, String JWS) {
		try {
			assert Jwts.parser().setSigningKey(signingKey).parseClaimsJws(JWS).getBody().getSubject().equals(username);
			return true;
		}
		catch(JwtException e){
			return false;
		}
	}
	/**
	 *  Creates a game for the user, and sends it to the cleint, adds in MSG_Delimiter at right place..
	 * @param msg The msg to send ( the game board)
	 */
	private void sendStart(String msg) throws IOException {
		String gameStatus = getWord();
		String startMsg= gameStatus+Constants.MSG_DELIMETER+JWS;
		msgClient(startMsg, MsgType.GAMERESPONSE);

	}
	/**
	 * Adds structure to a message and adds message to the toClient ByteBuffer queue. 
	 * @param msg The data to send to add to the queue.
	 * @param msgT The type of message to add to the message.
	 * @throws IOException
	 */
	private void msgClient(String msg, MsgType msgT) throws IOException {
		StringBuilder str = new StringBuilder();

		str.append(msgT.toString());
		str.append(Constants.MSG_DELIMETER.toString());
		if(msg!=null) {
			str.append(msg);
		}
		int length = str.toString().length();
		StringBuilder complete = new StringBuilder();
		complete.append(length);
		complete.append(Constants.LENGHT_DELIMITER);
		complete.append(str.toString());
		toClient.add(ByteBuffer.wrap(complete.toString().getBytes()));
		hangManServer.Send(clientChannel);
	}
	/**
	 * Sends the head of the Bytebuffer Queue to the client.
	 * @throws IOException
	 */
	public void sendMsg() throws IOException {
		ByteBuffer msg;
		synchronized (toClient) {
			while((msg = toClient.peek()) != null) {
				clientChannel.write(msg);
				if(msg.hasRemaining()) {
					return;
				}
				toClient.remove();
			}
		}
		hangManServer.recive(clientChannel);
	}
	/**
	 * gets creates a game for the hangman game.
	 * @return status of the game.
	 */
	private String getWord(){
		return contr.getWord();

	}
	/**
	 * Plays a user guess of the hangman game.
	 * @param guess The client char or string to play with.
	 * @return returns uppdated status of the game.
	 */
	private String hang(String guess){
		return contr.hang(guess.toUpperCase());

	}
	/**
	 * Handles client disconnect request.
	 * @throws IOException if we could not close clientChannel.
	 */
	private void disconnect() throws IOException{
		msgClient("disconncet", MsgType.DISCONNECT);
		try{
			clientChannel.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	/**
	 * Splits the body of a input using the body_Delimiter.
	 * @param ipt An instance of a Input class.
	 * @return the body in two parts.
	 */
	private String[] splitBody(Input ipt){
		return ipt.body.split(Constants.BODY_DELIMETER);
	}
	/**
	 * Class thats handles the user input.  
	 * Splits the input into body, iptType, key, and rawInput(input).
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
				//FIXA DENNA FUNKAR EJ.
			} catch (Throwable throwable){
				throw new HangmanException(null, throwable);
			}
		}


	}

}


