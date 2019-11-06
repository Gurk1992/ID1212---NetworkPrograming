package sockets.hangman.common;

public class HangmanException extends RuntimeException {
	
	public HangmanException(String msg) {
		super(msg);
	}
	public HangmanException(Throwable rootCause) {
        super(rootCause);
    }
	
}
