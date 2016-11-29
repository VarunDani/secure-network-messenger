package edu.utdallas.messenger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.utdallas.utils.EncryptUtil;

public class MainServer  extends Thread {

	private ServerSocket serverSocket;
	   
	   public MainServer() throws IOException {
	      serverSocket = new ServerSocket(11000);
	      //serverSocket.setSoTimeout(3000);
	   }

	   
	   public void run() 
	   {
		      while(true) 
		      {
		         try 
		         {
		            Socket server = serverSocket.accept();
		            
		            System.out.println("Just connected to " + server.getRemoteSocketAddress());
		            DataInputStream in = new DataInputStream(server.getInputStream());
		           
		            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		            byte[] buffer = new byte[1024];
		            int numRead;

		            while((numRead = in.read(buffer)) > 0) {
		                outputStream.write(buffer, 0, numRead);
		            }
		            
		            String serverString = EncryptUtil.decryptData(outputStream.toByteArray());
		            System.out.println("Decrypted Data : "+serverString);
		            
		            
		            if(serverString.startsWith("Authentication~"))
		            {
		            	//Check For Authentication // TODO
		            }
		            else if(serverString.startsWith("GetSessionKey~"))
		            {
		            	//Get Session Key For Any Other Client  // TODO
		            }
		            
		            DataOutputStream out = new DataOutputStream(server.getOutputStream());
		            out.writeUTF("Authenticated");
		            server.close();
		            
		         }
		         catch(SocketTimeoutException s) 
		         {
		            System.out.println("Socket timed out!");
		            break;
		         }
		         catch(IOException e) 
		         {
		            e.printStackTrace();
		            break;
		         }
		      }
		   }
		   
	   public static void main(String [] args) 
	   {
		  try
		  {
			  MainServer mainServ = new MainServer();
			  mainServ.start();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  
	   }

}
