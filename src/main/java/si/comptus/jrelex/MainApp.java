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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Main class.
 * @author tomazst
 */
public class MainApp extends Application {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    /**
     * Constructor.
     */
    public MainApp() { }

    /**
     * Main method.
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {

        final File log4jfile = new File("src/main/resources/log4j.properties");
        PropertyConfigurator.configure(log4jfile.getAbsolutePath());

        final ResourceBundle rb = ResourceBundle.getBundle("literal", new Locale("en", "US"));
        TiwulFXUtil.setLiteralBundle(rb);

        //LOG.info(OSUtils.userDataFolder("jrelex"));
        launch(args);
    }

    /**
     * Method starts JavaFX application.
     * @param stage main window
     */
    @Override
    public final void start(final Stage stage) {

        try {
            final String fxmlFile = "/fxml/main.fxml";

            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(""));
            // System.out.println(loader.getLocation());
            final BorderPane rootNode = (BorderPane) loader.load(getClass()
                    .getResourceAsStream(fxmlFile));
            final Scene scene = new Scene(rootNode);
            scene.getStylesheets().add("styles/tiwulfx.css");
            scene.getStylesheets().add("styles/styles.css");
                //TiwulFXUtil.setTiwulFXStyleSheet(scene);

            stage.setTitle("JRelEx");
            stage.setScene(scene);
            stage.show();

        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }
        finally {
            /*
            try {
                Common.getInstance().getDatabaseInteraction().closeConnections();
                System.out.println("Povezave do baze so zaprte");
            }
            catch (SQLException e) {
                LOG.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
            */
        }
    }
}
