package application;
	
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class MainBackup extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			 Button btn = new Button();
		        btn.setText("Say 'Hello World'");
		        btn.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						System.exit(0);
						
					}
				});
		        
		        StackPane root = new StackPane();
		        root.getChildren().add(btn);
			Scene scene = new Scene(root,400,400);
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Timer task = new Timer();
			task.schedule(
				    new TimerTask() {

				        @Override
				        public void run() {
				        	//btn.setText("asdad");
				        	while(true)
				            System.out.println("ping");
				        }
				    }, 0, 3000);
			
		/*	Timer timer = new Timer();
			TimerTask task = new TimerTask()
			{
			        public void run()
			        {
			            //The task you want to do 
			        	while(true)
			        	{
			        		System.out.println("z");
			        	}
			        	System.out.println("aa");
			        }

			};
			
			timer.schedule(task,5000l);
			
			
			
			primaryStage.setScene(scene);
			primaryStage.show();
	            System.out.println("asas");
	            //task.cancel();
*/	            
			primaryStage.setScene(scene);
			primaryStage.show();
			task.cancel();
			task.purge();
			
			 System.out.println("asas");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
