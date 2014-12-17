package biomet.popups;

import biomet.managers.EditorManager;
import biomet.models.FilterMask;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FilterMedianPopup extends BasePopup 
{

	private EditorManager eManager = EditorManager.getInstance();
    
	public FilterMedianPopup()
    {
        super(new GridPane(), "Filtr Medianowy");
        
        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(400);
        
        contentPane.getStyleClass().add("Filtr Medianowy");
        final FilterMask filterMask = new FilterMask(0);
        Label titleLabel = this.getTitleLabel();
        Label maskLabel = new Label("Wartosc maski:");
        TextField maskTextField = new TextField();
        Button maskOkButton = new Button("ok");
        Button maskCancelButton = new Button("anuluj");
        maskOkButton.setPrefSize(100, 25);
        maskCancelButton.setPrefSize(100, 25);
        titleLabel.setPrefSize(380, 25);
        contentPane.add(titleLabel, 0, 0, 2, 1);
        contentPane.add(maskLabel, 0, 1);
        contentPane.add(maskTextField, 1, 1);
        contentPane.add(maskOkButton, 0, 2);
        contentPane.add(maskCancelButton, 1, 2);
                       
        
        maskTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
            	
             	int valMask = Integer.parseInt(newValue);
             	filterMask.setMaskSize(valMask);    	
            }
        }); 

        maskOkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
                eManager.filterMedian(filterMask);                             
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
