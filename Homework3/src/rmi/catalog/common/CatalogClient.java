package rmi.catalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
public interface CatalogClient extends Remote {

	void sendMsg(String msg)throws RemoteException;
	void sendFiles(List<FileDTO> files)throws RemoteException;
}
