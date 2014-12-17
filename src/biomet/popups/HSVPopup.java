package biomet.popups;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import biomet.managers.EditorManager;
import biomet.panels.ImagePanel;
import biomet.panels.RootPanel;

public class HSVPopup extends BasePopup
{
private EditorManager eManager = EditorManager.getInstance();
final private ImagePanel imagePanel = RootPanel.getImagePanel();
    
	public HSVPopup()
    {
        super(new GridPane(), "HSV");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(600);
        contentPane.getStyleClass().add("HSV");
        
        Label titleLabel = this.getTitleLabel();
        titleLabel.setPrefSize(380, 25);

        //contentPane.add(titleLabel, 0, 0);
        
        this.getContent().add(contentPane);

        WritableImage imgH = eManager.decompositionHSVImage("H");
        WritableImage imgS = eManager.decompositionHSVImage("S");
        WritableImage imgV = eManager.decompositionHSVImage("V");
        
        ImageView iv1 = new ImageView(imgH);
        ImageView iv2 = new ImageView(imgS);
        ImageView iv3 = new ImageView(imgV);
        
               
        int size = 200;
        
        iv1.setFitWidth(size);     
        iv1.setFitHeight(size);
                
        iv2.setFitWidth(size);
        iv2.setFitHeight(size);
           
        iv3.setFitWidth(size);
        iv3.setFitHeight(size);     
           
        VBox vbox = new VBox(5); 
        Label labelImage = new Label();
        labelImage.setPrefSize(100, 25);
        labelImage.setText("H");
        vbox.getChildren().add(iv1);
        vbox.getChildren().add(labelImage);
        labelImage = new Label();
        labelImage.setPrefSize(100, 25);
        labelImage.setText("S");
        vbox.getChildren().add(iv2);
        vbox.getChildren().add(labelImage);
        vbox.getChildren().add(iv3);
        labelImage = new Label();
        labelImage.setPrefSize(100, 25);
        labelImage.setText("v");
        vbox.getChildren().add(labelImage);

        contentPane.add(vbox, 0, 1);
        
        /*
        eManager.updateImage(imgH);
        eManager.updateImage(imgS);
        eManager.updateImage(imgV);
        */
    }

}
