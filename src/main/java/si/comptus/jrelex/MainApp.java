package si.comptus.jrelex;

import java.io.File;
import java.util.Locale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panemu.tiwulfx.common.TiwulFXUtil;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import com.panemu.tiwulfx.table.TableControl;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
    	
    	File log4jfile = new File("src/main/resources/log4j.properties");
    	PropertyConfigurator.configure(log4jfile.getAbsolutePath());
    	
        ResourceBundle rb = ResourceBundle.getBundle("literal", new Locale("sl", "SI"));
        //ResourceBundle rb = ResourceBundle.getBundle("literal", new Locale("en", "US"));
        TiwulFXUtil.setLiteralBundle(rb);
    	
    	//log.info(OSUtils.userDataFolder("jrelex"));
    	
        launch(args);
    }

    public void start(Stage stage) {
    	
    	try{
    	
	        String fxmlFile = "/fxml/main.fxml";
	        
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource(""));
	        // System.out.println(loader.getLocation());
	        BorderPane rootNode = (BorderPane) loader.load(getClass().getResourceAsStream(fxmlFile));
                Scene scene = new Scene(rootNode);	        
	        scene.getStylesheets().add("styles/tiwulfx.css");
                scene.getStylesheets().add("styles/styles.css");
                //TiwulFXUtil.setTiwulFXStyleSheet(scene);
                                
	        stage.setTitle("JRelEx");
	        stage.setScene(scene);
	        stage.show();
        
    	} catch (Exception e){
    		log.error(e.getMessage(), e);
			MessageDialogBuilder.error(e).show(null);
    	} finally{
            try {
                Common.getInstance().getDatabaseInteraction().closeConnections();
                System.out.println("Povezave do baze so zaprte");
            } catch(SQLException e) {
                log.error(e.getMessage(), e);
		MessageDialogBuilder.error(e).show(null);
            }
        }
    }
}
