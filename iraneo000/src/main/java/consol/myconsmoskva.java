package consol;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class myconsmoskva {
	static String[] partsout(String[] array, int index)
	{
		 String [] result = new String[array.length-index];
		 for (int i=index; i<(array.length); i++)
		  {
			 result[i-index] = array[i];
			 
	  	  }
		return result;
	}
	
	static void ping(Socket socket) throws IOException 
	{
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeInt(1);
		dos.writeByte(1);
		byte[] nbm = new byte[dis.readInt()]; 
		dis.readFully(nbm);
		if(nbm.length == 1 && nbm[0] == 2) {
				System.out.println("Ping successfull");
			}
		else System.out.println("Ping unsuccessfull");
	}
	static void echo(Socket socket, String str) throws IOException 
	{
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		byte[] BytesOfString = str.getBytes();
		dos.writeInt(BytesOfString.length+1);
		dos.writeByte(3);
		dos.write(BytesOfString);
		byte[] nbm = new byte[dis.readInt()]; 
		dis.readFully(nbm);
				if(nbm.length > 1) {
		String str_received= new String(nbm, 0, nbm.length);
		System.out.println(str_received);
			}
		else System.out.println("Error "+nbm[0]);
	}
	
	
	@SuppressWarnings("null")
	public static void main(String[] args) {
				String[] parts;
		
				InputStreamReader isr = new InputStreamReader ( System.in );
		BufferedReader br = new BufferedReader ( isr );
				String s = null;
				System.out.printf("Enter String%n");
	
				
				
				
				try {
		   while ( (s = br.readLine ()) != null ) {
			  parts = s.split(" ");
			  Socket socket = null;
			switch (parts[0]) {
	           
			  case "ping": ping(socket);  
  	       	break;
  case "echo": echo(socket,String.join(" ", partsout(parts,1)));          	
  	            break;
			  
			  
			
	        
	        }
		   }
		}
		catch ( IOException ioe ) {
		   // won't happen too often from the keyboard
		}
	}

}

