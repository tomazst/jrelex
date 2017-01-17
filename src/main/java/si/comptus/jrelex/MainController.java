////////////////////////////////////////////////////////////////////////////////////////////////////
// JRelEx: Java application is intended for searching data using database relations.
// Copyright (C) 2015 tomazst <tomaz.stefancic@gmail.com>.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
////////////////////////////////////////////////////////////////////////////////////////////////////

package si.comptus.jrelex;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

/**
 * Main controller.
 * @author tomazst
 */
public final class MainController implements Initializable {

    @FXML
    private TabPane tabPaneTopDisplay;
    @FXML
    private BorderPane toolBar;

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    /**
     * Controller.
     */
    public MainController() { }

    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        this.setStyles();
    }

    /**
     * Opens tab for exploring the database.
     */
    public void exploreDatabase() {
        try {
            final String fxmlFile = "/fxml/database_explorer.fxml";
            final String text = "Explore database";
            final FXMLLoader loader = new FXMLLoader();
            final SplitPane rootNode = (SplitPane) loader.load(
                    getClass().getResourceAsStream(fxmlFile)
            );

            Tab tab = Common.getInstance().tabExists(text, this.tabPaneTopDisplay);
            if (tab == null) {
                tab = new Tab();
                tab.setText(text);
                this.tabPaneTopDisplay.getTabs().add(tab);
            }

            tab.setContent(rootNode);
            this.tabPaneTopDisplay.getSelectionModel().select(tab);

        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }
    }

    /**
     * Opens form for changing settings or adding new connection to database.
     */
    public void manageDatabase() {
        try {
            final String fxmlFile = "/fxml/database_meta_data.fxml";
            final String text = "Manage database";
            final FXMLLoader loader = new FXMLLoader();
            final HBox rootNode = (HBox) loader.load(getClass().getResourceAsStream(fxmlFile));

            Tab tab = Common.getInstance().tabExists(text, this.tabPaneTopDisplay);
            if (tab == null) {
                tab = new Tab();
                tab.setText(text);
                this.tabPaneTopDisplay.getTabs().add(tab);
            }

            tab.setContent(rootNode);
            this.tabPaneTopDisplay.getSelectionModel().select(tab);

        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }
    }

    /**
     * It shows Help content.
     */
    public void appHelp() {
        final HTMLEditor htmlEditor = new HTMLEditor();
        final String text = "Help";
        Tab tab = Common.getInstance().tabExists(text, this.tabPaneTopDisplay);
        if (tab == null) {
            tab = new Tab();
            tab.setText(text);
            this.tabPaneTopDisplay.getTabs().add(tab);
        }
        tab.setContent(htmlEditor);
        this.tabPaneTopDisplay.getSelectionModel().select(tab);
    }

    /**
     * Additional settings.
     */
    public void appSettings() {
        final Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        try {

            final String fxmlFile = "/fxml/settings.fxml";
            final FXMLLoader loader = new FXMLLoader();
            final VBox rootNode = (VBox) loader.load(getClass().getResourceAsStream(fxmlFile));

            final Scene scene = new Scene(rootNode);
            dialog.setScene(scene);

            dialog.initModality(Modality.WINDOW_MODAL);

        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }
        dialog.show();
    }

    /**
     * Close the application.
     */
    public void appClose() {
        System.exit(0);
    }

    private void setStyles() {
        toolBar.setStyle("-fx-background-color: linear-gradient(#888888, #333333)");
    }
}
