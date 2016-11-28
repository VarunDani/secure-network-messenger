package edu.utdallas.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
		            
		            System.out.println(in.readUTF());
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
