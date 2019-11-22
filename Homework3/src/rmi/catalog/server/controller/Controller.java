package rmi.catalog.server.controller;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

import rmi.catalog.common.Credentials;
import rmi.catalog.common.FileDTO;
import rmi.catalog.common.UserDTO;
import rmi.catalog.server.integration.DBAccess;
import rmi.catalog.server.model.User;
import rmi.catalog.server.model.UserHandler;
import rmi.catalog.server.net.ClientHandler;
import rmi.catalog.common.CatalogClient;
import rmi.catalog.common.CatalogServer;


public class Controller extends UnicastRemoteObject implements CatalogServer {
	private final UserHandler userHandler;
	private final DBAccess con;
	private final String fileDir = System.getProperty("user.dir")+"\\Files\\";
	public Controller() throws RemoteException {
		userHandler=new UserHandler();
		this.con = new DBAccess();
	}
	
	@Override
	public long login(CatalogClient remoteNode, Credentials credentials) {
		try {
			
			UserDTO user =con.findUser(credentials.getUsername());
		
			if(user !=null) {
				if(user.getPassword().contentEquals(credentials.getPassword())&& user.getUsername().contentEquals(credentials.getUsername())){
					User newUser= new User(credentials.getUsername(), credentials.getPassword(), remoteNode, userHandler);
					Long userId = userHandler.login(newUser);
					newUser.sendMsg("login#true");
					return userId;
				} else {
					remoteNode.sendMsg("invalidPass");
				}
			}
		} catch (SQLException | RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("försök till skicka false");
			remoteNode.sendMsg("login#false");
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
		
		return 0;
	}
	@Override
	public void register(CatalogClient remoteNode, Credentials credentials) {
		UserDTO userDto = new UserDTO(credentials.getUsername(), credentials.getPassword());
		boolean sucess;
		try {
			if(sucess =checkUser(credentials)){
				remoteNode.sendMsg("register#"+sucess);
			}
			else {
				if(sucess = con.createUser(userDto))
				{
					remoteNode.sendMsg("register#"+sucess);
				}
			}
		} catch (SQLException | RemoteException e) {
			try {
				remoteNode.sendMsg("register#"+"false");
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	@Override
	public void logout(CatalogClient myRemoteObj, long userId) {
		userHandler.logout(userId);
	}
	
	@Override
	public boolean upload(long myIdAtServer, String size, String name, boolean writeable) {
			boolean status = false;
			User user = userHandler.findUser(myIdAtServer);
			UserDTO userDto = user.getUserDTO();
			try {
				FileDTO file = con.checkFile(name);
				
				if(file != null) {
					
					if(checkPremission(file, userDto)) {
						status = this.con.updateFile(userDto.getUsername(), name, writeable, size, myIdAtServer);
						 updateUser(status, user, userDto, file, myIdAtServer, "updated");
					}
					else {
						user.sendMsg("fileExists#");
						status= false;
					}
				}
				else {
					status = con.storeFile(userDto.getUsername(), name, writeable, size, myIdAtServer);
					
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return status;

	}
	@Override
	public void listFiles(long myIdAtServer) throws RemoteException {
		User user =userHandler.findUser(myIdAtServer);
		if(user != null) {
			try {
				List<FileDTO> fileDTO =this.con.getAllFiles();
				user.sendFiles(fileDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public FileDTO downloadFile(long myIdAtServer, String fileName) throws RemoteException {
		User user = userHandler.findUser(myIdAtServer);
		try {
			FileDTO file = con.checkFile(fileName);
			if(file !=null) {
				updateUser(true, user, user.getUserDTO(), file, myIdAtServer, "downloaded");
				return file;
			}
			else {
				user.sendMsg("invalidFileName#");
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void deleteFile(long myIdAtServer, String fileName) throws RemoteException {
		User user = userHandler.findUser(myIdAtServer);
		UserDTO userDTO = user.getUserDTO();
		try {
			FileDTO file = con.checkFile(fileName);
			if(file != null) {
				if(checkPremission(file, userDTO)) {
					File f = new File(fileDir+file.getFileName());
					if(f.delete()) {
						boolean status = con.deleteFile(fileName);
						updateUser(status, user, userDTO, file, myIdAtServer, "deleted");
						
						user.sendMsg("delete#true"+"#"+file.getFileName());
					}
					else {
						user.sendMsg("delete#false"+"#"+file.getFileName());
					}
				}
				else {
					user.sendMsg("noPremission#");
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public int getFileSize(String name) {
		FileDTO file;
		try {
			file = con.checkFile(name);
			return file.getSize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public UserHandler getUserHandler() {
		return this.userHandler;
	}
	
	public User findUser(long userId) {
		return userHandler.findUser(userId);
	}

	public void addclientHandler(long userId, ClientHandler clientHandler) {
		userHandler.addclientHandler(userId, clientHandler);
	}
	
	/**
	 * Finds out if username exists in database. if no user return true.
	 * @param credentials
	 * @return
	 */
	private boolean checkUser(Credentials credentials) {
		try {
			if(con.findUser(credentials.getUsername())!=null) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 *  Check if file owner equals a user
	 * @param file the file
	 * @param userDTO the user
	 * @return true if user is the owner, else false.
	 */
	private boolean checkPremission(FileDTO file, UserDTO userDTO)
	{
		if(file.getPremissions() || file.getUserName().contentEquals(userDTO.getUsername())) {
			
			return true;
		}
		else {
			
			return false;
		}
		
	}
	private boolean updateUser(boolean status, User user,UserDTO userDto, FileDTO file, long myIdAtServer, String msgType) throws SQLException {
		if(status) {
			User prevUser =userHandler.findUser(file.getOwner());
			if(prevUser!=null && (!file.getUserName().contentEquals(userDto.getUsername()))) {
				prevUser.sendMsg("fileChange#"+file.getFileName()+"#"+msgType+"#"+userDto.getUsername());
				return true;
			}
			return true;
		}
		else {
			user.sendMsg("fileStatus#"+status);
			return false;
		}
	}
	
}
