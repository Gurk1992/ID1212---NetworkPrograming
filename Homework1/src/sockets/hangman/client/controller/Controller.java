package sockets.hangman.client.controller;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import sockets.hangman.client.net.Client;
import sockets.hangman.client.net.ResponseHandler;
public class Controller {
	private Client client = new Client();
	
	public void connect(String host, int port, ResponseHandler outputer )  {
		try {
			client.connect(host, port, outputer);
		} catch (IOException e) {	
			outputer.serverResponse("Server is not responding, please try again later!");
		}
		
	}
	public void start(String username, String pass) {
		
			try {
				client.startGame(username, pass);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void guess(String guess) {
	
			try {
				client.sendGuess(guess);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void play() {
		
			try {
				client.sendPlay();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void disconnect()throws IOException{
		client.sendDisconnect();
	}
	
	
}
