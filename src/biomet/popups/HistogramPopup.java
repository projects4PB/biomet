package biomet.popups;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import biomet.managers.EditorManager;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class HistogramPopup extends BasePopup
{
	private EditorManager eManager = EditorManager.getInstance();
    
	public HistogramPopup()
    {
        super(new GridPane(), "Histogram");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(800);
        
        contentPane.getStyleClass().add("histogram");
        
        Label titleLabel = this.getTitleLabel();
        titleLabel.setPrefSize(780, 25);

		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        final AreaChart<Number, Number> barChart = 
            new AreaChart<Number, Number>(xAxis, yAxis);

        xAxis.setLabel("Wartości pikseli");       
        yAxis.setLabel("Częstotliwość");
 
        XYChart.Series series = new XYChart.Series();
		series.setName("Ilość pikseli danej wartości");

		final HashMap<Integer, Integer> histogramValues =
			eManager.computeHistogramValues();

		List<Integer> histogramKeys = new ArrayList(histogramValues.keySet());

		Collections.sort(histogramKeys);

		for(Integer key : histogramKeys)
		{
			int valueCount = histogramValues.get(key);

			series.getData().add(new XYChart.Data(key, valueCount));
		}
		barChart.getData().add(series);

		Button saveHistogramButton = new Button("Zapisz do pliku");
		saveHistogramButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				FileChooser fileChooser = new FileChooser();
  
				FileChooser.ExtensionFilter extFilter =
					new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
					  
				File file = fileChooser.showSaveDialog(getScene().getWindow());

				eManager.saveHistogram(file);
			}
		});

		Button strechHistogramButton = new Button("Rozciągnięcie histogramu");
		strechHistogramButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				eManager.strechImageHistogram(histogramValues.keySet());
			}
		});

		Button equalizeHistogramButton = new Button("Wyrównanie histogramu");
		equalizeHistogramButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				eManager.equalizeImageHistogram(histogramValues);
			}
		});

        contentPane.add(titleLabel, 0, 0, 3, 1);
        contentPane.add(barChart, 0, 1, 3, 1);
        contentPane.add(saveHistogramButton, 0, 2, 1, 1);
        contentPane.add(strechHistogramButton, 1, 2, 1, 1);
        contentPane.add(equalizeHistogramButton, 2, 2, 1, 1);

        this.getContent().add(contentPane);
    }
}
