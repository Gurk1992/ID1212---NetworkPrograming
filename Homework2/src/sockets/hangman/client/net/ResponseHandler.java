package sockets.hangman.client.net;

public interface ResponseHandler {
	/**
     * Called when a broadcast message from the server has been received. That message originates
     * from one of the clients.
     *
     * @param msg The message from the server.
     */
    public void serverResponse(String msg);
}
