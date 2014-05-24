package helpers;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import helpers.FileHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;


public class LogHelper {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "LogHelper";

	// ===========================================================
	// Fields
	// ===========================================================
	private static boolean logToFile;
	private static boolean sIsInDebugMode;

	// ===========================================================
	// Constructors
	// ===========================================================
	private LogHelper() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static synchronized LogHelper getInstance() {
		return SingletonHolder.instance;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void initialize(Context pContext) {
		sIsInDebugMode = isInDebugMode(pContext);
	}


	/**
	 * Check if the application is in debug mode. setIsInDebugMode must be called during initialization or this will
	 * always be set to false.
	 *
	 * @return Boolean: debug mode
	 * @since 1.2
	 */
	public static boolean isInDebugMode(Context context) {
		return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
	}


	private static String ProcessAndThreadId(String label) {
		return String.format("%s , Process ID: %d , Thread ID: %d", label, android.os.Process.myPid(), Thread.currentThread().getId());
	}

	/**
	 * Prints an exception to logcat as well as the method name (not always accurate due to weird stack trace issues)
	 * This is a development level log method. Will only show during development. If error logs are required to be shown
	 * in a release application then Log.e should be used.
	 *
	 * @param label - Tag for the Android.util.log method
	 * @param error - the exception/error object used to access getStackTrace and toString used to print exception info
	 * @since 1.2
	 */
	public static void logException(String label, Throwable error) {
		if (sIsInDebugMode) {
			//ProcessAndThreadId(label);
			Log.e(label, error.getStackTrace()[1].getMethodName(), error);
			if (logToFile) {
				logToFile(label, error.getStackTrace()[1].getMethodName() + "|" + error.toString());
			}
		}
	}

	/**
	 * This is usd to allow verbose debugging during development. Log.d can be used also as it is stripped away at run
	 * time after release.
	 *
	 * @param label   - Label for logcat TAG
	 * @param message - Debug message text
	 * @since 1.2
	 */
	public static void logDebug(String label, String message) {
		if (sIsInDebugMode) {
			Log.v(label, message);
			if (logToFile) {
				logToFile(label, message);
			}
		}
	}

	/**
	 * Helper method that prepares the the intent to start the reporter service which saves the exception and sends it
	 * to an exception logging web service.
	 *
	 * @param context Used to start the service
	 * @param ex      The exception
	 * @param tag     A tag to help you determine where the error occurred
	 * @param fatal   Whether or not the exception was fatal
	 */
	public static void reportException(final Context context, final String tag, final Throwable ex, final boolean fatal) {
		try {
			final StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));

			final Intent i = new Intent(ErrorReportingService.INTENT);
			i.putExtra(ErrorReportingService.EXTRA_STACK_TRACE, sw.toString());
			i.putExtra(ErrorReportingService.EXTRA_FATAL, fatal);
			i.putExtra(ErrorReportingService.EXTRA_TAG, tag);

			context.startService(i);
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
		}
	}

	private static void logToFile(final String label, final String messageToLog) {
		try {
			String filePath = FileHelper.getRootFileDir() + "flow_log" + File.separator;
			File file = new File(filePath);
			if (file.exists() || file.mkdirs()) {


				File logfile = new File(filePath, "log.txt");
				if (logfile.exists() || logfile.createNewFile()) {

					FileWriter fileWriter = new FileWriter(logfile, true);
					try {
						fileWriter.write(ProcessAndThreadId(label));
						fileWriter.write(label + "|" + new Date().toString() + "|" + messageToLog);
						fileWriter.flush();
					} finally {
						fileWriter.close();
					}
				}
			}
		} catch (Exception e) {
			LogHelper.logException(TAG, e);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class SingletonHolder {
		public static final LogHelper instance = new LogHelper();
	}
}
