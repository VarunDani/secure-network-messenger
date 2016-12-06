package edu.utdallas.messenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.utdallas.utils.AES;
import edu.utdallas.utils.EncryptUtil;
import model.MessegeSendBean;

public class MainServer  extends Thread {

	private ServerSocket serverSocket;
	   
	   public MainServer() throws IOException {
	      serverSocket = new ServerSocket(11000);
	      //serverSocket.setSoTimeout(3000);
	   }

	   //Listener for Server Thread
	   public void run() 
	   {
		      while(true) 
		      {
		         try 
		         {
		            Socket server = serverSocket.accept();
		            
		            
		            InputStream inFromServer = server.getInputStream();
			        ObjectInputStream in = new ObjectInputStream(inFromServer);
			         
			         
		            System.out.println("Just connected to " + server.getRemoteSocketAddress());
		         /*  
		            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		            byte[] buffer = new byte[1024];
		            int numRead;

		            while((numRead = in.read(buffer)) > 0) {
		                outputStream.write(buffer, 0, numRead);
		            }*/
		            
		            
		            MessegeSendBean msgBean = (MessegeSendBean)in.readObject();
		            
		            
		            BigInteger nonce = new BigInteger(EncryptUtil.decryptData(msgBean.getKey()));
		            byte[] IV = msgBean.getIV();
		            String decryptedData = AES.decryptUseingAES(nonce, IV, msgBean.getAESData());
		            
		            System.out.println("This Should Work : "+decryptedData);
		            
		        	byte[] aesData;
		        	MessegeSendBean replyBean = new MessegeSendBean();
		            if(decryptedData.startsWith("Authentication~"))
		            {
		            	//Check For Authentication // TODO
		            	String[] userData = decryptedData.split("~");
		            	
		            	//If authenticated
		            	if(PasswordStore.passwordList.get(userData[1])!=null && PasswordStore.passwordList.get(userData[1]).equals(userData[2]))
		            	{
		            		//Authenticated User - Send Buddy List 
		            		if(!EncryptUtil.currentTimeStampChecking(userData[3]))
		            		{
		            			throw new Exception ("This might be replay Attack from : "+server.getRemoteSocketAddress());
		            		}
		            		IV = AES.getIVSpecs();
		            		 aesData = AES.encrypyUseingAES(nonce, IV, "Authenticated"+BuddyList.getBuddyList(userData[1])+"~"+System.currentTimeMillis());
		            		 
		            		 replyBean.setIV(IV);
		            		 replyBean.setAESData(aesData);
		            		 
		            		 UserInfo.ipAddressMap.put(userData[1], server.getRemoteSocketAddress().toString().split("/")[1].split(":")[0]);
		            		 UserInfo.nonceInfo.put(userData[1], nonce);
		            		 //System.out.println("IP : "+UserInfo.ipAddressMap.get(userData[1]));
		            	}
		            	else
		            	{
		            		//Not Authenticated 
		            		IV = AES.getIVSpecs();
		            		 aesData = AES.encrypyUseingAES(nonce, IV, "UnAuthenticated"+"~"+System.currentTimeMillis());
		            		 replyBean.setIV(IV);
		            		 replyBean.setAESData(aesData);
		            	}
		            	 OutputStream outToServer = server.getOutputStream();
			        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
			        	 
		            	 out.writeObject(replyBean);
		            }
		            else if(decryptedData.startsWith("GetSessionKey~"))
		            {
		            	//Get Session Key For Any Other Client  
		            	String[] userData = decryptedData.split("~");
		            	
		            	if(!EncryptUtil.currentTimeStampChecking(userData[3]))
	            		{
	            			throw new Exception ("This might be replay Attack from : "+server.getRemoteSocketAddress());
	            		}
		            	
		            	
		            	//check if another User is online
		            	if(UserInfo.ipAddressMap.get(userData[2])!=null)
		            	{
		            		//Generate Key For Client to Client Communication
		            		BigInteger commonKey = EncryptUtil.generateNonce();
		            		byte[] commonIV = AES.getIVSpecs();
		            		byte[] ticket = AES.encrypyUseingAES(UserInfo.nonceInfo.get(userData[2]), commonIV, 
		            				commonKey.toString()+"~"+userData[1]);
		            		
		            		IV = AES.getIVSpecs();
		            		 aesData = AES.encrypyUseingAES(UserInfo.nonceInfo.get(userData[1]), IV, 
		            				 "Available~"+userData[2]+"~"+UserInfo.ipAddressMap.get(userData[2])+"~"+commonKey+"~"+System.currentTimeMillis());
		            		 
		            		 replyBean = new MessegeSendBean();
		            		 replyBean.setIV(IV);
		            		 replyBean.setAESData(aesData);
		            		 replyBean.setTicket(ticket);
		            		 replyBean.setTicketIV(commonIV);
		            	}
		            	else
		            	{
		            		//User is offline 
		            		IV = AES.getIVSpecs();
		            		 aesData = AES.encrypyUseingAES(UserInfo.nonceInfo.get(userData[1]), IV, 
		            				 "NotAvailable~"+System.currentTimeMillis());
		            		 
		            		 replyBean = new MessegeSendBean();
		            		 replyBean.setIV(IV);
		            		 replyBean.setAESData(aesData);
		            		 
		            	}
		            	 OutputStream outToServer = server.getOutputStream();
			        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
			        	 
		            	 out.writeObject(replyBean);	
		            }
		            
		            server.close();
		            
		         }
		         catch(SocketTimeoutException s) 
		         {
		            System.out.println("Socket timed out!");
		         }
		         catch(IOException e) 
		         {
		            e.printStackTrace();
		         }
		         catch(Exception ee)
		         {
		        	 ee.printStackTrace();
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
