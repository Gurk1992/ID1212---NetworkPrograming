package rmi.catalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface CatalogServer extends Remote {
	
	// URI of catalog server
	public static final String SERVER_NAME_IN_REGISTRY ="CATALOG_SERVER";
	
	/**
	 * 
	 * @param remoteNode remoteObject that will be used to send messages to the particiipant.
	 * @param credentials the login info of the participant
	 * @return Id of the participant. Id is identifier.
	 * @throws RemoteException
	 */
	
	long login(CatalogClient remoteNode, Credentials credentials) throws RemoteException;



	void register(CatalogClient remoteNode, Credentials credentials) throws RemoteException;


	void logout(CatalogClient myRemoteObj, long userId) throws RemoteException;


	boolean upload(long myIdAtServer, String size, String name, boolean writeable)throws RemoteException;



	void listFiles(long myIdAtServer)throws RemoteException;



	FileDTO downloadFile(long myIdAtServer, String fileName)throws RemoteException;



	void deleteFile(long myIdAtServer, String fileName)throws RemoteException;
	
	
	
	
}
