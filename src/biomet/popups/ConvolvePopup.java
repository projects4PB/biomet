package biomet.popups;

import java.awt.Insets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import biomet.managers.EditorManager;
import biomet.models.FilterMask;
import biomet.models.Point;

public class ConvolvePopup extends BasePopup 
{
private EditorManager eManager = EditorManager.getInstance();
    
	public ConvolvePopup()
    {
        super(new GridPane(), "Operacja splotu");
        
        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(500);
        
        contentPane.getStyleClass().add("Operacja splotu");
        final FilterMask filterMask = new FilterMask(3);
        final Point point = new Point();
        Label titleLabel = this.getTitleLabel();
        Label maskLabel = new Label("Wartosc maski:");        
        
        Button maskOkButton = new Button("ok");
        Button maskCancelButton = new Button("anuluj");
        
        maskOkButton.setPrefSize(100, 25);
        maskCancelButton.setPrefSize(100, 25);
        titleLabel.setPrefSize(380, 25);
        
        contentPane.add(titleLabel, 0, 0, 2, 1);

        FlowPane flow = new FlowPane();
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(170);
        final TextField maskValues[] [] = new TextField[3] [3];
       
        RadioButton rb1 = new RadioButton("dolnoprzepustowy");
        RadioButton rb2 = new RadioButton("górnoprzepustowy");
        RadioButton rb3 = new RadioButton("Sopela");
        
        VBox vbox = new VBox();
        
        vbox.getChildren().add(rb1);
        vbox.getChildren().add(rb2);
        vbox.getChildren().add(rb3);
        
        vbox.setSpacing(2);
        
        final ToggleGroup group = new ToggleGroup();
        
        rb1.setToggleGroup(group);
        rb1.setUserData("D");
        
        rb2.setToggleGroup(group);
        rb2.setUserData("G");
        
        rb3.setToggleGroup(group);
        rb3.setUserData("S");

     
        for (int i=0; i<3; i++) 
        {
        	for (int j=0; j<3; j++) 
            {
        		maskValues[i][j] = new TextField("0");
        		maskValues[i][j].setPrefSize(50, 50);
        		flow.getChildren().add(maskValues[i][j]);
            }
        }
        
        contentPane.add(flow, 0, 1);
        contentPane.add(vbox, 0, 3);
        contentPane.add(maskOkButton, 0, 4);
        contentPane.add(maskCancelButton, 1, 4);
                
        for (int k=0; k<3; k++) 
        {
        	point.setX(k);
        	for (int w=0; w<3; w++) 
            {
        		point.setY(w);
        		maskValues[k][w].textProperty().addListener(new ChangeListener<String>() {
        			@Override
        			public void changed(ObservableValue<? extends String> observable,
        					String oldValue, String newValue) {
        				if(newValue.equals("-") == false 
        						&& newValue.equals("") == false)
        				{
            				float valMask = Float.parseFloat(newValue);
            				filterMask.setMaskValue(point.getX(), 
            						point.getY(), valMask);
        				}
        			}
        		}); 
            }
        }
        
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                Toggle old_toggle, Toggle new_toggle) {
            		if (group.getSelectedToggle() != null) 
            		{
            			String chooseFilter = group.getSelectedToggle().getUserData().toString();
            			String middleValue = "";
            			String cornerValue = "";
            			
            			if(chooseFilter.equals("D"))
            			{
            				middleValue = "2.0f";
            				cornerValue = "1.0f";
            			}
            			
            			else if(chooseFilter.equals("G"))
            			{
            				middleValue = "9.0f";
            				cornerValue = "-1.0f";
            			}
            			
        		        for (int i=0; i<3; i++) 
        		        {
        		        	for (int j=0; j<3; j++) 
        		            {
        		        		if(i == 1 && j == 1)
        		        		{
        		        			maskValues[i][j].setText(middleValue);
        		        		}
        		        		
        		        		else
        		        		{
        		        			maskValues[i][j].setText(cornerValue);
        		        		}
        		        		
        		        		String textFieldVal = maskValues[i][j].getText();
        		        		
                				float value = Float.valueOf(textFieldVal);
            				
                				filterMask.setMaskValue(i, j, value);
        		            }
        		        }
            		}
            	}
        });
        
        maskOkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
                eManager.convolveImage(filterMask);                             
            }
        });
        
        maskCancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
       
        this.getContent().add(contentPane);
    }
}
