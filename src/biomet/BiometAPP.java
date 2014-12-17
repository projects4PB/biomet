package biomet;
	
import biomet.panels.RootPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class BiometAPP extends Application
{
	private static RootPanel rootPanel = new RootPanel();

	@Override
	public void start(Stage primaryStage)
	{
		Scene scene = new Scene(this.rootPanel, 800, 600);

		primaryStage.setTitle("Biomet APP");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static RootPanel getRootPanel() 
	{
		return rootPanel;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
