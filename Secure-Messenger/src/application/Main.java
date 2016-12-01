package application;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This is Main Class that contains main method for launching Java FX Application
 * 
 * 
 * @version 1.0
 * @since v1.0
 * @author Varun Dani
 *
 */
public class Main extends Application {
	
	static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			 stage=primaryStage;
			 
			 
			//Loading Root Layout File 
			Pane root =  FXMLLoader.load(getClass().getResource("login.fxml")); 
			
			//Making  New FX Scene and Loading Current CSS File 
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//ready Primary Stage File 
			primaryStage.setTitle("Secure Messenger");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		/*	
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
						            d.decryptChatMessage(in.readUTF());
						            System.out.println("message from other client : "+in.readUTF());
						            
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
				    }, 0, 3000);*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	  public static void changeScene (String sceneName) throws IOException{
	        Parent parent=FXMLLoader.load(Main.class.getResource(sceneName));
	        stage.setScene(new Scene(parent));

	    }
	  
	public static void main(String[] args) {
		
		//Initializing Main Server Socket of Each Client serverSocket = new ServerSocket(11000);
		launch(args);
	}
	
	@Override
	public void stop(){
	    System.out.println("Stage is Closing End All Application Threads");
	    System.exit(0);
	}
	
}