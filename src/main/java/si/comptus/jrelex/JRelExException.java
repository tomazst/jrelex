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

/**
 * Application exception.
 * @author tomaz
 */
public class JRelExException extends Exception {
    
    private static final long serialVersionUID = 1997753363232807009L;
    
    /**
     * Constructor.
     */
    public JRelExException() {
        super();
    }

    public JRelExException(String message)
    {
        super(message);
    }

    public JRelExException(Throwable cause)
    {
        super(cause);
    }

    public JRelExException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JRelExException(String message, Throwable cause, 
                            boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
