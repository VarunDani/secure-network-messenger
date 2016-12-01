package application;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

public class MessegeListCell extends ListCell<Message> {  
	  
	static Font fontAwesome;
	static{
		fontAwesome = Font.loadFont(MessegeListCell.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 12);
	}
	
	
	@Override 
	  public void updateItem(Message msg, boolean empty) { 
	    super.updateItem(msg, empty); 
	    if (empty) { 
	      setText(null); 
	      setGraphic(null); 
	    } else { 
	      //setText(msg.getMessage() +" : "+ msg.getRecepient().toString()); 
	    	setText(msg.getMessage()); 
	      if(msg.getRecepient())
	      {
	    	  setStyle("-fx-alignment: CENTER-RIGHT;");	  
	      }
	      else
	      {
	    	  setStyle("-fx-alignment: CENTER-LEFT;");	
	      }
	      setGraphic(null); 
	    }
	  } 
	}