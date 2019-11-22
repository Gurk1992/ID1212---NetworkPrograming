package rmi.catalog.common;

import java.io.Serializable;

public class FileDTO implements Serializable{
	
		private String userName;
		private String fileName;
		private boolean premissions;
		private int size;
		private long owner; 
		
		public FileDTO(String userName, String fileName, boolean premissions, int size,long owner) {
			this.userName = userName;
			this.fileName = fileName;
			this.premissions= premissions;
			this.size = size;
			this.setOwner(owner);
		}

		public String getFileName() {
			return this.fileName;
		}
		
		public boolean getPremissions() {
			return this.premissions;
		}
		public int getSize() {
			return this.size;
		}
		public String getUserName() {
			return this.userName;
		}
		public void setUserName(String userName) {
			this.userName=userName;
		}
		public void setFileName(String fileName) {
			this.fileName= fileName;
		}
		public void setFilePremissions(boolean premission) {
			this.premissions= premission;
		}
		public void setSize(int size) {
			this.size= size;
		}

		public long getOwner() {
			return owner;
		}

		public void setOwner(long owner) {
			this.owner = owner;
		}
}
