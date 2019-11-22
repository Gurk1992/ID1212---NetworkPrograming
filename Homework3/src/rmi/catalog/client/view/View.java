package rmi.catalog.client.view;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.io.File;

import rmi.catalog.common.Credentials;
import rmi.catalog.common.FileDTO;
import rmi.catalog.common.CatalogClient;
import rmi.catalog.common.CatalogServer;
import rmi.catalog.client.net.FileClient;
import rmi.catalog.client.net.ResponseHandler;




public class View implements Runnable{
	private static final String PROMPT = "> ";
	private boolean getingInput = false;
	private final CatalogClient myRemoteObj;
    private CatalogServer server;
    private long myIdAtServer =0;
    private String hostname = "192.168.0.3";
    private FileClient fileClient;

	/**
	 * Views main function.
	 */
	public static void main(String[] args){
		View view;
		try {
			view = new View();
			view.start();
			System.out.println("Welcome To the server, use 'Login username password' or 'Register username password' to login to the server");
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
	}
	public View() throws RemoteException{
		myRemoteObj= new Outputer();
	}

	/**
	 * Threads run just once, thus we have to continously restart them.
	 */
	public void start() {
		
		if(getingInput) {
			return;
		}
		else {
			getingInput = true;
			new Thread(this).start();

		}

	}
	/**
	 * User input interpreter.
	 */
	@Override
	public void run(){
		while(getingInput) {
			try {
				Scanner ipt = new Scanner(System.in);
				String input = ipt.nextLine();
				String command[] = getCommand(input);
				lookupServer();
				switch(command[0].toUpperCase()) {
					case "REGISTER":
						if(notLoggedIn()) {
							 server.register(myRemoteObj, new Credentials(command[1], command[2]));
						}
						else {
							System.out.println("You are alredy logged in.");
						}
						
					break;
					case "LOGIN":
						if(notLoggedIn()) {
							myIdAtServer = server.login(myRemoteObj, new Credentials(command[1], command[2]));
							fileClient = new FileClient((ResponseHandler) myRemoteObj);
						}
						else {
							System.out.println("You are alredy logged in.");
						}
					break;
					case "UPLOAD":
							if(!notLoggedIn()) {
								File file = new File(command[1]);
								if(file.exists()) {
									if(server.upload(myIdAtServer, Long.toString(file.length()),file.getName(), Boolean.valueOf((command[2])))) {
										fileClient.sendFile(new String(Files.readAllBytes(file.toPath())), file.getName() ,myIdAtServer);
									}
									else {
										System.out.println("invalid command.");
									}
								}
								else {
									System.out.println("File does not exist");
								}
							}
							else {
								System.out.println("Login to continue");
							}
						break;
						
					case "DOWNLOAD":
						if(!notLoggedIn()) {
							FileDTO file = server.downloadFile(myIdAtServer, command[1]);
							System.out.println(file.getFileName()+ " TRYING TO DL");
							if(file!=null) {
								fileClient.download(file.getFileName(), myIdAtServer);
							}
						}
						else {
							System.out.println("Login to continue");
						}
						
							
						break;
					case "DELETE":
						if(!notLoggedIn()) {
							server.deleteFile(myIdAtServer,command[1]);
						}
						else {
							System.out.println("Login to continue");
						}
						break;
					case "LS":
						if(!notLoggedIn()) {
							server.listFiles(myIdAtServer);
						}
						else {
							System.out.println("Login to continue");
						}
							
						break;
					case "LOGOUT":
						
						server.logout(myRemoteObj, myIdAtServer);
						myIdAtServer= 0;
						fileClient.sendDisconnect();
						
					break;
					case "HELP":
						System.out.println("");
						break;

					default:
						System.out.println("Use HELP for more info");
					}

			}catch( Exception e) {

			}
		}
	}
	
	private boolean notLoggedIn() {
		if(myIdAtServer ==0) {
			return true;
		}
		return false;
	}
	private void lookupServer() throws NotBoundException, MalformedURLException,
    RemoteException{
		
		server =(CatalogServer) Naming.lookup("//"+hostname+"/"+CatalogServer.SERVER_NAME_IN_REGISTRY);

	}

	/**
	 * 
	 * @param input the user input to get commands from
	 * @return returns array of commands, command, first option, second option.
	 */
	private String[] getCommand(String input){
		String[] cmd = input.split(" ");
		return cmd;
	}

	private class Outputer extends UnicastRemoteObject  implements CatalogClient, ResponseHandler{
		public Outputer() throws RemoteException {
        }
		@Override
		public void sendMsg(String msg) throws RemoteException {
			
			String[] inFromServer= msg.split("#");
			switch (inFromServer[0]) {
			case"login":
				if(Boolean.valueOf(inFromServer[1])) {
					System.out.println("Successfully logged in Welcome\nCommands\nLS - to display all files. \nupload(filepath premission) - to upload a file. \nDownload filepath - to download a file \nDelete filepath - to delete a file.\nlogout - to logout");
					}
					else {
						System.out.println("Invalid username, please try again!");
					}
			break;
			
			case"register":
				if(Boolean.valueOf(inFromServer[1])) {
					System.out.println("You have successfully registered a new account. Please procced to login.");
					}
					else {
						System.out.println("Could not register account, please try again");
					}
				break;
				
			case"logout":
				System.out.println(inFromServer[1]+" have successfully been logged out.");
			break;
			
			case"invalidPass":
				System.out.println("Incorrect Username or password, please try again!");
				break;
				
			case"notLogged":
				System.out.println("Can't logout, you are not logged in, please login");
				break;
				
			case"fileStatus":
					if(Boolean.valueOf(inFromServer[1])) {
						System.out.println("File has been successfully uploaded");
					}
					else {
						System.out.println("File has not been uploaded");
					}
				break;
				
			case"delete":
			
				if(Boolean.valueOf(inFromServer[1])) {
					System.out.println(inFromServer[2]+" Has been deleted");
				}
				else {
					System.out.println(inFromServer[2]+" Could not be deleted please try again!");
				}
				break;
				
			case"invalidFileName":
				System.out.println("A file with this name does not exist.");
				break;
				
			case"fileExists":
					System.out.println("File name alredy exists and you do not have write premission, please choose another one.");
				break;
				
			case"noPremission":
					System.out.println("You do not have premission for this file.");
					break;
				
			case"fileChange":
					System.out.println(inFromServer[1]+" has been " +inFromServer[2]+" by "+inFromServer[3]);
				break;

			}
			
		}
		@Override
		public void sendFiles(List<FileDTO> files) throws RemoteException {
			for(FileDTO file : files) {
				System.out.println("FileName: "+file.getFileName()+" | Owner: "+file.getUserName()+" | Size: "+file.getSize()+" | Writeable: "+file.getPremissions());
			}
		}
		@Override
		public void serverResponse(String msg) {
			System.out.println(msg);
			System.out.print(PROMPT);
		}
		
		
		
	}


}