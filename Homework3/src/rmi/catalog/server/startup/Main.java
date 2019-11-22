package rmi.catalog.server.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import rmi.catalog.server.controller.Controller;
import rmi.catalog.server.net.CataServer;



public class Main {

	public static void main(String[] args) throws RemoteException {
		Controller contr;
			contr = new Controller();
		 try {
	            new Main().startRegistry();
	            Naming.rebind(contr.SERVER_NAME_IN_REGISTRY,contr);
	            System.out.println("Server is running. With regestry: " +Controller.SERVER_NAME_IN_REGISTRY);
	        } catch (MalformedURLException | RemoteException ex) {
	            System.out.println("Could not start catalog server."+ ex);
	        }
		 CataServer server = new CataServer(contr);
			System.out.println("Server up!");
			server.serve();
	}
	private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
	
}
