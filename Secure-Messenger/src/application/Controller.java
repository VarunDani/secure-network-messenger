package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.MessegeSendBean;
import util.AES;
import util.DeccryptUtil;
import util.EncryptUtil;

public class Controller implements Initializable{

	
	static  String SERVER_IP = null;
	static  int SERVER_PORT = 11000;
	
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
	
	ObservableList<User> items =FXCollections.observableArrayList ();

    
	//ObservableList<String> messeges =FXCollections.observableArrayList ("aa", "bb", "cc", "dd");
	
	ObservableList<Message> messeges = FXCollections.observableArrayList();
	
	private ServerSocket serverSocket;
	
	@FXML
	private AnchorPane anchorPane1;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		//System.out.println("dvdv");
		
		
		//Initialize Server IP

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
		}
		if(mainPane!=null)
		{
			//Initialize All Items IN Messenger Panel
			
			fadeTransition(mainPane);
			
			
			userList.setItems(items);
			
			items.add(new User("Hello"));
			
			userList.setCellFactory((ListView<User> l) -> new UserListCell());
			
			
			chatMessages.setItems(messeges);
			chatMessages.setCellFactory((ListView<Message> l) -> new MessegeListCell());
			
			
			messageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) {
			        if (keyEvent.getCode() == KeyCode.ENTER)  {
			            String text = messageBox.getText();

			            // do your thing...
			            //messeges.add(text);
			            messeges.add(new Message(text,true));
			            // clear text
			            messageBox.setText("");
			            
			            Socket client = null;
			            try
			            {
			            	client = new Socket("localhost", 12000);
					         
					         System.out.println("Just connected to " + client.getRemoteSocketAddress());
					         OutputStream outToServer = client.getOutputStream();
					         DataOutputStream out = new DataOutputStream(outToServer);
					         out.writeUTF(text);
					         //client.close();
			            }
			            catch(Exception e)
			            {
			            	
			            }
			            finally
			            {
			            	try {
								client.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
			            }
						 
			        }
			    }
			});
			
			try {
				serverSocket = new ServerSocket(12000);
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
						            
				        			
						            System.out.println("Just connected to " + server.getRemoteSocketAddress());
						            DataInputStream in = new DataInputStream(server.getInputStream());
						            
						            DeccryptUtil d = new DeccryptUtil();
						            
						            try
						            {
						            	String readFile = in.readUTF();
						            	System.out.println("message from other client : "+readFile);
						            	d.decryptChatMessage(readFile);
						            	
						            	Platform.runLater(new Runnable() {
						            	    public void run() {
						            	    	messeges.add(new Message(readFile,true));
						            	    }
						            	});
						            	
						            	
						            }
						            catch(EOFException  ee)
						            {
						            	ee.printStackTrace();
						            }
						            
						            //Thread.sleep(5000);
						            
						            //Do not write for now It will be done by utility 
						            //DataOutputStream out = new DataOutputStream(server.getOutputStream());
						            //out.writeUTF("");
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
	
	
	 private void fadeTransition(Node e){
	        FadeTransition x=new FadeTransition(new Duration(1000),e);
	        x.setFromValue(0);
	        x.setToValue(100);
	        x.setCycleCount(1);
	        x.setInterpolator(Interpolator.LINEAR);
	        x.play();
	    }

}
