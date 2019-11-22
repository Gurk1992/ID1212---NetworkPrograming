package rmi.catalog.common;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
public class ByteReader {
		
		/**
		 * Reads in the first int(length of message to read), in while look reads all the bytes
		 * corresponding to the length int.  
		 * @param in datainputstream from client or server.
		 * @return String of the message.
		 * @throws IOException
		 */

		public String readData(DataInputStream in) throws IOException {
			
			int length =(int)in.readLong();

			byte[] messageByte = new byte[length];
			StringBuilder str = new StringBuilder(length);
			boolean end = false;
			int totalBytesRead = 0;
			while(!end) {
				int bytesRead = in.read(messageByte);
				totalBytesRead = bytesRead+totalBytesRead;
				if(totalBytesRead <= length) {
					str.append(new String(messageByte, 0, bytesRead, StandardCharsets.UTF_8));
				}else {
			        str.append(new String(messageByte, 0, length - totalBytesRead + bytesRead, StandardCharsets.UTF_8));  
			    }
				if(str.length()>=length) {
			        end = true;
			    }
			}
		
			return str.toString();
		}
}


