package si.comptus.jrelex;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;

public class MainController implements Initializable {
	
	@FXML
	private TabPane tabPaneTopDisplay;
	@FXML
	private BorderPane toolBar;
	
	
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * Initializes the controller class.
	 */
	public void initialize(final URL url, final ResourceBundle rb) {
		
            this.setStyles();
				                
	}
        
        /**
        *  Opens tab for exploring the database.
        */
        public void exploreDatabase(){
            try {
                String fxmlFile = "/fxml/database_explorer.fxml";
                //log.debug("Loading FXML for main view from: {}", fxmlFile);
                FXMLLoader loader = new FXMLLoader();

                SplitPane rootNode = (SplitPane) loader.load(getClass().getResourceAsStream(fxmlFile));

                String text = "Explore database";
                Tab tab = Common.getInstance().tabExists(text, tabPaneTopDisplay); 
                if(tab == null){
                        tab = new Tab();
                        tab.setText(text);
                        tabPaneTopDisplay.getTabs().add(tab);
                }

                tab.setContent(rootNode);
                tabPaneTopDisplay.getSelectionModel().select(tab);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
        }
        
        /**
        *  Opens form for changing settings or adding new connection to database
        */
        public void manageDatabase(){
            try {
                String fxmlFile = "/fxml/database_meta_data.fxml";
                //log.debug("Loading FXML for main view from: {}", fxmlFile);
                FXMLLoader loader = new FXMLLoader();

                HBox rootNode;

                rootNode = (HBox) loader.load(getClass().getResourceAsStream(fxmlFile));

                String text = "Manage database";
                Tab tab = Common.getInstance().tabExists(text, tabPaneTopDisplay); 
                if(tab == null){
                    tab = new Tab();
                    tab.setText(text);
                    tabPaneTopDisplay.getTabs().add(tab);
                }

                tab.setContent(rootNode);
                tabPaneTopDisplay.getSelectionModel().select(tab);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
        }
        
        /**
        * It shows Help content
        */
        public void appHelp(){
            HTMLEditor htmlEditor = new HTMLEditor();
				
            String text = "Help";
            Tab tab = Common.getInstance().tabExists(text, tabPaneTopDisplay); 
            if(tab == null){
                tab = new Tab();
                tab.setText(text);
                tabPaneTopDisplay.getTabs().add(tab);
            }

            tab.setContent(htmlEditor);
            tabPaneTopDisplay.getSelectionModel().select(tab);
        }
        
        /**
        * Additional settings
        */
        public void appSettings(){
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            try {

                String fxmlFile = "/fxml/settings.fxml";
                FXMLLoader loader = new FXMLLoader();
                VBox rootNode = (VBox) loader.load(getClass().getResourceAsStream(fxmlFile));

                Scene scene = new Scene(rootNode);
                dialog.setScene(scene);


                dialog.initModality(Modality.WINDOW_MODAL);
                //dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );

            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
            dialog.show();
        }
        
        /**
        * Close the application 
        */
        public void appClose(){
            System.exit(0);
        }
	
	private void setStyles(){
            //menuItemExploreDatabase.setStyle("-fx-background-color: transparent;-fx-text-fill: #E5E4E2;-fx-font-weight: bold");
            //menuItemManageDatabase.setStyle("-fx-background-color: transparent;-fx-text-fill: #E5E4E2;-fx-font-weight: bold");
            //menuItemSettings.setStyle("-fx-background-color: transparent;-fx-text-fill: #E5E4E2;-fx-font-weight: bold");
            //menuItemHelp.setStyle("-fx-background-color: transparent;-fx-text-fill: #E5E4E2;-fx-font-weight: bold");
            //menuItemClose.setStyle("-fx-background-color: transparent;-fx-text-fill: #E5E4E2;-fx-font-weight: bold");
            toolBar.setStyle("-fx-background-color: linear-gradient(#888888, #333333)");
		
	}
}
