package sockets.hangman.client.view;

import java.util.Scanner;

import sockets.hangman.client.controller.Controller;
import sockets.hangman.client.net.ResponseHandler;

public class View implements Runnable{
	private static final String PROMPT = "> ";
	private Controller controller;
	private boolean getingInput = false;

	/**
	 * Views main function.
	 */
	public static void main(String[] args){
		View view = new View();
		view.start();
		System.out.println("Welcome please enter Connect hostname port, hangman server is on 127.0.0.1 and port 29019");
	}

	/**
	 * Threads run just once, thus we have to continously restart them.
	 */
	public void start() {
		if(getingInput) {
			return;
		}
		else {
			getingInput = true;
			controller = new Controller();
			new Thread(this).start();

		}

	}
	/**
	 * User input interpreter.
	 */
	@Override
	public void run(){
		while(getingInput) {
			try {
				Scanner ipt = new Scanner(System.in);
				String input = ipt.nextLine();
				String command[] = getCommand(input);
				switch(command[0].toUpperCase()) {
				case "CONNECT":
					controller.connect(command[1], Integer.parseInt(command[2]), new Outputer());
					break;

				case "START":
					controller.start(command[1], command[2]);
					break;

				case "GUESS":
					controller.guess(command[1]);
					break;

				case "PLAY":
					controller.play();
					break;

				case "DISCONNECT":
					ipt.close();
					getingInput= false;
					controller.disconnect();
					break;

				case "HELP":
					System.out.println("use START username password to authenticate, GUESS char or GUESS word to play or DISCONNECT to quit.");
					break;

				default:
					System.out.println("Use HELP for more info");
				}

			}catch( Exception e) {

			}
		}
	}
	/**
	 * 
	 * @param input the user input to get commands from
	 * @return returns array of commands, command, first option, second option.
	 */
	private String[] getCommand(String input){
		String[] cmd = input.split(" ");
		return cmd;
	}

	private class Outputer implements ResponseHandler{
		@Override
		public void serverResponse(String response) {
			print(response);
		}
		private synchronized void print(String output){
			System.out.println(output);
			System.out.print(PROMPT);
		}
	}


}
