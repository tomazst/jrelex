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
package si.comptus.jrelex.database;

import java.sql.DatabaseMetaData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import si.comptus.jrelex.container.CDatabase;
import si.comptus.jrelex.container.CTable;

/**
 *
 * @author tomaz
 */
public class DatabaseMetaDataDAOTest {
    
    public DatabaseMetaDataDAOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of tableIteratorNext method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testTableIteratorNext() {
        System.out.println("tableIteratorNext");
        DatabaseMetaDataDAO instance = null;
        CTable expResult = null;
        CTable result = instance.tableIteratorNext();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tableIteratorHasNext method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testTableIteratorHasNext() {
        System.out.println("tableIteratorHasNext");
        DatabaseMetaDataDAO instance = null;
        boolean expResult = false;
        boolean result = instance.tableIteratorHasNext();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tableIteratorClose method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testTableIteratorClose() throws Exception {
        System.out.println("tableIteratorClose");
        DatabaseMetaDataDAO instance = null;
        instance.tableIteratorClose();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDatabaseContainer method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetDatabaseContainer() {
        System.out.println("getDatabaseContainer");
        DatabaseMetaDataDAO instance = null;
        CDatabase expResult = null;
        CDatabase result = instance.getDatabaseContainer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentTableIndex method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetCurrentTableIndex() {
        System.out.println("getCurrentTableIndex");
        DatabaseMetaDataDAO instance = null;
        int expResult = 0;
        int result = instance.getCurrentTableIndex();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMetaData method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetMetaData() throws Exception {
        System.out.println("getMetaData");
        DatabaseMetaDataDAO instance = null;
        DatabaseMetaData expResult = null;
        DatabaseMetaData result = instance.getMetaData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCatalog method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetCatalog() {
        System.out.println("getCatalog");
        DatabaseMetaDataDAO instance = null;
        String expResult = "";
        String result = instance.getCatalog();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShema method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetShema() {
        System.out.println("getShema");
        DatabaseMetaDataDAO instance = null;
        String expResult = "";
        String result = instance.getShema();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTableCount method, of class DatabaseMetaDataDAO.
     */
    @Test
    public void testGetTableCount() {
        System.out.println("getTableCount");
        DatabaseMetaDataDAO instance = null;
        int expResult = 0;
        int result = instance.getTableCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
