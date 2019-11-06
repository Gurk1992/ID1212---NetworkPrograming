package sockets.hangman.common;

public class HangmanException extends RuntimeException {
	
	public HangmanException(String msg, Throwable err) {
		super(msg, err);
	}
	
}
