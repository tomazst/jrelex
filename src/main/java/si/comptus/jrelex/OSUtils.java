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

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * provides OS-specific utilities
 */
public class OSUtils {
	private static final Logger log = LoggerFactory.getLogger(OSUtils.class);
	//~ Enumerations ---------------------------------------------------------------------------------------------------

	public enum Platform {WIN32, WIN64, MAC32, MAC64, LINUX32, LINUX64;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static String getJVMPath() {
		return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
	}

	public static Platform getPlatform() {

		String bitage = System.getProperty("sun.arch.data.model");
		boolean bit64 = false;
		if (bitage.equals("32")) {
			bit64 = false;
		} else if (bitage.equals("64")) {
			bit64 = true;
		} else {
			log.info("Unknown data-model: '"+bitage+"'");
			MessageDialogBuilder.warning().message("Unknown data-model: '"+bitage+"'").show(null);
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") != -1) {
			return (bit64 ? Platform.WIN64 : Platform.WIN32);
		} else if (os.indexOf("mac") != -1) {
			return (bit64 ? Platform.MAC64 : Platform.MAC32);
		} else if (os.indexOf("linux") != -1) {
			return (bit64 ? Platform.LINUX64 : Platform.LINUX32);
		} else {
			log.info("unknown os: '"+os+"'");
			MessageDialogBuilder.warning().message("unknown os: '"+os+"'").show(null);
			return null;
		}
	}

	public static boolean isWindows() {
		return ((getPlatform() == Platform.WIN32) || (getPlatform() == Platform.WIN64));
	}

	public static boolean isMac() {
		return ((getPlatform() == Platform.MAC32) || (getPlatform() == Platform.MAC64));
	}

	public static boolean isLinux() {
		return ((getPlatform() == Platform.LINUX32) || (getPlatform() == Platform.LINUX64));
	}

	/**
	 * Returns the correct userDataFolder for the given application name.
	 */
	public static String userDataFolder(final String applicationName) {

		// default
		String folder = "." + File.separator;

		if (isMac()) {
			folder = System.getProperty("user.home") + File.separator + "Library" + File.separator
					 + "Application Support";
		} else if (isWindows()) {

			Map<String, Object> options = new HashMap<>();
			options.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			options.put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);

			HWND hwndOwner   = null;
			int nFolder		 = Shell32.CSIDL_LOCAL_APPDATA;
			HANDLE hToken    = null;
			int dwFlags		 = Shell32.SHGFP_TYPE_CURRENT;
			char pszPath[]   = new char[Shell32.MAX_PATH];
			Shell32 instance = (Shell32) Native.loadLibrary("shell32", Shell32.class, options);
			int hResult		 = instance.SHGetFolderPath(hwndOwner, nFolder, hToken, dwFlags, pszPath);
			if (Shell32.S_OK == hResult) {

				String path = new String(pszPath);
				int len     = path.indexOf('\0');
				folder	    = path.substring(0, len);
			} else {
				System.err.println("Error: " + hResult);
			}
		}

		folder = folder + File.separator + applicationName + File.separator;

		return folder;
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	private static interface Shell32 extends Library {

		public static final int MAX_PATH									  = 260;
		public static final int CSIDL_LOCAL_APPDATA							  = 0x001c;
		public static final int SHGFP_TYPE_CURRENT							  = 0;
		@SuppressWarnings("unused")
		public static final int SHGFP_TYPE_DEFAULT							  = 1;
		public static final int S_OK										  = 0;

		/**
		 * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
		 *
		 * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
		 * DWORD dwFlags, LPTSTR pszPath);
		 */
		public int SHGetFolderPath(final HWND hwndOwner, final int nFolder, final HANDLE hToken, final int dwFlags,
								   final char pszPath[]);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static class HANDLE extends PointerType {}

	private static class HWND extends HANDLE {}
}