package biomet.popups;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Popup;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
abstract public class BasePopup extends Popup
{
    private final Parent rootNode;
    
    private final Label titleLabel = new Label();
    
    protected static final String POPUPS_STYLESHEET
            = "/biomet/popups/css/popups.css";
    
    public BasePopup(Parent rootNode, String title)
    {
        this.rootNode = rootNode;
        
        this.rootNode.getStyleClass().add("popup");
        
        this.rootNode.getStylesheets().add(
                POPUPS_STYLESHEET
        );
        this.titleLabel.setText(title);
        
        this.titleLabel.getStyleClass().add(
                "title-label"
        );
    }
    
    public Label getTitleLabel()
    {
        return this.titleLabel;
    }
    
    public Parent getRootNode()
    {
        return this.rootNode;
    }
}
