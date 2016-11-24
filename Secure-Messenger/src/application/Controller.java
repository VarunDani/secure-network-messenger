package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.ClientInfoStatus;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import util.DeccryptUtil;

public class Controller implements Initializable{

	
	@FXML
    private Pane loginPanel;
	@FXML
	private VBox mainPane;
	@FXML
	private Button submitBtn;
	@FXML
	private TextField messageBox;
	
	//Two lists of main messenger 
	
	@FXML
	private ListView<String> userList;
	
	@FXML
	private ListView<String> chatMessages;
	
	ObservableList<String> items =FXCollections.observableArrayList ("User1", "User2", "User3", "User4");
	ObservableList<String> messeges =FXCollections.observableArrayList ("aa", "bb", "cc", "dd");
	
	
	private ServerSocket serverSocket;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		//System.out.println("dvdv");
		
		if(loginPanel!=null)
		{
			fadeTransition(loginPanel);
		}
		if(mainPane!=null)
		{
			//Initialize All Items IN Messenger Panel
			
			fadeTransition(mainPane);
			
			userList.setItems(items);
			chatMessages.setItems(messeges);
			
			messageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) {
			        if (keyEvent.getCode() == KeyCode.ENTER)  {
			            String text = messageBox.getText();

			            // do your thing...
			            messeges.add(text);
			            
			            // clear text
			            messageBox.setText("");
			            
			            Socket client = null;
			            try
			            {
			            	client = new Socket("192.168.0.17", 12000);
					         
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
						            	
						            	items.add(readFile);
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
		try {
			
			 Socket client = new Socket("192.168.0.17", 11000);
	         
	         System.out.println("Just connected to " + client.getRemoteSocketAddress());
	         OutputStream outToServer = client.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);
	         
	         out.writeUTF("Hello from " + client.getLocalSocketAddress());
	         InputStream inFromServer = client.getInputStream();
	         DataInputStream in = new DataInputStream(inFromServer);
	         
	         System.out.println("Server says " + in.readUTF());
	         client.close();
	         
	         //If Authenticated then change screen to Next Screen else Show Error Message
			Main.changeScene("messenger.fxml");
			
		} catch (IOException e) {
			e.printStackTrace();
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
