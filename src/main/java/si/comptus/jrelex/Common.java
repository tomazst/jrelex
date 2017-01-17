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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import si.comptus.jrelex.container.CDatabaseStore;

/**
 *
 * @author tomaz
 */
public class Common {

    static private Common instance = null;
    private static final Logger log = LoggerFactory.getLogger(Common.class);
    private DatabaseInteraction databaseInteraction = null;
    private Map<Integer, String> jDBCTypes = null;
    private PojoGenerator pojoGenerator = null;
    private TabPane explorerLeftSideTabPane;
    private TabPane tablesTabPane;
    private SplitPane explorerSplitPane;
    private ScrollPane referencedDataPane;
    public SplitPane verticalSplitPane;
    /**
     * It has all stored databases from the disk
     */
    private CDatabaseStore dbstore = null;

    private Common() {
        setDbstore();
        databaseInteraction = new DatabaseInteraction();
        jDBCTypes = this.getJdbcTypesName();
        pojoGenerator = new PojoGenerator();
    }

    static public Common getInstance() {
        if (instance == null) {
            instance = new Common();
        }
        return instance;
    }

    public SplitPane getVerticalSplitPane() {
        return verticalSplitPane;
    }

    public void setVerticalSplitPane(SplitPane verticalSplitPane) {
        this.verticalSplitPane = verticalSplitPane;
    }

    public PojoGenerator getPojoGenerator() {
        return pojoGenerator;
    }

    public CDatabaseStore getDbstore() {
        if (dbstore == null) {
            setDbstore();
        }
        return dbstore;
    }

    public void setDbstore(CDatabaseStore dbstore) {
        this.dbstore = dbstore;
    }

    public ScrollPane getReferencedDataPane() {
        return referencedDataPane;
    }

    public void setReferencedDataPane(ScrollPane referencedDataPane) {
        this.referencedDataPane = referencedDataPane;
    }

    /**
     * it reads data saved on disk as serialized file
     */
    public void setDbstore() {
        this.dbstore = this.readSerializedDBMetaDataFromDisk();
    }

    public DatabaseInteraction getDatabaseInteraction() {
        return databaseInteraction;
    }

    public void setDatabaseInteraction(DatabaseInteraction databaseInteraction) {
        this.databaseInteraction = databaseInteraction;
    }

    public TabPane getExplorerLeftSideTabPane() {
        return explorerLeftSideTabPane;
    }

    public void setExplorerLeftSideTabPane(TabPane explorerLeftSideTabPane) {
        this.explorerLeftSideTabPane = explorerLeftSideTabPane;
    }

    public TabPane getTablesTabPane() {
        return tablesTabPane;
    }

    public void setTablesTabPane(TabPane tablesTabPane) {
        this.tablesTabPane = tablesTabPane;
    }

    public SplitPane getExplorerSplitPane() {
        return explorerSplitPane;
    }

    public void setExplorerSplitPane(SplitPane explorerSplitPane) {
        this.explorerSplitPane = explorerSplitPane;
    }

    public Map<Integer, String> getjDBCTypes() {
        return jDBCTypes;
    }

    public void setjDBCTypes(Map<Integer, String> jDBCTypes) {
        this.jDBCTypes = jDBCTypes;
    }

    private Map<Integer, String> getJdbcTypesName() {
        Map<Integer, String> map = new HashMap<Integer, String>();

        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                Integer value = (Integer) field.get(null);
                map.put(value, name);
            }
            catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
        }
        return map;
    }

    private CDatabaseStore readSerializedDBMetaDataFromDisk() {
        CDatabaseStore dstore = new CDatabaseStore();
        FileInputStream fis = null;
        ObjectInputStream in = null;

        String dir = OSUtils.userDataFolder("jrelex");

        String filename = "dbmanagement.ser";

        File file = new File(dir + filename);
        if (file.exists()) {
            try {
                fis = new FileInputStream(dir + filename);
                in = new ObjectInputStream(fis);
                dstore = (CDatabaseStore) in.readObject();
                in.close();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                MessageDialogBuilder.error(ex).show(null);
            }

        }
        return dstore;
    }

    public boolean saveSerializedDBMetaDataToDisk(CDatabaseStore dstore) {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        String dir = OSUtils.userDataFolder("jrelex");

        File d = new File(dir);
        if (!d.exists() && !d.isDirectory()) { // create folder
            d.mkdir();
        }

        String filename = "dbmanagement.ser";

        try {

            fos = new FileOutputStream(dir + filename);

            out = new ObjectOutputStream(fos);
            out.writeObject(dstore);
            out.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            MessageDialogBuilder.error(ex).show(null);
        }

        return true;
    }

    /**
     * If Tab is shown returns true else false
     *
     * @param id
     * @param tabPane
     * @return
     */
    public Tab tabExists(String text, TabPane tabPane) {

        List<Tab> tabList = (List<Tab>) tabPane.getTabs();

        for (Tab tab : tabList) {
            if (text.equals(tab.getText())) {
                return tab;
            }
        }

        return null;
    }

    public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o : c) {
            r.add(clazz.cast(o));
        }
        return r;
    }

    public void showTooltip(Stage owner, Control control, String tooltipText,
            ImageView tooltipGraphic) {
        Point2D p = control.localToScene(0.0, 0.0);

        final Tooltip customTooltip = new Tooltip();
        customTooltip.setText(tooltipText);

        control.setTooltip(customTooltip);
        customTooltip.setAutoHide(true);

        customTooltip.show(owner, p.getX()
                + control.getScene().getX() + control.getScene().getWindow().getX(), p.getY()
                + control.getScene().getY() + control.getScene().getWindow().getY());

    }

    /**
     * Enums are returned as strings in ObservableList.
     * @param <E enum
     * @param ens Array of enums
     * @return ObservableList
     */
    public <E extends Enum<E>> ObservableList enumsToList(final E[] ens) {
        final ObservableList list = FXCollections.observableArrayList();
        for (E item : ens) {
            list.add(item.toString());
        }
        return list;
    }

}
