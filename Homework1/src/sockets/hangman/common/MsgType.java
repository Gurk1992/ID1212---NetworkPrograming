package sockets.hangman.common;

public enum MsgType {
	/**
	 * Starts a new game of hangman, only able to start game if user is registered.
	 */
	START,
	/**
	 * Specifies a guess from the client to the server
	 */
	 GUESS,
	 /**
	  * Command to start a new game once logged in.
	  */
	 PLAY, 
	 /**
	  * Server game responses
	  */
	 GAMERESPONSE, 
	 
	 /**
	 * Disconnects user from server
	 */
	DISCONNECT,
	/**
	 * Wrong username/password
	 */
	INVALIDLOGIN,
	/**
	 * Invalid Input to server.
	 */
	INVALIDINPUT,
	/**
	 * Invalid ACCESSTOKEN to server.
	 */
	INVALIDACCESSTOKEN,
}
