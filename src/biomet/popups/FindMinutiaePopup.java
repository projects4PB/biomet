package biomet.popups;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

import biomet.managers.EditorManager;


/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class FindMinutiaePopup extends BasePopup
{
	final private EditorManager eManager = EditorManager.getInstance();
    
	public FindMinutiaePopup()
    {
        super(new GridPane(), "BINARYZACJA");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(400);
        
        contentPane.getStyleClass().add("binarization");
        
        Label titleLabel = this.getTitleLabel();
        titleLabel.setPrefSize(380, 25);
        
		Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(255);
        slider.setBlockIncrement(1);
		slider.setValue(125);

        final Label sliderLabel = new Label(
                Double.toString(slider.getValue()));
        sliderLabel.setPrefSize(75, 25);
        sliderLabel.setText("125");

		Button cancelButton = new Button("anuluj");
        cancelButton.setOnAction(new EventHandler<ActionEvent>()
		{
            @Override
            public void handle(ActionEvent event)
			{
                eManager.restoreWorkingImage();                             

				hide();
            }
        });

		Button saveButton = new Button("zastosuj");
        saveButton.setOnAction(new EventHandler<ActionEvent>()
		{
            @Override
            public void handle(ActionEvent event)
			{
                eManager.updateWorkingImage();                             

				hide();
            }
        });
        
        contentPane.add(titleLabel, 0, 0, 3, 1);
        contentPane.add(slider, 0, 1, 3, 1);
        contentPane.add(sliderLabel, 0, 2, 1, 1);
        contentPane.add(cancelButton, 1, 2, 1, 1);
        contentPane.add(saveButton, 2, 2, 1, 1);

		eManager.binarizeImage(125);
        
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            		eManager.binarizeImage(new_val.intValue());
                    sliderLabel.setText(
						String.format("%d", Math.round(new_val.intValue()))
					);
            }
        });     
        this.getContent().add(contentPane);
    }
}
