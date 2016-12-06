package application;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

/**
 * Customized List Cells for User Panel 
 *
 */
public class UserListCell extends ListCell<User>{

	static Font fontAwesome;
	static{
		fontAwesome = Font.loadFont(MessegeListCell.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 64);
	}
	
	
	@Override 
	  public void updateItem(User usr, boolean empty) { 
	    super.updateItem(usr, empty); 
	    if (empty) { 
	      setText(null); 
	      setGraphic(null); 
	    } else { 
	      //setText(msg.getMessage() +" : "+ msg.getRecepient().toString()); 
	      setText(usr.getUserName()); 
	      Label label = new Label(Icons.USER_ICON); 
	      label.setFont(fontAwesome);
	      label.setStyle("-fx-font-family: 'FontAwesome';-fx-cell-size: 100px;-fx-font-size: 50px");
	      //setPrefHeight(32);
	      setGraphic(label); 
	    }
	  } 
	
}
