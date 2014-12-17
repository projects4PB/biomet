package biomet.panels;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import biomet.managers.EditorManager;
import biomet.popups.BinarizationPopup;
import biomet.popups.ConvolvePopup;
import biomet.popups.FilterMedianPopup;
import biomet.popups.FindMinutiaePopup;
import biomet.popups.HSVPopup;
import biomet.popups.HistogramPopup;

/**
 *
 * @author Adrian Olszewski
 */
public class RootPanel extends GridPane
{
    private final static ImagePanel imagePanel = new ImagePanel();

    private final EditorManager eManager = EditorManager.getInstance();

    
    public RootPanel()
    {
        MenuBar menuBar = this.createMenuBar();

        this.add(menuBar, 0, 0);

		this.add(imagePanel, 0, 1);
    }

	public static ImagePanel getImagePanel()
	{
		return imagePanel;
	}
    
    private MenuBar createMenuBar()
    {
        MenuBar menuBar = new MenuBar();
        
        menuBar.setPrefSize(800, 25);
        
        Menu fileMenu = new Menu("Plik");
        
        MenuItem openImage = new MenuItem("Otwórz obraz");
        openImage.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(
                        getScene().getWindow()
                );
                if(file != null)
                {
					eManager.loadImage(file);
                }               
            }
        });
        
        MenuItem saveImage = new MenuItem("Zapisz obraz");
        saveImage.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Image");
                    File file = fileChooser.showSaveDialog(getScene().getWindow());
                    if (file != null) 
                    {
                    	eManager.saveImage(file);
                    }
            }
        });

        Menu editMenu = new Menu("Edycja");

        MenuItem histogram = new MenuItem("Histogram");
        histogram.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				HistogramPopup popup = new HistogramPopup();

				popup.show(getScene().getWindow());
            }
        });
        
        MenuItem exit = new MenuItem("Zakończ");
        exit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                System.exit(0);
            }
        });
        
        Menu filtersMenu = new Menu("Filtry");
        
        MenuItem binarizationFilter = new MenuItem("Binaryzacja");
        binarizationFilter.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				BinarizationPopup popup = new BinarizationPopup();

				popup.show(getScene().getWindow());
            }
        });
        
        MenuItem medianFilter = new MenuItem("Filtr Medianowy");
        medianFilter.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				FilterMedianPopup popup = new FilterMedianPopup();

				popup.show(getScene().getWindow());
            }
        });
        
        MenuItem splotFilter = new MenuItem("Filtr splotowy");
        splotFilter.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				ConvolvePopup popup = new ConvolvePopup();

				popup.show(getScene().getWindow());
            }
        });

        MenuItem findMinutiae = new MenuItem("Znajdowanie minucji");
        findMinutiae.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				eManager.findMinutiae();
            }
        });
        
        MenuItem HSV = new MenuItem("HSV");
        HSV.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				HSVPopup popup = new HSVPopup();

				popup.show(getScene().getWindow());
            }
        });
        
        MenuItem thinningFilter = new MenuItem("Scienianie");
        thinningFilter.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
            	eManager.setThinningImage();
            }
        });

        fileMenu.getItems().addAll(
				openImage,
				saveImage,
				exit
			);
		editMenu.getItems().addAll(
				histogram
			);
        filtersMenu.getItems().addAll(
				binarizationFilter,
				medianFilter,
				findMinutiae,
				splotFilter,
				HSV,
				thinningFilter
			);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, filtersMenu);
        
        return menuBar;
    }
}
