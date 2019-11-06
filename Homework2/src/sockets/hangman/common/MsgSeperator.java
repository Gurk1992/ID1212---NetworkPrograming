package sockets.hangman.common;

import java.util.ArrayDeque;
import java.util.Queue;

public class MsgSeperator {
	private StringBuilder recived = new StringBuilder();
	private final Queue<String> messages = new ArrayDeque<>();
	
	/**
	 * Checks for finished words.
	 * @return true if there are words, otherwise false.
	 */
	public synchronized boolean hasNext() {
		return !messages.isEmpty();
	}
	/**
	 * gets a word from the finished wordlist.
	 * @return String with word.
	 */
	public synchronized String getNext() {
		return messages.remove();
	}
	/**
	 * Appends a recived string into the Stringbuilder with recived strings.
	 * @param recv The string to append.
	 */
	public synchronized void appendS(String recv) {
		recived.append(recv);
		while(Extract());
	}
	/**
	 * Checks if the recived string has recived a complete message.
	 * If it has it appends the whole message into the message queue.
	 * @return true if there is a complete word in the recived stringbuilder otherwise false.
	 */
	private boolean Extract() {
		String allRecived = recived.toString();
		String[] checkHeader = allRecived.split(Constants.LENGHT_DELIMITER);
		if(checkHeader.length <2) {
			return false;
		}
		int length = Integer.parseInt(checkHeader[0]);
		if(length>=checkHeader[1].length()) {
			messages.add(checkHeader[1].substring(0, length));
			recived.delete(0,checkHeader[0].length()+length+Constants.LENGHT_DELIMITER.length());
			return true;
		}
		return false;
	}

}
