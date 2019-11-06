package sockets.hangman.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;



public class HangmanServer{

	private int port = 29019;
	private Selector selector;
	private ServerSocketChannel listenSocketChannel;

	/**
	 * Main function starts a new hangmanserver
	 */
	public static void main(String[] args){
		HangmanServer server = new HangmanServer();
		System.out.println("Server");
		server.serve();
	}
	/**
	 * A serve function that is ment to server incomming connections and requests to the server.
	 * Non-blocking structure, with three selectors.
	 */
	private void serve() {
		try {
			selector = Selector.open();
			initListeningChannel();

			while (true) {
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if(!key.isValid()) {
						continue;
					}
					if(key.isAcceptable()) {
						startHandler(key);
					}else if(key.isReadable()){
						fromClient(key);
					}else if(key.isWritable()) {
						toClient(key);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Server failure.");
		}
	}
	/**
	 * Starts a new client handler, and binds the key to it and changes the key to OP_READ.
	 * @param clientSocket Takes the incomming client socket for which a specific connection to the server is handled on.
	 * @throws IOException 
	 */
	private void startHandler(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel clientChannel = serverSocketChannel.accept();
		clientChannel.configureBlocking(false);
		ClientHandler client = new ClientHandler(clientChannel, this);
		clientChannel.register(selector, SelectionKey.OP_READ, client);
		clientChannel.setOption(StandardSocketOptions.SO_LINGER,5000);
	}

	/**
	 *  Sends a message from the keys bound clientHandlers ByteBuffer Queue.
	 * @param key The SelectionKey that is ready for a write operation.
	 * @throws IOException
	 */
	private void toClient(SelectionKey key) throws IOException {
		ClientHandler client = (ClientHandler) key.attachment();
		try {
			client.sendMsg();
		}
		catch(IOException e){
			client.doDisconnect();
			key.cancel();
			System.out.println(e);
		}
	}
	/**
	 * Reads a message from the client.
	 * @param key The SelectionKey that is ready for a read operation.
	 * @throws IOException
	 */
	private void fromClient(SelectionKey key) throws IOException {
		ClientHandler client = (ClientHandler) key.attachment();
		try {
			client.recvMsg();
		}catch(IOException e) {
			client.doDisconnect();
			key.cancel();
		}

	}
	/**
	 * Initiates a new Listening channel, Non blocking, and bound to port 29019.
	 * Registers OP_ACCEPT with the channel.
	 * @throws IOException
	 */
	private void initListeningChannel() throws IOException {
		listenSocketChannel = ServerSocketChannel.open();
		listenSocketChannel.configureBlocking(false);
		listenSocketChannel.bind(new InetSocketAddress(port));
		listenSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

	}
	/**
	 * Function to alter a clientChannels SelectionKey to OP_WRITE.
	 * @param clientChannel The clientChannel that is ready for write.
	 * @throws ClosedChannelException 
	 */
	public void Send(SocketChannel clientChannel) throws ClosedChannelException {
		clientChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
		selector.wakeup();
	}
	/**
	 * Function to alter a ClientChannels SelectionKey to OP_READ.
	 * @param clientChannel The Channel that is ready to read a message.
	 * @throws ClosedChannelException
	 */
	public void recive(SocketChannel clientChannel) throws ClosedChannelException {
		clientChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
		selector.wakeup();
	}


}