import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class FxTestScrollPane extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		GridPane grid = new GridPane();
		
				
		for(int i = 1; i < 10; i++) {
			for(int j = 1; j < 10; j++){
				Label lbl = new Label("R;"+i+" C;"+j);
				grid.add(lbl, i, j);
			}
		}
		
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setContent(grid);
		
		scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		//scrollpane.setFitToHeight(true);
		VBox.setVgrow(scrollpane, Priority.ALWAYS);
		
		VBox vbox = new VBox();
		vbox.setPrefHeight(400);
		vbox.setStyle("-fx-border-color: red");
		vbox.getChildren().add(scrollpane);
		
		Scene scene = new Scene(vbox);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("FxTestScrollPane");
        stage.setScene(scene);
        stage.setHeight(500);
        stage.setWidth(600);
        stage.show();
	}

}
