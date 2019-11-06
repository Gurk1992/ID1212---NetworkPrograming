package sockets.hangman.common;



/**
 * Defines the structure of the messages used by both client and server.
 */
public class Constants {
    	    /**
	     * Separates a message type from body.
	     */
	    public static final String MSG_DELIMETER = "#";
	    /**
	     * Specifies index of type in message between server and client.
	     */
	    public static final int MSG_TYPE =0;
	    /**
	     * Specifies index of body in message between server and client.
	     */
	    public static final int MSG_BODY =1;
	    /**
	     * Specifies the index of the JWTKEY inside the body.
	     */
	    public static final int MSG_KEY =2;
	    
	    /**
	     * Separates different data in the body.
	     */
	    public static final String BODY_DELIMETER = "&";
	    /**
	     * Specifies the index of the word inside the body.
	     */
	    public static final int WORD_BODY =0;
	    /**
	     * Specifies the index of the attempts inside the body.
	     */
	    public static final int ATTEMPTS_BODY =1;
	    /**
	     * Specifies the index of the score inside the body.
	     */
	    public static final int SCORE_BODY =2;
	    
	   
	    
	}
	

