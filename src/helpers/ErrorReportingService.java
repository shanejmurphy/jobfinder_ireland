package helpers;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import org.json.JSONObject;


/**
 * @author Ruairi
 * @version 1
 * @since 18/04/12 - 14:03
 */

public class ErrorReportingService extends IntentService {

	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final String TAG = "ErrorReportingService";

	public static final String INTENT = "ErrorReportingServiceIntentKey";

	public static final String EXTRA_STACK_TRACE = "stacktrace";
	public static final String EXTRA_FATAL = "fatal";
	public static final String EXTRA_TAG = "tag";

	public ErrorReportingService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		LogHelper.logDebug(TAG, "onHandleIntent(intent=" + intent.toString() + ")");

		try {
			final String trace = intent.getStringExtra(EXTRA_STACK_TRACE);
			final String tag = intent.getStringExtra(EXTRA_TAG);
			final boolean fatal = intent.getBooleanExtra(EXTRA_FATAL, true);

			JSONObject error = new JSONObject();

			// String errordesc = "origin:" + origin + "; msg:" + msg + userString + "; url:" + url + "; lineNo:" + lineNo;

			error.put("errordesc ", trace);
			error.put("origin ", "Wrapper");
			error.put("model", Build.MODEL);
			error.put("fatal", Boolean.toString(fatal));
			error.put("tag", tag);

		} catch (final Exception e) {
			LogHelper.logException(TAG, e);
		}
	}
}
