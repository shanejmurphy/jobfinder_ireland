package helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import global.AppPool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Gamal Hilal & Ruairi O'Brien
 * @version 2
 * @since 1.1.1 - 12/08/2011
 *        <p/>
 *        Exposes custom methods for accessing the Android file system using standard Android methods.
 */
public final class FileHelper {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "FileHelper";
	private static final String SITE_DIR = "site" + File.separator;
	private static final String TEMP_DIR = "temp" + File.separator;
	private static final String FILE_DIR = File.separator + "Android" + File.separator + "data" + File.separator;
	// ===========================================================
	// Fields
	// ===========================================================
	private static boolean externalStorageAvailable = false;
	private static boolean externalStorageWritable = false;


	// ===========================================================
	// Constructors
	// ===========================================================
	private FileHelper() {
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static synchronized FileHelper getInstance() {
		return FileHelperHolder.instance;
	}

	/**
	 * @return - Local data directory for application
	 * @since 1.2
	 */

	public static String getLocalFileDir() {
		return File.separator + "data" + File.separator + "data" + File.separator + AppPool.getPackageName() + File.separator + "files" + File.separator;
	}

	/**
	 * @return True if the SD card is currently available. False otherwise.
	 * @since 1.1
	 */
	public static boolean isExternalStorageAvailable() {
		checkSDCard();
		return externalStorageAvailable;
	}

	/**
	 * @return True if SD card can currently be written to. False otherwise.
	 * @since 1.1
	 */
	public static boolean isExternalStorageWritable() {
		checkSDCard();
		return externalStorageWritable;
	}

	/**
	 * Gets the root directory for this applications external file storage.
	 *
	 * @return Path of files directory in SD Card
	 * @since 1.1
	 */

	public static String getRootFileDir() {
		File sdCard = Environment.getExternalStorageDirectory();
		return sdCard.getAbsolutePath() + FILE_DIR + AppPool.getPackageName()
				+ File.separator + "files" + File.separator;
	}

	/**
	 * Gets the SD Card directory for embedded web site
	 *
	 * @return Path of site directory in SD Card
	 * @since 1.1.0
	 */

	public static String getSiteDir() {
		return getRootFileDir() + SITE_DIR;
	}

	/**
	 * Gets the SD Card directory to temporarily store downloaded site.
	 *
	 * @return Path of temporary directory in SD Card
	 * @since 1.1
	 */

	public static String getTempDir() {
		return getRootFileDir() + TEMP_DIR;
	}


	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * A function to return the attribute value in a given XML
	 *
	 * @param XMLContent A string representing the XML/XML element
	 * @param attribute  The name of the attribute to be searched for
	 * @return A string representing the attribute value or null if not found
	 * @since 1.0
	 * @deprecated 1.2
	 */

	public static String getAttributeValue(String XMLContent, String attribute) {
		if (XMLContent == null) {
			return null;
		}
		String result;
		String XMLContentTemp = XMLContent.toLowerCase(Locale.ENGLISH);
		attribute = attribute.toLowerCase(Locale.ENGLISH);
		attribute = attribute + "=\"";
		int attributeIndex = XMLContentTemp.indexOf(attribute);
		if (attributeIndex < 0) { // Attribute doesn't exist in XML
			return null;
		}
		else {
			attributeIndex += (attribute.length());
			int secondQuoteIndex = XMLContentTemp.indexOf("\"", attributeIndex + 1);
			result = XMLContent.substring(attributeIndex, secondQuoteIndex);
			result = result.replace("\"", "");
			return result;
		}
	}

	/**
	 * A function to return the element value in an XML or null if not found
	 *
	 * @param XMLContent A string representing the XML content
	 * @param element    The element to be searched for
	 * @return returns the value of the element or null if not found
	 * @since 1.0
	 * @deprecated 1.2
	 */

	public static String getElementValue(String XMLContent, String element) {
		try {
			if (XMLContent == null) {
				return null;
			}
			String result;
			String XMLContentTemp = XMLContent.toLowerCase(Locale.ENGLISH);
			element = element.toLowerCase(Locale.ENGLISH);
			String elementEndTag;
			elementEndTag = element;
			elementEndTag = new StringBuffer(elementEndTag).insert(1, "/").toString();
			int elementStartIndex = XMLContentTemp.indexOf(element) + element.length();
			int elementEndIndex = XMLContentTemp.indexOf(elementEndTag);
			if (elementEndIndex > 0) {
				result = XMLContent.substring(elementStartIndex, elementEndIndex);
				result = result.replace("\"", "");
				if (result.contains("&amp;")) {
					result = result.replace("&amp;", "&");
				}
				if (result.contains("&#163;")) {
					result = result.replace("&#163;", "£");
				}
				result = result.trim();
				if (result.length() > 0) {
					return result;
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
			return null;
		}
	}

	/**
	 * Download any file with a valid MIME type from a URL and save it to the file passed in as a the second parameter.
	 *
	 * @param url  The url for the file to be downloaded
	 * @param file The target file/location
	 * @return successful or not
	 * @since 1.1.3
	 */
	public static boolean downloadFile(URL url, File file) {
		try {
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.connect();
			InputStream is = c.getInputStream();
			FileOutputStream fos = new FileOutputStream(file, false);
			byte[] buffer = new byte[1024];
			int len1;
			while ((len1 = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len1);
			}
			fos.flush();
			fos.close();
			is.close();
			return true;
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
			return false;
		}
	}


	/**
	 * Use to delete files. Also use to delete a folder and all its contents including sub folders
	 *
	 * @param fileOrDirectory If a file is passed it is deleted, if a directory is passed all it contents and the
	 *                        directory are deleted.
	 * @since 1.2
	 */
	public static void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}

		if (!fileOrDirectory.delete()) {
			LogHelper.logDebug(TAG, "Error deleting file recursively");
		}
	}

	/**
	 * Function to get the last modified date of a file.
	 *
	 * @param file The file object to get the date from.
	 * @return Last Modified date as String
	 * @since 1.1
	 */
	public static String getFileDate(File file) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		try {
			if (file.exists()) {
				Date fileLastModified = new Date(file.lastModified());
				return sdf.format(fileLastModified);

			}
			else {
				return sdf.format(new Date(0));
			}
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
			return sdf.format(new Date(0));
		}
	}

	/**
	 * A function to read in a file given a full path, i.e. /data/data..... and returns a string of the content
	 *
	 * @param filePath - The path to the file to be read
	 * @return The files text or nothing if the file does not exist.
	 * @since 1.0
	 */

	public static String readFileToString(String filePath) {
		BufferedReader reader = null;
		StringBuilder stringBuilder;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
			String line;
			stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			String result = stringBuilder.toString();
			result = result.replace("\n", "");
			return result;

		} catch (Exception e) {
			//Log.e(TAG, e.getStackTrace()[1].getMethodName(), e);

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				LogHelper.logException(TAG, e);
			}
		}
		return null;
	}

	/**
	 * Writes a give string to a file in the application data directory with the given name.
	 *
	 * @param context       Context used for openFileOutput method.
	 * @param stringToWrite String to be written to a file
	 * @param fileName      Name of file - will be created if one does not exist.
	 * @return True if successful
	 * @since 1.0
	 */
	public static boolean writeStringToLocalDataFile(Context context, String stringToWrite,
													 String fileName) throws FileNotFoundException {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(stringToWrite.getBytes());
			fos.flush();
			return true;

		} catch (Exception e) {
			LogHelper.logException(TAG, e);
			return false; // write not successful
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				LogHelper.logException(TAG, e);
			}
		}
	}

	public static String readFromFile(Context context, String filename) {

	    String ret = "";
	    try {
	        InputStream inputStream = context.openFileInput(filename);//new FileInputStream(filename);

	        if (inputStream != null) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        LogHelper.logDebug(TAG, "File not found: " + e.toString());
	    } catch (IOException e) {
	    	LogHelper.logDebug(TAG, "Can not read file: " + e.toString());
	    }

	    return ret;
	}

	/**
	 * TODO: This method needs to be cleaned up Write a file to the application directory on the SD card. Will overwrite
	 * an existing file if one already exists. Will create a new file if none exists. Will return true is write success
	 * and false if an error occurs such as SD card is mounted or does not exist.
	 *
	 * @param context         Context used for file IO methods.
	 * @param fileToWrite     File object to be copied.
	 * @param destinationPath Target path for file.
	 * @return Boolean: Successful or not
	 * @since 1.1
	 */
	public static boolean writeFileToPath(Context context,
										  File fileToWrite, String destinationPath) {
		FileOutputStream fos = null;
		FileInputStream is = null;
		try {
			if (isExternalStorageWritable()) {
				String fullPath = getRootFileDir();
				if (destinationPath.startsWith(fullPath)) {
					destinationPath = destinationPath.replace(fullPath, "");
				}

				String[] dirs = null;
				if (destinationPath.contains(File.separator)) {
					dirs = destinationPath.split(File.separator);
				}

				String fileName = null;
				if (dirs != null) {
					fileName = dirs[dirs.length - 1];
					if (dirs.length > 1) {
						for (int i = 0; i < dirs.length - 1; i++) {
							fullPath += dirs[i] + File.separator;
							File dir = new File(fullPath);
							if (!dir.exists()) {
								dir.mkdirs();
							}
						}
					}
				}

				File file = new File(fullPath, fileName);

				if (file.exists()) {
					file.delete();
				}

				fos = new FileOutputStream(file);
				is = new FileInputStream(fileToWrite);

				byte[] data = new byte[is.available()];

				is.read(data);
				fos.write(data);
			}
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				LogHelper.logException(TAG, e);
			}
		}
		return true;
	}

	/**
	 * Check if the SD card is available. Returns void but sets local scoped variables: externalStorageAvailable and
	 * externalStorageWriteable
	 */
	private static void checkSDCard() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			externalStorageAvailable = externalStorageWritable = true;
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			externalStorageAvailable = true;
			externalStorageWritable = false;
		}
		else {
			// Something else is wrong.
			externalStorageAvailable = externalStorageWritable = false;
		}
	}

	/**
	 * A function to return true if file's copied from assets folder to local data folder successfully or false
	 * otherwise. If false returned, then file already exists in local data folder. The parameters should be just the
	 * file names and not the full path. Asset file should be in asset folder and local file will be in
	 * data/data/App_name/files folder.
	 *
	 * @param context       Context used to get Assets.
	 * @param assetFilePath Path relative to assets directory.
	 * @param localFilePath Path to file in application data directory.
	 * @return file copied
	 * @since 1.0
	 */
	public static boolean copyAssetsFile(Context context, String assetFilePath, String localFilePath) {
		FileOutputStream fos = null;
		File file = new File(localFilePath);
		if (!(file.exists())) { // Don't copy file from assets if already copied
			// before.
			// This code should only execute the first time the APK is launched.
			// Open the input file
			InputStream in = null;
			try {
				in = context.getAssets().open(assetFilePath);
			} catch (IOException e) {
				LogHelper.logException(TAG, e);
			}
			// Open the output file
			String outFilename = file.getName();

			try {
				fos = context.openFileOutput(outFilename, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e) {
				LogHelper.logException(TAG, e);
			}
			// OutputStream out = new FileOutputStream(outFilename);

			// Transfer bytes from the input file to the output file
			byte[] buf = new byte[1024];
			int len;
			try {
				if (in != null) {
					while ((len = in.read(buf)) > 0) {
						if (fos != null) {
							fos.write(buf, 0, len);
						}
					}
				}
			} catch (Exception e) {
				LogHelper.logException(TAG, e);
			} finally {
				// Close the Streams
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					LogHelper.logException(TAG, e);
				}
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e) {
					LogHelper.logException(TAG, e);
				}
			}
			return true; // file was created
		}
		else {
			return false;
			// file already created, i.e not first time to launch APP
		}
	}

	/**
	 * Checks if an application is already installed on the device
	 *
	 * @param context Context used to get PackageManager
	 * @param uri     Application package name.
	 * @return application is installed
	 * @since 1.2
	 */
	public static boolean isAppInstalled(Context context, String uri) {
		PackageManager pm = context.getPackageManager();
		boolean installed;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			LogHelper.logException(TAG, e);
			installed = false;
		}
		return installed;
	}


	/**
	 * Generates a checksum value for a specified file.
	 *
	 * @param filePath Path to file that will have its MD5 checksum calculated.
	 * @return Byte array of checksum value.
	 * @since 1.2
	 */

	public static byte[] generateChecksum(String filePath) {
		try {
			InputStream inputStream = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int readCount;
			do {
				readCount = inputStream.read(buffer);
				if (readCount > 0) {
					complete.update(buffer, 0, readCount);
				}
			} while (readCount != -1);

			inputStream.close();
			return complete.digest();
		} catch (Exception e) {
			LogHelper.logException(TAG, e);

			return null;
		}
	}

	/**
	 * Get the MD5 checksum value of a file as a String.
	 *
	 * @param filePath Path to file that will have its MD5 checksum calculated.
	 * @return MD5 string checksum value of the given file.
	 * @since 1.2
	 */

	public static String getMD5Checksum(String filePath) {
		String result = null;
		try {
			byte[] b = generateChecksum(filePath);

			for (byte aB : b) {
				result += Integer.toString((aB & 0xff) + 0x100, 16)
						.substring(1);
			}
		} catch (Exception e) {

			LogHelper.logException(TAG, e);

			result = ""; // Error: returning empty String
		}
		return result;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class FileHelperHolder {
		public static final FileHelper instance = new FileHelper();
	}
}
