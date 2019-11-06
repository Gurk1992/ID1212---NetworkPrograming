package sockets.hangman.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

import sockets.hangman.common.Constants;
import sockets.hangman.common.HangmanException;
import sockets.hangman.common.MsgSeperator;
import sockets.hangman.common.MsgType;
import java.util.concurrent.Executor;


public class Client implements Runnable {

	private final ByteBuffer fromServer = ByteBuffer.allocateDirect(8192);
	private final Queue<ByteBuffer> sendServer = new ArrayDeque<>();
	private volatile boolean connected;
	private String key;
	private SocketChannel socketChannel;
	private Selector selector;
	private InetSocketAddress server;
	private MsgSeperator msgSeperator = new MsgSeperator();
	private boolean timeToSend = false;
	private ResponseHandler listener;


	/**
	 * Trys to connect the client socket to the server socket.
	 * @param hostname ip address of server
	 * @param port the port of the server
	 * @param outputer the outputter to help with server responses.
	 * @throws IOException Exception from trying to connect to the server
	 */

	public void connect(String hostname, int port, ResponseHandler outputer) throws IOException{
		server = new InetSocketAddress(hostname, port);
		new Thread(this).start();
		listener=outputer;
	}

	/**
	 * Run function of the Client runnable, uses non blocking sockets with three selector options.
	 *  isConnectable, is writeable, is readable.
	 */
	@Override
	public void run() {
		try {
			initConnection();
			initSelector();
			while(connected || !sendServer.isEmpty()) {
				if(timeToSend) {
					socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
					timeToSend=false;
				}
				selector.select();
				for (SelectionKey key : selector.selectedKeys()) {
					selector.selectedKeys().remove(key);
					if(!key.isValid()) {
						continue;
					}
					if(key.isConnectable()) {

						compConnection(key);
					}
					if(key.isWritable()) {

						sendToServer(key);
					}
					if(key.isReadable()) {

						recv(key);
					}

				}

			}
		}
		catch (Exception e) {
			System.out.println(e);
			System.err.println("failed to connect");
		}
		try {
			sendDisconnect();
		} catch (IOException ex) {
			System.err.println("failed to connect");
		}
	}    




	/**
	 * Sets up and sends a StartGame request to server.
	 * @param username the username to login with
	 * @param pass password to login with.
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
	/**
	 * Function to setup and send a Play request to server.
	 * @throws IOException
	 */
	public void sendPlay() throws IOException{
		toServer(MsgType.PLAY.toString(),"play");
	}
	/**
	 * Simple disconnection request from the user.
	 * @throws IOException
	 */
	public void sendDisconnect() throws IOException{
		toServer(MsgType.DISCONNECT.toString(),"Disconnect");
		socketChannel.close();
		socketChannel.keyFor(selector).cancel();
		connected=false;
	}
	/**
	 * Initiates selectors, registers selector to socketChannel and puts selectionKey to OP_CONNECT.
	 */
	private void initSelector() throws IOException {
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_CONNECT);

	}
	/**
	 * Initiates Connection to the server.
	 * Opens socketChannel, sets to NON-Blocking, and connects to server.
	 */
	private void initConnection() throws IOException {
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(server);
		connected = true;
	}
	/**
	 * Completes connection, if it was not immediately completed with initConnection.
	 * In a new thread returns a welcome message to the client.
	 * @param key
	 * @throws IOException
	 */
	private void compConnection(SelectionKey key) throws IOException {
		if(socketChannel.finishConnect()) {
			Executor pool = ForkJoinPool.commonPool();
			pool.execute(new Runnable(){
				@Override
				public void run() {
					listener.serverResponse("Connected to 127.0.0.1 \nTo start a game use command - Start username password"
							+ "\nOnce started use command - Guess char to make a guess"
							+ "\nFor help use command - Help"
							+ "\nTo quit use command - disconnect");
				}
			});	
		}
	}

	/**
	 * Revives input from ByteBuffer fromServer, extracts bytes in Bytebuffer to string.
	 * Then sends the string into the msgSeparator that stores the strings and completes whole messages
	 * from the server.
	 * creates a Input class and whenever there is a completed message in the msgSeparator the Input class will 
	 * handle the message.
	 * @param key the selection key attached to the clientHandler
	 * @throws IOException
	 */
	private void recv(SelectionKey key) throws IOException {
		fromServer.clear();
		int numBytes =socketChannel.read(fromServer);
		if(numBytes == -1) {
			throw new  IOException("faield to read bytes");
		}
		String recvdString = extractMsg();

		msgSeperator.appendS(recvdString);
		if(msgSeperator.hasNext()) {
			Input ipt = new Input(listener); 
			Executor pool = ForkJoinPool.commonPool();
			pool.execute(new Runnable(){
				@Override
				public void run() {
					ipt.getMsg(msgSeperator.getNext());
				}
			});	
		}
	}
	/**
	 * Extracts String from Bytebuffer fromServer into a String.
	 * @return String from the array of bytes.
	 */
	private String extractMsg() {
		fromServer.flip();
		byte[] bytes = new byte[fromServer.remaining()];
		fromServer.get(bytes);
		return new String(bytes);
	}

	/**
	 * Fixes the structure of the message and queues the message up in the ByteBuffer sendServer.
	 * sets timetosend and wakes up the selector.
	 * @param msgType The type of message to send.
	 * @param msg The actual message to send to the server.
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
		int length = str.toString().length();
		StringBuilder complete = new StringBuilder();
		complete.append(length);
		complete.append(Constants.LENGHT_DELIMITER);
		complete.append(str.toString());
		synchronized(sendServer) {
			sendServer.add(ByteBuffer.wrap(complete.toString().getBytes()));
		}
		timeToSend=true;
		selector.wakeup();

	}
	/**
	 * Function to acctuly send the head of the ByteBUffer sendServer to the server.
	 * @param key
	 * @throws IOException
	 */
	private void sendToServer(SelectionKey key) throws IOException {
		ByteBuffer msg;
		synchronized (sendServer) {
			while ((msg=sendServer.peek()) !=null) {
				socketChannel.write(msg);

				if(msg.hasRemaining()) {
					return;
				}
				sendServer.remove();
			}
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	/**
	 * Class to handle finished messages from msgSeparator.
	 */
	private class Input {
		private final ResponseHandler outputer;
		private Input(ResponseHandler outputer) {
			this.outputer = outputer;
		}
		/**
		 * Prints the message from the server to the view.
		 * @param msg
		 */
		public void getMsg(String msg) {
			outputer.serverResponse(MsgFromServer(msg));
		}
		/**
		 * Prepares an output from the serverResponse. Different responses depending on the type of message.
		 * @param msg The message from the server.
		 * @return The output that the responseHandler should be handled on the view.
		 */
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
			if(typeBody[0].equals(MsgType.DISCONNECT.toString())) {
				return "Please come back and play again soon!"; 
			}
			else throw new HangmanException("error :" +msg, null);
		}
	}
}
