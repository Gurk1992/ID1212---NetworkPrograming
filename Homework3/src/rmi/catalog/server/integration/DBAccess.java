package rmi.catalog.server.integration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;




import rmi.catalog.common.FileDTO;
import rmi.catalog.common.UserDTO;

public class DBAccess {
	    private Connection connection;
	    private String URL = "jdbc:mysql://localhost:3306/catalog?autoReconnect=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
	    private String driver = "com.mysql.jdbc.Driver";
	    private String userID = "roots";
	    private String password = "1234";

	    private PreparedStatement createAccountStmt;
	    private PreparedStatement findAccountStmt;
	    private PreparedStatement findAllAccountsStmt;
	    private PreparedStatement checkFileStmt;
	    private PreparedStatement storeFileStmt;
	    private PreparedStatement updateFileStmt;
	    private PreparedStatement findAllFilesStmt;
	    private PreparedStatement deleteFileStmt;

	    /**
	     * @return the instance of this class.
	     */
	    public  DBAccess() {
	        try {
				connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	    /**
	     * Connects to the database with the information in the instance variables and
	     * sets up a commit configuration.
	     * 
	     * @throws Exception if failed to connect to the database.
	     */
	    private void connect() throws Exception {
	        Class.forName(driver);
	        this.connection = DriverManager.getConnection(URL, userID, password);
	    
	        //this.connection.setAutoCommit(false);
	    }

	    public boolean createUser(UserDTO userDTO) throws SQLException  {
	    	createAccountStmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
	    	System.out.println(createAccountStmt);
	        try {
	            createAccountStmt.setString(1, userDTO.getUsername());
	            createAccountStmt.setString(2, userDTO.getPassword());
	            System.out.println(createAccountStmt);
	            int rows = createAccountStmt.executeUpdate();
	            
	            if (rows != 1) {
	                return false;
	            }
	        } catch (SQLException sqle) {
	        	sqle.printStackTrace();
	        }
	        return true;
	    }
	    public UserDTO findUser(String name) throws SQLException {
				findAccountStmt = connection.prepareStatement("SELECT * from users WHERE username= ?");
				findAccountStmt.setString(1, name);
				ResultSet result = findAccountStmt.executeQuery();
				if(result.next()) {
					return new UserDTO(result.getString("username"),result.getString("password"));
				}
	    	return null;
	    	
	    }
	    public List<UserDTO> getUsers() throws SQLException {
	    	findAllAccountsStmt = connection.prepareStatement("Select * FROM users");
	    	List<UserDTO> accounts = new ArrayList<>();
	    	try(ResultSet result = findAllAccountsStmt.executeQuery()){
	    		while(result.next()) {
	    			accounts.add(new UserDTO(result.getString("username"), result.getString("password")));
	    		}
	    	}catch(SQLException s) {
	    		s.printStackTrace();
	    	}
	    	return accounts;
	    }
	    
	    public FileDTO checkFile(String fileName) throws SQLException{
	    	checkFileStmt = connection.prepareStatement("SELECT * from files where name = ?");
	    	checkFileStmt.setString(1, fileName);
	    	ResultSet result = checkFileStmt.executeQuery();
	    	if(result.next()) {
	    		return new FileDTO(result.getString("user"),result.getString("name"),result.getBoolean("premission"), result.getInt("size"), result.getLong("loggedIn"));
	    	}
	    	return null;
	    }
		public boolean storeFile(String username, String name, boolean writeable, String size, long userId) throws SQLException {
			
			storeFileStmt = connection.prepareStatement("INSERT INTO files (name, user, premission, size, loggedIn) VALUES (?, ?, ?,?,?)");
			try {
				storeFileStmt.setString(1, name);
				storeFileStmt.setString(2, username);
				storeFileStmt.setBoolean(3, writeable);
				storeFileStmt.setString(4, size);
				storeFileStmt.setLong(5, userId);
	            int rows = storeFileStmt.executeUpdate();
	            System.out.println(rows);
	            if (rows != 1) {
	            	 System.out.println("storeFile "+rows);
	                return false;
	            }
	        } catch (SQLException sqle) {
	        	
	        	sqle.printStackTrace();
	        }
	        return true;
			
		}

		public boolean updateFile(String username, String name, boolean writeable, String size, long userId) throws SQLException {
			updateFileStmt =connection.prepareStatement("UPDATE files SET user=?, premission=?, size=?,loggedIn=? WHERE name=? ");
			try {
				
				updateFileStmt.setString(1, username);
				updateFileStmt.setBoolean(2, writeable);
				updateFileStmt.setString(3, size);
				updateFileStmt.setLong(4, userId);
				updateFileStmt.setString(5, name);
		
				int rows =updateFileStmt.executeUpdate();
				if(rows !=1) {
					return false;
				}
			}catch(SQLException sqle) {
				sqle.printStackTrace();
			}
			return true;
		}

		public List<FileDTO> getAllFiles() throws SQLException {
		    findAllFilesStmt = connection.prepareStatement("SELECT * FROM files");
		    List<FileDTO> files = new ArrayList<>();
		    
		    try(ResultSet result = findAllFilesStmt.executeQuery()){
		    	while(result.next()) {

		    		files.add(new FileDTO(result.getString("user"), result.getString("name"), result.getBoolean("premission"), result.getInt("size"), result.getLong("loggedIn")));
		    	}
		    }catch(SQLException s) {
		    		s.printStackTrace();
		    }
		    return files;
		}

		public boolean deleteFile(String fileName) {
			try {
				deleteFileStmt = connection.prepareStatement("DELETE from files WHERE name =?");
				deleteFileStmt.setString(1, fileName);
				int rows = deleteFileStmt.executeUpdate();
				if (rows!=1) {
					return false;  
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}
			return true;
			
		}
	
	    
	   
	    
	    
}
