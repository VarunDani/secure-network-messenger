package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.MessegeSendBean;
import util.AES;
import util.EncryptUtil;

public class Controller implements Initializable{

	
	static  String SERVER_IP = null;
	static  int SERVER_PORT = 11000;
	static int CLIENT_PORT = 12000;
	
	@FXML
    private Pane loginPanel;
	@FXML
	private VBox mainPane;
	@FXML
	private Button submitBtn;
	@FXML
	private TextField messageBox;
	@FXML
	private Label errorMsg;
	@FXML
	private TextField userName;
	@FXML 
	private PasswordField password;
	
	
	
	//Two lists of main messenger 
	
	@FXML
	private ListView<User> userList;
	
	@FXML
	private ListView<Message> chatMessages;
	
	static ObservableList<User> items =FXCollections.observableArrayList ();
	static String userID = "";
    static BigInteger myNonce;
    static HashMap<String,String> myIPList = new HashMap<String,String>();
    static HashMap<String,BigInteger> myKeyList = new HashMap<String,BigInteger>();
    
	//ObservableList<String> messeges =FXCollections.observableArrayList ("aa", "bb", "cc", "dd");
	
	static HashMap<String,ObservableList<Message>> userMsgs = new  HashMap<String,ObservableList<Message>> ();
	
	
	private ServerSocket serverSocket;
	
	@FXML
	private AnchorPane anchorPane1;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// load a properties file
		if(SERVER_IP==null)
		{
			try
			{
				Properties prop = new Properties();
				prop.load(Controller.class.getResourceAsStream("/ServerConfig.properties"));
				
				// get the property value and print it out
				SERVER_IP = prop.getProperty("SERVER_IP");
				SERVER_PORT = Integer.parseInt(prop.getProperty("SERVER_PORT"));
				CLIENT_PORT = Integer.parseInt(prop.getProperty("CLIENT_PORT"));
				
			}
			catch(Exception e)
			{
				//Problem in Loading Property File 
				e.printStackTrace();
			}
		}
		
		if(loginPanel!=null)
		{
			fadeTransition(loginPanel);
			userName.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) {
			        if (keyEvent.getCode() == KeyCode.ENTER)  {
			        	password.requestFocus();
			        }
			    }
			});
			
			password.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) {
			        if (keyEvent.getCode() == KeyCode.ENTER)  {
			        	authenticateClient();
			        }
			    }
			});
			
		}
		if(mainPane!=null)
		{
			//Initialize All Items IN Messenger Panel
			
			fadeTransition(mainPane);
			
			
			userList.setItems(items);
			userList.setCellFactory((ListView<User> l) -> new UserListCell());
			userList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			        @Override
			        public void handle(MouseEvent event) {
			            System.out.println("clicked on " + userList.getSelectionModel().getSelectedItem().toString());
			            String userName = userList.getSelectionModel().getSelectedItem().getUserName();
			            if(userMsgs.get(userName)!=null && userMsgs.get(userName).size()>0)
			            {
			            	chatMessages.setItems(userMsgs.get(userName));
			            }
			            else
			            {
			            	//Go for hit to Server For this selected User 
			            	Socket client = null;
			            	try
			            	{
			            		
			            		 client = new Socket(SERVER_IP, SERVER_PORT);
			        	         
			        	         byte[] aesData;
			        	         byte[] IV;
			        	         byte[] key;
			        	         
			                  	 OutputStream outToServer = client.getOutputStream();
			    	        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
			    	             
			    	             
			    	        	 IV = AES.getIVSpecs();
			    	        	 aesData = AES.encrypyUseingAES(myNonce, IV, 
			    	        			 "GetSessionKey~"+userID+"~"+userName+"~"+String.valueOf(System.currentTimeMillis()));
			    	        	 

			    	        	 key =  EncryptUtil.encryptData(String.valueOf(myNonce).getBytes());
			    	        	 MessegeSendBean encryptedMsg = new MessegeSendBean(key, aesData, IV);
			    	        	 out.writeObject(encryptedMsg);
			    	        	 
			    	        	 
			    	        	 
			    	        	 InputStream inFromServer = client.getInputStream();
			    			     ObjectInputStream in = new ObjectInputStream(inFromServer);
			    			     MessegeSendBean replyBean = (MessegeSendBean)in.readObject();
			    			     
			    			     
		    			     	byte[] returnIV = replyBean.getIV();
		    		            String decryptedData = AES.decryptUseingAES(myNonce, returnIV, replyBean.getAESData());
		    		            
		    		            
		    		            System.out.println("Decrypted At Client For Key Request: "+decryptedData);
		    		            
		    		            if(decryptedData.startsWith("Available~"))
		    		            {
		    		            	String[] userData = decryptedData.split("~");
		    		            	myIPList.put(userName, userData[2]);
		    		            	myKeyList.put(userName, new BigInteger(userData[3]));
		    		            	
		    		            	//Send Ticket to Client 
		    		            	boolean success = sendDataToClient(replyBean,userName);
		    		            	
		    		            	if(success)
		    		            	{
		    		            		userMsgs.get(userName).add(new Message("Messages in Conversation are End to End Encrypted",true));
		    		            		chatMessages.setItems(userMsgs.get(userName));
		    		            	}
		    		            	else
		    		            	{
		    		            		//Problem in Connecting 
		    		            		userNotAvailable(userName);
		    		            	}
		    		            }
		    		            else
		    		            {
		    		            	// Client is Not available 
		    		            	userNotAvailable(userName);
		    		            }
			    		            
			    		            
			            	}
			            	catch(Exception e)
			            	{
			            		e.printStackTrace();
			            	}
			            }
			            
			        }

					private boolean sendDataToClient(MessegeSendBean replyBean,String otehrClientID) 
					{
						Socket otherClient = null;
						try
						{
							otherClient = new Socket(myIPList.get(otehrClientID), CLIENT_PORT);
							  
		                  	 OutputStream outToServer = otherClient.getOutputStream();
		    	        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
		    	        	 
		    	        	 out.writeObject(replyBean);
		    	        	 
							otherClient.close();
							return true;
						}
						catch(Exception e)
						{
							return false;
						}
						
					}

					private void userNotAvailable(String userName) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Clent Not Available Warning ");
						alert.setHeaderText("Requested Client is Not Online");
						alert.setContentText("User "+userName+" is not available in messenger application. Please Try again Later.");

						alert.showAndWait();
					}
			    });
			
			//chatMessages.setItems(messeges);
			chatMessages.setCellFactory((ListView<Message> l) -> new MessegeListCell());
			
			
			messageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) {
			        if (keyEvent.getCode() == KeyCode.ENTER)  {
			            String text = messageBox.getText();

			            if(userList.getSelectionModel().getSelectedItem()==null)
			            {
			            	selectUserAlert();
			            	return;
			            }
			            String userName = userList.getSelectionModel().getSelectedItem().getUserName();
			            
			            // do your thing...
			            userMsgs.get(userName).add(new Message(text,true));
			            messageBox.setText("");
			            
			            Socket otherClient = null;
						try
						{
							otherClient = new Socket(myIPList.get(userName), CLIENT_PORT);
							
		                  	 OutputStream outToServer = otherClient.getOutputStream();
		    	        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
		    	        	 
		    	        	 
		    	        	 byte[] IV = AES.getIVSpecs();
		    	        	 byte[] aesData = AES.encrypyUseingAES(myKeyList.get(userName), IV, text);
		    	        	 MessegeSendBean encryptedMsg = new MessegeSendBean(null, aesData, IV);
		    	        	 encryptedMsg.setTicket(null);
		    	        	 encryptedMsg.setFromUser(userID);
		    	        	 
		    	        	 out.writeObject(encryptedMsg);
		    	        	 
							otherClient.close();
						}
						catch(Exception e)
						{
							
						}
						 
			        }
			    }
			});
			
			try {
				serverSocket = new ServerSocket(CLIENT_PORT);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Timer task = new Timer();
			task.schedule(
				    new TimerTask() {

				        @Override
				        public void run() {
				        	while(true)
				        	{
				        		try
				        		{
				        			Socket server = serverSocket.accept();
						            System.out.println("Messege Received From :" + server.getRemoteSocketAddress());
						            
						            try
						            {
						            	
						            	  InputStream inFromServer = server.getInputStream();
									      ObjectInputStream in = new ObjectInputStream(inFromServer);
									         
								          MessegeSendBean msgBean = (MessegeSendBean)in.readObject();
						            	
								          
								          if(msgBean.getTicket()!=null)
								          {
								        	  //This is First Message For verification
								        	  
								        	  String decryptedMessage = AES.decryptUseingAES(myNonce, msgBean.getTicketIV(), msgBean.getTicket());
								        	  String msgArray[] = decryptedMessage.split("~");
								        	  
								        	  BigInteger commonKey = new BigInteger(msgArray[0]);
								        	  myKeyList.put(msgArray[1], commonKey);
								          }
								          else
								          {
								        	  //This is After Message
								        	  String decryptedMessage = AES.decryptUseingAES(myKeyList.get(msgBean.getFromUser()), msgBean.getIV(), msgBean.getAESData());
								        	  Platform.runLater(new Runnable() {
								            	    public void run() {
								            	    	userMsgs.get(msgBean.getFromUser()).add(new Message(decryptedMessage,false));
								            	    }
								            	});
								          }
						            }
						            catch(Exception  ee)
						            {
						            	ee.printStackTrace();
						            }
						            
						            server.close();
				        		}
				        		catch(Exception e )
				        		{
				        			e.printStackTrace();
				        		}
				        		 	
				        	}
				        }
				    }, 0, 3000);
			
			
			
		}
	}
	
	@FXML
	public void authenticateClient()
	{
		Socket client = null;
		try {
			
			 client = new Socket(SERVER_IP, SERVER_PORT);
	         
	         String user = userName.getText().trim();
	         String passWD = password.getText().trim();
	         
	         if(user.equals("") || passWD.equals(""))
	         {
	        	 errorMsg.setVisible(true);
	        	 return;
	         }
	         
	         BigInteger nonce;
	         byte[] aesData;
	         byte[] IV;
	         byte[] key;
	         try
	         {
	        	 
	        	 OutputStream outToServer = client.getOutputStream();
	        	 ObjectOutputStream out = new ObjectOutputStream(outToServer);
	             
	             
	 	         //DataOutputStream out = new DataOutputStream(outToServer);
	 	         
	        	 passWD = EncryptUtil.makeSHA512Hash(passWD);
	        	 nonce = EncryptUtil.generateNonce();
	        	 myNonce = nonce;
	        	 IV = AES.getIVSpecs();
	        	 aesData = AES.encrypyUseingAES(nonce, IV, EncryptUtil.mergeAESDetails(user, passWD, 
	        			 									String.valueOf(System.currentTimeMillis())));
	        	 

	        	 key =  EncryptUtil.encryptData(String.valueOf(nonce).getBytes());
	        	 
	        	 MessegeSendBean encryptedMsg = new MessegeSendBean(key, aesData, IV);
	        	 
	        	 System.out.println("User Name : "+user);
	        	 System.out.println("Encrypted SHA Password : "+passWD);
	        	 System.out.println("Nonce : "+nonce);
	        	 
	        	 out.writeObject(encryptedMsg);
	        	
	        	 
	        	 InputStream inFromServer = client.getInputStream();
			     ObjectInputStream in = new ObjectInputStream(inFromServer);
			     MessegeSendBean replyBean = (MessegeSendBean)in.readObject();
			     
			     
			     	byte[] returnIV = replyBean.getIV();
		            String decryptedData = AES.decryptUseingAES(nonce, returnIV, replyBean.getAESData());
		            
		            
		            System.out.println("Decrypted At Client : "+decryptedData);
		            if(decryptedData.startsWith("Authenticated"))
		            {
		            	Main.changeScene("messenger.fxml");
		            	setUsersPanel(decryptedData);
		            	userID = user;
		            }
		            else
		            {
		            	errorMsg.setVisible(true);
			        	return;
		            }
	        	
	         }
	         catch(Exception innerExp )
	         {
	        	 //Problem in Encrypting Data
	        	 innerExp.printStackTrace();
	         }
	         
	         //If Authenticated then change screen to Next Screen else Show Error Message
	         
	         
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void setUsersPanel(String decryptedData)
	{
		String[] userNames = decryptedData.split("~");
		for (int i = 1; i < (userNames.length-1); i++) 
		{
			System.out.println("dv : "+userNames[i]);
			items.add(new User(userNames[i]));
			userMsgs.put(userNames[i],FXCollections.observableArrayList());
		}
	}
	
	
	 private void fadeTransition(Node e){
	        FadeTransition x=new FadeTransition(new Duration(1000),e);
	        x.setFromValue(0);
	        x.setToValue(100);
	        x.setCycleCount(1);
	        x.setInterpolator(Interpolator.LINEAR);
	        x.play();
	    }

	 private void selectUserAlert() {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Select User");
			alert.setHeaderText("Plese Select User First in Left Panel");
			alert.setContentText("To Send Messege, Please Select User From Left Panel of Window");

			alert.showAndWait();
		}
	 
}
