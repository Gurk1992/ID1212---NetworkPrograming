package sockets.hangman.server.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import sockets.hangman.common.Constants;

public class Hangman{
	private String hangmanWord;
	private String guessedWord;
	private int lenght;
	private int attempts;
	private int score =0;
	private String status;
	private Random random = new Random();
	private List<String> list = new ArrayList<String>();
	/**
	 * Constructor for Hangman.
	 * Reads in the wordlist in a separate thread.
	 */
	public Hangman(){
		CompletableFuture.runAsync(() -> {
			try {
				readNioFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * Gets a random word from the text file with words.
	 * It also setup guessed word.
	 */
	public String getWord() {
		this.hangmanWord = randomWord();
		setLenght();
		return this.status;
	}
	/**
	 * Checks the guessed word or char against the random hangmanWord.
	 */

	public String hang(String guess){
		if(attempts == 0) {
			System.out.println(status);
			return this.status;
		}
		boolean correct = false;
		// if guess = words => win.
		if(hangmanWord.contentEquals(guess)){
			score =score+ 1;
			attempts = 0;
			this.guessedWord=this.hangmanWord;
			setStatus();
			return this.status;
		}
		//If guess is only 1 char.
		if(guess.length()==1){
			for(int i = 0; i<this.lenght; i++){
				if(hangmanWord.charAt(i) == guess.charAt(0)){
					String newGuessed = guessedWord.substring(0,i) + guess.charAt(0) + guessedWord.substring(i+1);
					guessedWord=newGuessed;
					correct = true;
				}
			}
			if(guessedWord.contentEquals(hangmanWord)){
				score++;
				attempts = 0;
				setStatus();
				return this.status;
			}
		}
		if(!correct) {
			attempts= attempts-1;
			if(attempts == 0) {
				score = score -1;
				attempts = 0;
				setStatus();
				return this.status;
			}
		}
		setStatus();
		return this.status;
	}
	/**
	 * Puts the words of the words.txt file into the list. (-1 = end of stream)
	 * @throws IOException If we could not open file.
	 */
	private void readNioFile() throws IOException   {
		StringBuilder str = new StringBuilder();
		File file = new File(getClass().getResource("words.txt").getFile());
		FileInputStream inf = new FileInputStream(file);
		FileChannel inChannel = inf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
		int c = 0;
		while ((c = inChannel.read(buffer)) != -1) {
			buffer.flip();
			String s = getString(buffer);
			buffer.clear();
			str.append(s);
		}
		buffer.clear();
		praseWords(str.toString());
	}
	/**
	 * Transfers bytes from the buffer into a String
	 * @param buffer with the bytes from the input file.
	 * @return a String with all the chars from the ByteArray.
	 */
	private String getString(ByteBuffer buffer) {
		byte[] bytes= new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes);
	}
	/**
	 * Splits the input String at each newline, and puts 1 word into each array position in the list.
	 * @param string A String filled with all the chars from the Input.
	 */
	private void praseWords(String string) {
		list = Arrays.asList(string.split("\\s*\n*\\s"));
	}
	/**
	 * From the randomWord the gussedword is filled with "_" and the game status is set.
	 */
	private void setLenght() {
		this.lenght = this.attempts = hangmanWord.length();
		StringBuilder str = new StringBuilder();
		for(int i = 0; i<this.lenght; i++){
			str.append("_");
		}
		guessedWord = str.toString();
		setStatus();
	}
	/**
	 *  The game status is put into a String format with the body_delimiter at the right places.
	 */
	private void setStatus(){
		this.status=guessedWord+Constants.BODY_DELIMETER+attempts+Constants.BODY_DELIMETER + score;
	}
	/**
	 * Gets the random word from our list of words using math library.
	 * @return a random word from the list.
	 */
	private String randomWord()  {
		return list.get(random.nextInt((list.size()))).toUpperCase();
	}

}


