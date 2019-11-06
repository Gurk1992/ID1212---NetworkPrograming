package sockets.hangman.server.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import sockets.hangman.common.Constants;

public class Hangman{
    private String hangmanWord;
    private String guessedWord;
    private int lenght;
    private int attempts;
    private int score =0;
    private String status;
    private Random random = new Random();

    /**
     * Gets a random word from the text file with words.
     * It also setup guessed word.
     * 
     */
    public String getWord(){
        try {
			this.hangmanWord = randomWord();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        this.lenght = this.attempts = hangmanWord.length();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i<this.lenght; i++){
            str.append("_");
        }
        guessedWord = str.toString();
        setStatus();
        return this.status;
    }
    /**
     * Working but bad logic for a hangman game :-) 
     * @param guess The users guess whole word or char.
     * @return status of the game gussedword with guessedchars at right place, score and attempts left.
     */
    public String hang(String guess){
    	if(attempts == 0) {
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
    
    private void setStatus(){
    	this.status=guessedWord+Constants.BODY_DELIMETER+attempts+Constants.BODY_DELIMETER + score;
    }
    /**
     * Reads in the wordlist, and saves every entry in a ArrayList, using the arraylist lenght a random int is picked out
     * that random it is the position of our random word.
     * @return Random word from wordlist
     * @throws FileNotFoundException
     */
    private String randomWord() throws FileNotFoundException {
    	
    File file = new File(getClass().getResource("words.txt").getFile());
    Scanner input =new Scanner(file);
    ArrayList<String> list = new ArrayList<String>();

    while(input.hasNext()){
        list.add(input.next());
    }
    input.close();
    return list.get(random.nextInt((list.size()))).toUpperCase();
    }
    
}

