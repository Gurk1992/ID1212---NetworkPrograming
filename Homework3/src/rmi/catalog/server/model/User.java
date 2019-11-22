package rmi.catalog.server.model;

import java.rmi.RemoteException;
import java.sql.SQLException;

import rmi.catalog.common.CatalogClient;
import rmi.catalog.common.FileDTO;
import rmi.catalog.common.UserDTO;
import rmi.catalog.server.integration.DBAccess;
import rmi.catalog.server.net.ClientHandler;
import java.util.ArrayList;
import java.util.List;

public class User {
	private final CatalogClient remoteNode;
	private final UserHandler userHandler;
	private transient DBAccess catalogDB;
	private UserDTO userDTO;
	private ClientHandler clientHandler;

	public User(String userName,String userPass , CatalogClient remoteNode, UserHandler userHandler) {
	
		this.userHandler = userHandler;
		this.remoteNode = remoteNode;
		this.catalogDB = new DBAccess();
		this.userDTO = new UserDTO(userName, userPass);
		
	}
	
	
	public UserDTO getUserDTO(){
		this.userDTO.getUsername();
		return this.userDTO;
	}
	public void addClientHandler(ClientHandler clientHandler) {
		this.clientHandler= clientHandler;
		
	}
	public ClientHandler getClientHandler() {
		return this.clientHandler;
	}
	
	public void sendMsg(String msg) {
		try {
            remoteNode.sendMsg(msg);
        } catch (RemoteException re) {
       }
	}
	public void sendFiles(List<FileDTO> files) {
		try {
			remoteNode.sendFiles(files);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
