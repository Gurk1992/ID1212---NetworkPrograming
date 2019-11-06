package sockets.hangman.server.controller;


import sockets.hangman.server.model.Hangman;
/**
 * Could probably be removed.
 *
 */
public class Controller {
	private Hangman hangman = new Hangman();
	
	 public String getWord(){
		 
	        return hangman.getWord();
	    }
	    public String hang(String guess){
	       return hangman.hang(guess);
	    }
}
