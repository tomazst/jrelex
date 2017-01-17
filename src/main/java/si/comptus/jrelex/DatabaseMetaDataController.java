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

import com.panemu.tiwulfx.common.TiwulFXUtil;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CDatabase;
import si.comptus.jrelex.container.CDatabaseStore;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.container.ConnBean;

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import si.comptus.jrelex.configuration.RDBMSType;
import si.comptus.jrelex.database.DatabaseMetaDataDAO;

/**
 * Controller for FXML template. It is used to set data needed for browsing the relational database.
 *
 * @author tomaz
 */
public class DatabaseMetaDataController implements Initializable {

    /**
     * Loggerd.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseMetaDataController.class);
    /**
     * Form control. Combo box contains names for RDMS.
     */
    @FXML
    private ComboBox<String> cbDbms;
    /**
     * Form control. The connection title.
     */
    @FXML
    private TextField txfName;
    /**
     * Form control. Editable combo box to insert or chose port.
     */
    @FXML
    private ComboBox<String> cbPort;
    /**
     * Form control. Server hostname.
     */
    @FXML
    private TextField txfHostname;
    /**
     * Form control. Username.
     */
    @FXML
    private TextField txfUsername;
    /**
     * Form control. Editable combo box for shemas.
     */
    @FXML
    private ComboBox<String> cbDatabase;
    /**
     * Form control. User password.
     */
    @FXML
    private PasswordField pwfPassword;
    /**
     * TreeView contains list of all saved databases, tables and columns. User can chose which
     * database, table or column can be shown in relational browser.
     */
    @FXML
    private TreeView trvDatabaseList;
    /**
     * It saves form connection data and database meta data to local file (dbmanagement.ser).
     */
    @FXML
    private Button btnSave;
    /**
     * Container for form data.
     */
    private DatabaseConnDataVO connVO;
    /**
     * It shows progress while reading table meta data from database.
     */
    @FXML
    private ProgressBar progressBar;
    /**
     * It shows progress data while reading table meta data.
     */
    @FXML
    private TextArea txtProgress;
    /**
     * TextField to filter databases in TreeView.
     */
    @FXML
    private TextField txtDBFilter;
    /**
     * TextField to filter tables in TreeView.
     */
    @FXML
    private TextField txtTBLFilter;
    /**
     * Label shows error if user forgets to choose RDMS in ComboBox.
     */
    @FXML
    private Label lblDbms;
    /**
     * Label shows error if user forgets to type name for connection.
     */
    @FXML
    private Label lblName;
    /**
     * Label shows error if user forgets to type hostname for connection.
     */
    @FXML
    private Label lblHostname;
    /**
     * Label shows error if user forgets to choose port.
     */
    @FXML
    private Label lblPort;
    /**
     * Label shows error if user forgets to type username.
     */
    @FXML
    private Label lblUsername;
    /**
     * Label shows error if user forgets to type or choose shema name.
     */
    @FXML
    private Label lblDatabase;

    /**
     * String.
     */
    private static final String STR_DATABASES = "Databases";

    /**
     * Contructor.
     */
    public DatabaseMetaDataController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public final void initialize(final URL url, final ResourceBundle rb) {

        this.cbDbms.setItems(Common.getInstance().enumsToList(RDBMSType.values()));

        this.refreshTreeViewDatabaseList(
                Common.getInstance().getDbstore().getDatabases()
        );
        this.trvDatabaseList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.trvDatabaseList.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

        this.trvDatabaseList.setEditable(true);

        this.trvDatabaseList.getSelectionModel().selectedItemProperty()
                .addListener((ChangeListener<Object>) (ObservableValue<?> ov, Object t, Object t1) -> {
                    final TreeItem<?> treeItem = (TreeItem<?>) t1;
                    if (treeItem != null
                    && treeItem.getParent() != null
                    && STR_DATABASES.equals(treeItem.getParent().getValue())) {
                        // get database index with regex
                        final String connectionString = (String) treeItem.getValue();
                        final Pattern pattern = Pattern.compile("\\(.*.\\)");
                        final Matcher matcher = pattern.matcher(connectionString);
                        if (matcher.find()) {
                            String databaseIndex = matcher.group();
                            databaseIndex = databaseIndex.replaceAll("[\\(\\)]", "");
                            this.fillDatabaseManageForm(databaseIndex);
                        }
                    }
                });
    }

    /**
     * Saving selection in database treeview.
     */
    public final void saveSelection() {
        final CDatabaseStore dbStore = Common.getInstance().getDbstore();
        Common.getInstance().saveSerializedDBMetaDataToDisk(dbStore);
    }

    /**
     * User can revert selection before saving.
     */
    public final void revertSelection() {
        Common.getInstance().setDbstore();
        this.refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
    }

    /**
     * Database from UI form gets loaded.
     */
    public final void reloadDatabaseList() {
        List<String> dbList = new ArrayList<>();
        this.connVO = this.extractDataFromForm(new DatabaseConnDataVO());

        try {
            dbList = Common.getInstance().getDatabaseInteraction().getDatabaseList(this.connVO);
        } catch (SQLException ex) {
            LOG.error(TiwulFXUtil.getLiteral("error.connection"), ex);
            MessageDialogBuilder.error(ex).show(null);
        }

        this.cbDatabase.setItems(FXCollections.observableList(dbList));
        this.cbDatabase.show();
    }

    /**
     * Default form data.
     */
    public final void newConnection() {
        this.cbDbms.setValue("");
        this.txfName.setText("");
        this.txfHostname.setText("");
        this.cbPort.setValue("0000");
        this.txfUsername.setText("");
        this.cbDatabase.setValue("");
        this.pwfPassword.setText("");

    }

    /**
     * Collects data and saves connection.
     */
    public final void saveConnection() {

        if (this.isFormValid()) {

            Connection connection = null;
            try {
                connection = this.connectToDatabase(this.connVO);
            } catch (SQLException e) {
                LOG.error(TiwulFXUtil.getLiteral("error.connection"), e);
                MessageDialogBuilder.error(e).show(null);
                return;
            }

            // crate tread
            final DatabaseMetaDataController.DatabaseMetaDataTask task
                    = new DatabaseMetaDataTask(connection);

            final Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    /**
     * Checks if form data is valid.
     *
     * @return boolean
     */
    private boolean isFormValid() {
        if (this.validateForm()) {
            this.extractDataFromForm(this.connVO);
            return true;
        }
        return false;
    }

    /**
     * Checks form data.
     *
     * @return tag
     */
    private boolean validateForm() {
        FormValidator.getInstance().setFormValid(true);
        FormValidator.getInstance().isTextEmpty(
                this.cbDbms.getValue(), this.lblDbms,
                TiwulFXUtil.getLiteral("formValidatorMessage.empty.rdbms")
        );
        FormValidator.getInstance().isTextEmpty(
                this.txfName.getText(), this.lblName,
                TiwulFXUtil.getLiteral("formValidatorMessage.empty.name")
        );
        FormValidator.getInstance().isTextEmpty(
                this.txfHostname.getText(), this.lblHostname,
                TiwulFXUtil.getLiteral("formValidatorMessage.empty.hostname")
        );
        if (this.cbPort.getValue() == null) {
            FormValidator.getInstance().isTextEmpty(
                    this.cbPort.getEditor().getText(), this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.empty.port")
            );
            FormValidator.getInstance().isAllowedLength(
                    this.cbPort.getEditor().getText(), 4, this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.size.port")
            );
            FormValidator.getInstance().isNumeric(
                    this.cbPort.getEditor().getText(), this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.numeric.port")
            );
        } else {
            FormValidator.getInstance().isTextEmpty(
                    this.cbPort.getValue(), this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.empty.port")
            );
            FormValidator.getInstance().isAllowedLength(
                    this.cbPort.getValue(), 4, this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.size.port")
            );
            FormValidator.getInstance().isNumeric(
                    this.cbPort.getValue(), this.lblPort,
                    TiwulFXUtil.getLiteral("formValidatorMessage.numeric.port")
            );
        }
        FormValidator.getInstance().isTextEmpty(
                this.txfUsername.getText(), this.lblUsername,
                TiwulFXUtil.getLiteral("formValidatorMessage.empty.username")
        );
        //FormValidator.getInstance().isTextEmpty(pwfPassword.getText(), lblPassword, "Password is empty!");
        if (this.cbDatabase.getValue() == null) {
            FormValidator.getInstance().isTextEmpty(
                    this.cbDatabase.getEditor().getText(),
                    this.lblDatabase,
                    TiwulFXUtil.getLiteral("formValidatorMessage.empty.username"));
        } else {
            FormValidator.getInstance().isTextEmpty(
                    this.cbDatabase.getValue(),
                    this.lblDatabase,
                    TiwulFXUtil.getLiteral("formValidatorMessage.empty.database")
            );
        }
        return FormValidator.getInstance().isFormValid();
    }

    /**
     * Gets data from form.
     *
     * @param vo DatabaseConnDataVO
     * @return DatabaseConnDataVO
     */
    private DatabaseConnDataVO extractDataFromForm(final DatabaseConnDataVO vo) {
        vo.setDriver(this.cbDbms.getValue());
        vo.setName(this.txfName.getText());
        vo.setHostname(this.txfHostname.getText());
        if (this.cbPort.getValue() == null) {
            vo.setPort(Integer.parseInt(this.cbPort.getEditor().getText()));
        } else {
            vo.setPort(Integer.parseInt(this.cbPort.getValue()));
        }
        vo.setUsername(this.txfUsername.getText());
        vo.setPassword(this.pwfPassword.getText());
        if (this.cbDatabase.getValue() == null) {
            vo.setDatabase(this.cbDatabase.getEditor().getText());
        } else {
            vo.setDatabase(this.cbDatabase.getValue());
        }
        return vo;
    }

    /**
     * Deletes database meta data.
     */
    public final void deleteConnection() {
        Common.getInstance().getDbstore().getDatabases().remove(
                this.connVO.getName()
        );
        Common.getInstance().saveSerializedDBMetaDataToDisk(
                Common.getInstance().getDbstore()
        );
        this.newConnection();
        this.refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
    }

    /**
     * Method is inserting data to UI form.
     *
     * @param storedDatabaseId String
     */
    public final void fillDatabaseManageForm(final String storedDatabaseId) {
        final CDatabase database = Common.getInstance()
                .getDbstore().getDatabases().get(storedDatabaseId);

        final ConnBean connBean = database.getConnBean();

        this.cbDbms.setValue(connBean.getDriver().toString());
        this.txfName.setText(connBean.getName());
        this.txfHostname.setText(connBean.getHostname());
        this.cbPort.setValue(String.valueOf(connBean.getPort()));
        this.txfUsername.setText(connBean.getUsername());
        this.pwfPassword.setText(connBean.getPassword());
        this.cbDatabase.setValue(connBean.getDatabase());

        this.connVO.setDriver(connBean.getDriver().toString());
        this.connVO.setName(connBean.getName());
        this.connVO.setHostname(connBean.getHostname());
        this.connVO.setPort(connBean.getPort());
        this.connVO.setUsername(connBean.getUsername());
        this.connVO.setPassword(connBean.getPassword());
        this.connVO.setDatabase(connBean.getDatabase());

    }

    /**
     * TreeView node.
     * @param name connection id
     * @param value database name
     * @param check to check
     * @return CheckBoxTreeItem
     */
    private CheckBoxTreeItem<String> getDatabaseTreeViewItem(
            final String name, final String value, final boolean check) {
        final CheckBoxTreeItem<String> tviDatabase = new CheckBoxTreeItem<>(
                name + " (" + value + ")"
        );

        tviDatabase.setSelected(check);
        tviDatabase.selectedProperty().addListener((Observable o) -> {
            final BooleanProperty property = (BooleanProperty) o;
            final TreeItem<String> item = (TreeItem<String>) property.getBean();
            final String itemName = item.getValue();
            final String[] splitedItemName = itemName.split(" \\(");
            final String storedDatabaseID = splitedItemName[1].replace(")", "");
            Common.getInstance().getDbstore()
                    .getDatabases().get(storedDatabaseID).setVisible(property.getValue());
        }
        );

        return tviDatabase;
    }

    /**
     * TreeView node.
     * @param databaseName database name
     * @param name table name
     * @param check to check
     * @return CheckBoxTreeItem
     */
    private CheckBoxTreeItem<String> getTableTreeViewItem(
            final String databaseName, final String name, final boolean check) {

        final CheckBoxTreeItem<String> tviTable = new CheckBoxTreeItem<>(
                databaseName + "." + name);

        //tableItem.setGraphic(imgViewTable);
        tviTable.setSelected(check);

        tviTable.selectedProperty().addListener((Observable o) -> {
            final BooleanProperty property = (BooleanProperty) o;

            final TreeItem<String> item = (TreeItem<String>) property.getBean();
            final String[] splitedTableName = item.getValue().split("\\.");
            final String stableName = splitedTableName[1];

            final TreeItem<String> itemParent = (TreeItem<String>) item.getParent();
            final String[] splitedItemParentName = itemParent.getValue().split(" \\(");
            final String storedDatabase = splitedItemParentName[1].replace(")", "");

            Common.getInstance().getDbstore()
                    .getDatabases().get(storedDatabase).getTables()
                    .get(stableName).setVisible(property.getValue());

        });

        return tviTable;
    }

    /**
     * TreeView node.
     *
     * @param column column name
     * @param check checked
     * @return CheckBoxTreeItem
     */
    private CheckBoxTreeItem<String> getColumnTreeViewItem(
            final String column, final boolean check) {

        final CheckBoxTreeItem<String> tviColumn = new CheckBoxTreeItem<>(column);

        tviColumn.setSelected(check);

        tviColumn.selectedProperty().addListener((Observable o) -> {
            final BooleanProperty property = (BooleanProperty) o;

            final TreeItem<String> item = (TreeItem<String>) property.getBean();
            final String columnName = item.getValue();

            final TreeItem<String> itemParent = (TreeItem<String>) item.getParent();
            final String[] splitedTableName = itemParent.getValue().split("\\.");
            final String tableName = splitedTableName[1];

            final String itemParentParentName = itemParent.getParent().getValue();
            final String[] spliteditemParentParentName = itemParentParentName.split(" \\(");
            final String sstoredDatabase = spliteditemParentParentName[1].replace(")", "");

            Common.getInstance().getDbstore()
                    .getDatabases().get(sstoredDatabase).getTables()
                    .get(tableName).getColumnByName(columnName).setVisible(property.getValue());
        }
        );

        return tviColumn;
    }

    /**
     * Refreshing database list in tree view.
     *
     * @param databases Saved database list.
     */
    private void refreshTreeViewDatabaseList(
            final Map<String, CDatabase> databases) {

        // tree view root
        final CheckBoxTreeItem<String> tviRoot = new CheckBoxTreeItem<>(STR_DATABASES);

        final Map<String, CDatabase> sortedDatabases = new TreeMap<>(databases);

        final Iterator<Map.Entry<String, CDatabase>> databaseIterator = sortedDatabases
                .entrySet().iterator();

        while (databaseIterator.hasNext()) {
            final Map.Entry<String, CDatabase> pairs
                    = (Map.Entry<String, CDatabase>) databaseIterator.next();

            final CDatabase storedDatabase = (CDatabase) pairs.getValue();

            // filter for database name
            if (this.filterName(storedDatabase.getName(), this.txtDBFilter.getText())) {
                continue;
            }

            final CheckBoxTreeItem<String> tviDatabase = this.getDatabaseTreeViewItem(
                    storedDatabase.getName(), pairs.getKey(), storedDatabase.isVisible());

            // databaseItem.checkBoxSelectionChangedEvent();
            final Map<String, CTable> tables = storedDatabase.getTables();
            if (tables != null) {

                final Map<String, CTable> sortedTables = new TreeMap<>(tables);
                final Iterator<Map.Entry<String, CTable>> tableIterator
                        = sortedTables.entrySet().iterator();

                while (tableIterator.hasNext()) {

                    final Map.Entry<String, CTable> pairs2
                            = (Map.Entry<String, CTable>) tableIterator.next();

                    final CTable table = pairs2.getValue();
                    final String tableName = pairs2.getKey();

                    // filter for table name
                    if (this.filterName(tableName, this.txtTBLFilter.getText())) {
                        continue;
                    }

                    final CheckBoxTreeItem<String> tviTable = this.getTableTreeViewItem(
                            storedDatabase.getName(), tableName, table.isVisible());

                    final List<CColumn> columns = table.getColumns();
                    if (columns != null) {

                        for (CColumn column : columns) {

                            final CheckBoxTreeItem<String> tviColumn = this.getColumnTreeViewItem(
                                    column.getName(), column.isVisible());

                            tviTable.getChildren().add(tviColumn);

                        }
                    }
                    tviDatabase.getChildren().add(tviTable);
                }
            }
            tviRoot.getChildren().add(tviDatabase);
        }
        tviRoot.setExpanded(true);
        this.trvDatabaseList.setRoot(tviRoot);
        this.trvDatabaseList.setShowRoot(false);

    }

    /**
     * Retrieves database connection. We need it to collect meta data from database.
     *
     * @param vo essential data for connection
     * @return Connection
     * @throws SQLException sql exception
     */
    private Connection connectToDatabase(final DatabaseConnDataVO vo) throws SQLException {
        final DataSource ds = Common.getInstance()
                .getDatabaseInteraction().getDataSource(
                        RDBMSType.valueOf(vo.getDriver()),
                        vo.getHostname(),
                        vo.getPort(),
                        vo.getUsername(),
                        vo.getPassword(),
                        vo.getDatabase()
                );
        final Connection connection = ds.getConnection();
        return connection;
    }

    /**
     * Reads meta data from database, stores it to disk and returns it.
     *
     * @param vo Contains essential data
     * @param connection Connection to database
     */
    private void readAndSaveDBMetaData(final DatabaseConnDataVO vo, final Connection connection) {

        // we add informations on connection
        final ConnBean bean = new ConnBean();
        bean.setDriver(RDBMSType.valueOf(vo.getDriver()));
        bean.setName(vo.getName());
        bean.setHostname(vo.getHostname());
        bean.setPort(vo.getPort());
        bean.setUsername(vo.getUsername());
        bean.setPassword(vo.getPassword());
        bean.setDatabase(vo.getDatabase());

        // Collect meta data from database
        final CDatabase metaData = this.readDBMetaData(bean, connection);
        metaData.setConnBean(bean);

        Common.getInstance().setDbstore();
        Common.getInstance().getDbstore().getDatabases()
                .put(bean.getName(), metaData);

        // Save meta data in to file
        if (Common.getInstance().saveSerializedDBMetaDataToDisk(
                Common.getInstance().getDbstore())) {
            JOptionPane.showMessageDialog(null, "Sucsesss :) !!!");
        }
    }

    /**
     * Reads meta data from database and fills CDatabase object.
     *
     * @param bean Contains essential data
     * @param connection Connection to database
     * @return CDatabase object
     */
    private CDatabase readDBMetaData(final ConnBean bean, final Connection connection) {

        DatabaseMetaDataDAO metaDataDao = null;
        try {
            metaDataDao = new DatabaseMetaDataDAO(connection, bean);
            metaDataDao.getDatabaseContainer().setName(bean.getDatabase());
            try {
                this.txtProgress.setText("");
                while (metaDataDao.tableIteratorHasNext()) {
                    final CTable table = metaDataDao.tableIteratorNext();

                    // UI Progress bar.
                    this.txtProgress.setText(this.txtProgress.getText()
                            + "Reading table: " + table.getName()
                            + " - " + metaDataDao.getCurrentTableIndex()
                            + " of " + metaDataDao.getTableCount() + "\n"
                    );
                    this.txtProgress.setScrollTop(Double.MAX_VALUE);
                    final float progress = (float) metaDataDao.getTableCount()
                            / (float) metaDataDao.getTableCount();
                    this.progressBar.setProgress(progress);
                }
            } finally {
                metaDataDao.tableIteratorClose();
            }

        } catch (SQLException e) {
            this.txtProgress.setText(this.txtProgress.getText()
                    + "Error while reading meta data!" + "\n" + e.getMessage());
            this.txtProgress.setScrollTop(Double.MAX_VALUE);
            LOG.error("Error while reading meta data!", e);
        }

        if (metaDataDao != null) {
            return metaDataDao.getDatabaseContainer();
        }
        return null;
    }

    /**
     * Refresh database tree view in meta data view.
     */
    public final void refreshTreeViewDBList() {
        this.refreshTreeViewDatabaseList(Common.getInstance()
                .getDbstore().getDatabases());
    }

    /**
     * It filters the names starting with the user entered string.
     *
     * @param name compared string
     * @param typedFilter user typed string
     * @return boolean
     */
    private boolean filterName(final String name, final String typedFilter) {
        boolean exclude = true;
        if ("".equals(typedFilter)
                || name.toLowerCase().startsWith(typedFilter.toLowerCase())) {
            exclude = false;
        }
        return exclude;
    }

    /**
     * Task is used in a new thread for reading and storing database meta data.
     */
    class DatabaseMetaDataTask extends Task<Void> {

        /**
         * Connection to database.
         */
        private Connection connection;

        /**
         * Contructor.
         *
         * @param connection Connection to database.
         */
        public DatabaseMetaDataTask(final Connection connection) {
            this.connection = connection;
        }

        @Override
        public Void call() {
            DatabaseMetaDataController.this.btnSave.setDisable(true);
            try {
                DatabaseMetaDataController.this.readAndSaveDBMetaData(
                        DatabaseMetaDataController.this.connVO, this.connection
                );
            }
            catch (Exception e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        LOG.error("Error while saving database meta data!", e);
                        MessageDialogBuilder.error(e).show(null);
                    }
                });
            }
            DatabaseMetaDataController.this.btnSave.setDisable(false);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            DatabaseMetaDataController.this.refreshTreeViewDatabaseList(
                    Common.getInstance().getDbstore().getDatabases());
        }
    }

}
