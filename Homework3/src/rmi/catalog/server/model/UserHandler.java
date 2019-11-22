package rmi.catalog.server.model;



import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import rmi.catalog.server.net.ClientHandler;


public class UserHandler {
	private final Random rand = new Random();

	private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());
	
	public UserHandler() {		
	}
	/**
	 *  Puts the user in the logged in list.
	 * @param newUser User object.
	 * @return userId, the id of the user whilst logged in.
	 */
	public Long login(User newUser) {
		long userId= rand.nextLong();
		
		users.put(userId, newUser);
	
		return userId;
	}

	/**
	 * finds currently logged in user with userid.
	 * @param id
	 * @return
	 */
	public User findUser(long id) {
        return users.get(id);
    }
	/**
	 * log a user out.
	 * @param userId
	 */
	public void logout(long userId) {
		User user= findUser(userId);
		user.sendMsg("logout#"+user.getUserDTO().getUsername());
		users.remove(userId);
	}

	public void addclientHandler(long userId, ClientHandler clientHandler) {
		User user = findUser(userId);
		user.addClientHandler(clientHandler);
	}
}