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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author tomaz
 */
public class FromDBTypeToPropType {

	private Set<String> stringTypes;
	private Set<String> integerTypes;
	private Set<String> floatTypes;
	private Set<String> doubleTypes;
	private Set<String> bigDecimalTypes;
	private Set<String> booleanTypes;
	private Set<String> dateTypes;
	private Set<String> timeTypes;
	private Set<String> timestampTypes;

	public FromDBTypeToPropType() {

		stringTypes = new HashSet<>();
		stringTypes.add("CHAR");
		stringTypes.add("VARCHAR");
		stringTypes.add("LONGVARCHAR");
		stringTypes.add("BINARY");
		stringTypes.add("VARBINARY");
		stringTypes.add("LONGVARBINARY");

		booleanTypes = new HashSet<>();
		booleanTypes.add("BIT");
		booleanTypes.add("BOOLEAN");

		integerTypes = new HashSet<>();
		integerTypes.add("TINYINT");
		integerTypes.add("SMALLINT");
		integerTypes.add("INTEGER");
		integerTypes.add("BIGINT");

		floatTypes = new HashSet<>();
		floatTypes.add("REAL");

		doubleTypes = new HashSet<>();
		doubleTypes.add("DOUBLE");
		doubleTypes.add("FLOAT");

		bigDecimalTypes = new HashSet<>();
		bigDecimalTypes.add("DECIMAL");
		bigDecimalTypes.add("NUMERIC");

		dateTypes = new HashSet<>();
		dateTypes.add("DATE");

		timeTypes = new HashSet<>();
		timeTypes.add("TIME");

		timestampTypes = new HashSet<>();
		timestampTypes.add("TIMESTAMP");
	}

	public Class<?> getType(String jdbcType) {

		if (stringTypes.contains(jdbcType)) {
			return String.class;
		}
		if (booleanTypes.contains(jdbcType)) {
			return Boolean.class;
		}
		if (integerTypes.contains(jdbcType)) {
			return Integer.class;
		}
		if (floatTypes.contains(jdbcType)) {
			return Float.class;
		}
		if (doubleTypes.contains(jdbcType)) {
			return Double.class;
		}
		if (bigDecimalTypes.contains(jdbcType)) {
			return Float.class;
		}
		if (dateTypes.contains(jdbcType)) {
			return Date.class;
		}
		if (timeTypes.contains(jdbcType)) { // TODO tiwulfx table does't support TIME
			//return Time.class;
		}
		if (timestampTypes.contains(jdbcType)) { // TODO tiwulfx table does't support TIMESTAMP
			//return Timestamp.class;
		}

		return String.class;
	}

}
