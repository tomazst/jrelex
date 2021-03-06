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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.FromDBTypeToPropType;

/**
 *
 * @author tomaz
 */
public class PojoGenerator {
    private Map<String, Class<?>> lastPojoProperties;
    private Map<String, Class<?>> tableClases;

    private static final Logger log = LoggerFactory
            .getLogger(PojoGenerator.class);

    public PojoGenerator() {
        tableClases = new HashMap<String, Class<?>>();
    }

    public Class<?> generate(String className, Map<String, Class<?>> properties)
            throws NotFoundException, CannotCompileException {

        if(tableClases.containsKey(className)){
            return tableClases.get(className);
        }

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(className);


        // add this to define a super class to extend
        // cc.setSuperclass(resolveCtClass(MySuperClass.class));

        // add this to define an interface to implement
        cc.addInterface(resolveCtClass(Serializable.class));

        for (Entry<String, Class<?>> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            Class<?> type = entry.getValue();
            CtField field = new CtField(resolveCtClass(type), fieldName, cc);
            cc.addField(field);

            String funcName = fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);

            // add getter
            CtMethod getter = CtNewMethod.getter("get" + funcName, field);
            cc.addMethod(getter);

            // add setter
            CtMethod setter = CtNewMethod.setter("set" + funcName, field);
            cc.addMethod(setter);
        }

        tableClases.put(className, cc.toClass());
        cc.detach();
        return tableClases.get(className);
    }

    private CtClass resolveCtClass(Class<?> clazz) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return pool.get(clazz.getName());
    }

    public Object createPojoForTable(String tableName, CTable table) {
        Map<String, Class<?>> pojoPropeties = new HashMap<>();
        lastPojoProperties = pojoPropeties;
        ArrayList<CColumn> columns = table.getColumns();
        Class<?> clazz = null;
        Object obj = null;

        pojoPropeties.put("position", Object.class);

        pojoPropeties.put("references", new HashMap<>().getClass());

        for (CColumn column : columns) {
            // user can select to see column
            if(!column.isVisible()){
                continue;
            }

            FromDBTypeToPropType typeConversion = new FromDBTypeToPropType();
            Class<?> columnType = typeConversion.getType(column.getType());
            pojoPropeties.put(column.getName(), columnType);
        }
        /*
         * for(Entry entry : columns.entrySet()){ String columnName =
         * entry.getKey().toString(); CColumn column =
         * (CColumn)entry.getValue();
         *
         * Class columnType =
         * Common.getInstance().getTypeConversion().getType(column.getType());
         *
         * pojoPropeties.put(columnName, columnType); }
         */
        try {
            clazz = generate(tableName, pojoPropeties);
        } catch (NotFoundException ex) {
            log.error(ex.getMessage(), ex);
            MessageDialogBuilder.error(ex).show(null);
        } catch (CannotCompileException ex) {
            log.error(ex.getMessage(), ex);
            MessageDialogBuilder.error(ex).show(null);
        }
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException ex) {
            log.error(ex.getMessage(), ex);
            MessageDialogBuilder.error(ex).show(null);
        } catch (IllegalAccessException ex) {
            log.error(ex.getMessage(), ex);
            MessageDialogBuilder.error(ex).show(null);
        }

        return obj;
    }

    public Map<String, Class<?>> getLastPojoProperties() {
        return lastPojoProperties;
    }

}
